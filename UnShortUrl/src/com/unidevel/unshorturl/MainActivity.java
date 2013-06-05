
package com.unidevel.unshorturl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends Activity implements OnItemClickListener, Runnable
{
	static final int SO_TIMEOUT = 5000;
	static final int CONNECT_TIMEOUT = 5000;
	static String URL_PATTERN = "((https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])"; //$NON-NLS-1$

	@SuppressWarnings ("unused")
	public void run()
	{
		if ( false && !this.canceled && this.appLoaded && this.linkLoaded && this.realLinks.size() > 0 )
		{
			String url = this.realLinks.get( this.realLinks.size() - 1 );
			finish();
			viewLink( url );
		}
	}

	/** Called when the activity is first created. */
	ListView linkView;
	GridView appView;
	LoadLinksTask linkTask;
	LoadAppTask appTask;
	CreateDeskLinkTask deskTask;
	AppAdapter appAdapter;
	ProgressBar progressBar;
	Handler handler;
	boolean appLoaded = false;
	boolean linkLoaded = false;
	boolean canceled = false;
	String mainUrl = null;
	String mainType = null;

	String starUrl = null;
	List<String> realLinks = new ArrayList<String>();

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>>
	{
		public void run(){
			List<AppInfo> apps=doInBackground();
			onPostExecute(apps);
		}
		
		@Override
		protected List<AppInfo> doInBackground( Void... params )
		{
			List<AppInfo> apps;
			Uri uri;

			if ( MainActivity.this.mainUrl == null )
			{
				uri = Uri.parse( "http://www.google.com/" ); //$NON-NLS-1$
				MainActivity.this.mainType = "text/html"; //$NON-NLS-1$
			}
			else
			{
				uri = Uri.parse( MainActivity.this.mainUrl );
			}
			apps = findActivity( uri, MainActivity.this.mainType );
			return apps;
		}

		@SuppressWarnings ("synthetic-access")
		@Override
		protected void onPostExecute( List<AppInfo> result )
		{
			super.onPostExecute( result );
			MainActivity.this.appAdapter = new AppAdapter( MainActivity.this, result );
			MainActivity.this.appView.setAdapter( MainActivity.this.appAdapter );
			int sel = MainActivity.this.appAdapter.getSelected();
			if ( sel >= 0 )
			{
				MainActivity.this.appView.smoothScrollToPosition( sel );
				MainActivity.this.appLoaded = true;
			//	MainActivity.this.handler.postDelayed( MainActivity.this, 3000 );
			}

			if ( MainActivity.this.mainUrl != null && !MainActivity.this.canceled )
			{
				MainActivity.this.linkTask = new LoadLinksTask();
				MainActivity.this.linkTask.execute( MainActivity.this.mainUrl );
			}

			initAd();
		}
	}

	class LoadLinksTask extends AsyncTask<String, Integer, Void>
	{

		@Override
		protected Void doInBackground( String... params )
		{
			List<String> links = new ArrayList<String>();
			links.add( params[ 0 ] );
			DefaultHttpClient client = new DefaultHttpClient();
			final BasicHttpParams httpParams = new BasicHttpParams();
			HttpClientParams.setRedirecting( httpParams, false );
			HttpConnectionParams.setSoTimeout( httpParams, SO_TIMEOUT );
			HttpConnectionParams.setConnectionTimeout( httpParams, CONNECT_TIMEOUT );
			client.setParams( httpParams );
			int n = 0;
			@SuppressWarnings ("unused")
			int total = 0;
			while ( links.size() > 0 && !MainActivity.this.canceled )
			{
				n++;
				total += links.size();
				String url = links.remove( 0 );
				if ( MainActivity.this.realLinks.contains( url ) )
				{
					continue;
				}
				url=url.trim();
				Log.i("URI.create",url);
				URI uri = URI.create( url );
				MainActivity.this.realLinks.add( url );
				this.publishProgress( n );
				if ( !isShort( uri ) )
				{
					findLinksInternal( links, uri );
					continue;
				}

				HttpGet request = new HttpGet( url );
				HttpResponse response;
				try
				{
					response = client.execute( request );
					StatusLine status = response.getStatusLine();
					if ( status.getStatusCode() >= 300 && status.getStatusCode() < 400 )
					{
						for ( Header header : response.getAllHeaders() )
						{
							String value = header.getValue();
							if ( !MainActivity.this.canceled && isLink( value ) )
							{
								URI linkURI = URI.create( value );
								links.add( value );
								findLinksInternal( links, linkURI );
								//this.publishProgress( n );
							}
						}
					}
				}
				catch (Throwable e)
				{
					Log.e( "LoadLinks", e.getMessage(), e ); //$NON-NLS-1$
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			MainActivity.this.progressBar.setProgress( 5 );
			MainActivity.this.progressBar.setVisibility( View.GONE );
			if ( MainActivity.this.realLinks != null && MainActivity.this.realLinks.size() > 0 )
			{
				MainActivity.this.linkView.setSelection( MainActivity.this.realLinks.size() - 1 );
			}
			MainActivity.this.linkLoaded = true;
			// MainActivity.this.handler.postDelayed( MainActivity.this, 3000 );
		}

		@Override
		protected void onProgressUpdate( Integer... values )
		{
			List<String> links = new ArrayList<String>();
			links.addAll( MainActivity.this.realLinks );
			final LinkAdapter linkAdapter = new LinkAdapter( MainActivity.this, MainActivity.this.starUrl, links );
			linkAdapter.setOnStarClickListener( new LinkAdapter.StarClickListener()
			{
				public void onClick( String url, boolean hasStar )
				{
					if ( hasStar )
					{
						deleteLink( url );
					}
					else
					{
						MainActivity.this.deskTask = new CreateDeskLinkTask();
						MainActivity.this.deskTask.execute( url );
						linkAdapter.setStarUrl(url);
					}
				}
			} );
			MainActivity.this.linkView.setAdapter( linkAdapter );
			int progress = values[ 0 ];
			if ( progress >= 4 )
				progress = 4;
			MainActivity.this.progressBar.setProgress( progress );
		}
	}

	class CreateDeskLinkTask extends AsyncTask<String, Void, Void> implements OnCancelListener
	{
		ProgressDialog progressDialog;
		String url;
		String title;
		byte[] icon;

		protected Void doInBackground( String... args )
		{
			this.title = args[ 0 ];
			this.url = this.title;
			StringBuffer buf = new StringBuffer();
			try
			{
				String encoding = null;
				URL urlLink = new URL( this.url );
				URL favLink = new URL( urlLink.getProtocol(), urlLink.getHost(), urlLink.getPort(), "/favicon.ico" ); //$NON-NLS-1$
				HttpURLConnection conn = (HttpURLConnection)urlLink.openConnection();
				conn.setConnectTimeout( CONNECT_TIMEOUT );
				conn.setReadTimeout( SO_TIMEOUT );
				// conn.setDoOutput(true);
				conn.setRequestMethod( "GET" ); //$NON-NLS-1$
				conn.setRequestProperty( "User-Agent", //$NON-NLS-1$
						"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1" ); //$NON-NLS-1$
				InputStream in = conn.getInputStream();
				encoding = conn.getContentEncoding();

				InputStreamReader reader = null;
				if ( encoding == null )
					reader = new InputStreamReader( in, "ISO8859-1" ); //$NON-NLS-1$
				else
					reader = new InputStreamReader( in, encoding );
				int len;
				char cbuf[] = new char[ 1024 ];
				String htmlEncoding = null;
				Pattern pattern = Pattern.compile( "<title>([^<]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE ); //$NON-NLS-1$
				Pattern charsetPattern = Pattern.compile( "\\bcharset\\s*=\\s*\"?([^\"]+)\"" ); //$NON-NLS-1$
				while ( (len = reader.read( cbuf )) > 0 )
				{
					if ( encoding == null )
					{
						if ( htmlEncoding == null )
						{
							String s = new String( cbuf );
							Matcher m = charsetPattern.matcher( s );
							if ( m.find() )
							{
								htmlEncoding = m.group( 1 ).trim();
								buf = new StringBuffer();
								buf.append( new String( s.getBytes( "ISO8859-1" ), htmlEncoding ) ); //$NON-NLS-1$
							}
						}
					}
					if ( encoding != null )
						buf.append( cbuf, 0, len );
					else
					{
						if ( htmlEncoding != null )
						{
							buf.append( new String( new String( cbuf ).getBytes( "ISO8859-1" ), htmlEncoding ) ); //$NON-NLS-1$
						}
						else
						{
							buf.append( new String( new String( cbuf ).getBytes( "ISO8859-1" ) ) ); //$NON-NLS-1$
						}
					}
					String body = buf.toString();
					Matcher m = pattern.matcher( body );
					if ( m.find() )
					{
						this.title = m.group( 1 ).trim();
						int pos = this.title.indexOf( '\n' );
						if ( pos >= 0 )
							this.title = this.title.substring( 0, pos ).trim();
						break;
					}
				}
				in.close();
				conn.disconnect();

				conn = (HttpURLConnection)favLink.openConnection();
				conn.setConnectTimeout( 5000 );
				conn.setReadTimeout( 5000 );
				conn.setDoOutput( true );
				conn.setRequestMethod( "GET" ); //$NON-NLS-1$
				conn.setRequestProperty( "User-Agent", //$NON-NLS-1$
						"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1" ); //$NON-NLS-1$
				in = conn.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] data = new byte[ 1024 ];
				while ( (len = in.read( data )) > 0 )
				{
					out.write( data, 0, len );
				}
				in.close();
				out.close();
				conn.disconnect();

				this.icon = out.toByteArray();
			}
			catch (Throwable ex)
			{
				Log.e( "ToDesktop", ex.getMessage(), ex ); //$NON-NLS-1$
			}

			return null;
		}

		protected void onCancelled()
		{
			this.progressDialog.cancel();
			createLink( this.url, this.title, this.icon );
		}

		protected void onPreExecute()
		{
			super.onPreExecute();
			this.progressDialog =
					ProgressDialog.show( MainActivity.this, null, getString( R.string.desk_link ), true, true );
			this.progressDialog.setOnCancelListener( this );
		}

		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			this.progressDialog.dismiss();
			createLink( this.url, this.title, this.icon );
		}

		public void createLink( final String url, String title, byte[] icon )
		{
			if(url==null)return;
			Bitmap bitmap = null;
			if ( !url.equals( title ) || icon != null )
			{
				try
				{
					bitmap = BitmapFactory.decodeResource( getResources(), android.R.drawable.btn_star_big_on );
					bitmap = bitmap.copy( Bitmap.Config.ARGB_8888, true );
					Canvas canvas = new Canvas( bitmap );
					Paint paint = new Paint();
					int x = bitmap.getWidth() / 2;
					int y = bitmap.getHeight() / 2;
					int w = bitmap.getWidth() * 2 / 5;
					int h = bitmap.getHeight() * 2 / 5;

					Bitmap favicon = BitmapFactory.decodeByteArray( icon, 0, icon.length );
					// canvas.drawBitmap(bitmap, w, h, paint);
					canvas.drawBitmap( favicon, new Rect( 0, 0, favicon.getWidth(), favicon.getHeight() ), new Rect( x,
							y, x + w, y + h ), paint );
					Log.i( "unidevel", "using site icon" ); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (Throwable ex)
				{
					Log.w( "unidevel", ex.getMessage(), ex ); //$NON-NLS-1$
				}
				finally
				{
				}
			}

			if ( bitmap == null )
			{
				bitmap =
						((BitmapDrawable)getResources().getDrawable( android.R.drawable.btn_star_big_off )).getBitmap();
				Log.i( "unidevel", "using default icon" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			Log.i( "unidevel", "url=" + url ); //$NON-NLS-1$ //$NON-NLS-2$
			Log.i( "unidevel", "title=" + title ); //$NON-NLS-1$ //$NON-NLS-2$

			Intent shortcutIntent = new Intent( Intent.ACTION_VIEW );
			Uri uri = Uri.parse( url );
			shortcutIntent.setData( uri );
			shortcutIntent.putExtra( getPackageName(), true );
			Intent createIntent = new Intent();
			createIntent.putExtra( Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent );
			createIntent.putExtra( Intent.EXTRA_SHORTCUT_NAME, title );
			if ( bitmap != null )
			{
				createIntent.putExtra( Intent.EXTRA_SHORTCUT_ICON, bitmap );
			}
			createIntent.setAction( "com.android.launcher.action.INSTALL_SHORTCUT" ); //$NON-NLS-1$
			//	createIntent.setAction( "com.android.launcher.action.UNINSTALL_SHORTCUT" ); //$NON-NLS-1$
			MainActivity.this.sendBroadcast( createIntent );
		}

		@Override
		public void onCancel( DialogInterface dialog )
		{
			this.cancel( true );
		}
	}

	public void deleteLink( final String url )
	{
		if ( url == null )
			return;

		Intent shortcutIntent = new Intent( Intent.ACTION_VIEW );
		Uri uri = Uri.parse( url );
		shortcutIntent.setData( uri );
		shortcutIntent.putExtra( getPackageName(), true );
		Intent deleteIntent = new Intent();
		deleteIntent.putExtra( Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent );
		deleteIntent.setAction( "com.android.launcher.action.UNINSTALL_SHORTCUT" ); //$NON-NLS-1$
		MainActivity.this.sendBroadcast( deleteIntent );
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.handler = new Handler();

		Uri uri = getIntent().getData();

		requestWindowFeature( Window.FEATURE_LEFT_ICON );
		if ( uri == null )
		{
			setTitle( R.string.settings );
		}
		else
		{
			setTitle( R.string.app_name );
		}

		if ( Intent.ACTION_VIEW.equals( getIntent().getAction() ) && uri != null )
		{
			this.mainUrl = uri.toString();
			this.mainType = getIntent().getType();
			if ( this.mainType == null || this.mainType.length() == 0 )
			{
				this.mainType = MimeTypes.getType( this.mainUrl );
			}
		}
		else if ( Intent.ACTION_SEND.equals( getIntent().getAction() ) )
		{
			Bundle extras = getIntent().getExtras();
			Pattern pattern = Pattern.compile( URL_PATTERN );
			for ( Iterator<String> it = extras.keySet().iterator(); it.hasNext(); )
			{
				Object key = it.next();
				Object value = extras.get( key.toString() );
				String sValue = value == null
						? "" //$NON-NLS-1$
						: value.toString();
				Matcher m = pattern.matcher( sValue );
				if ( m.find() )
				{
					this.mainUrl = m.group( 1 );
					this.mainType = MimeTypes.getType( this.mainUrl );
				}
			}
		}
		else
		{
			this.mainUrl = null;
			this.mainType = null;
		}

		setContentView( R.layout.main );
		setFeatureDrawableResource( Window.FEATURE_LEFT_ICON, R.drawable.link );
		
		viewAd();
		Button buyButton = (Button)this.findViewById( R.id.buy );
		buyButton.setVisibility( View.GONE );

		this.linkView = (ListView)this.findViewById( R.id.listView1 );
		this.linkView.setItemsCanFocus( true );
		this.linkView.setFocusable( true );
		this.linkView.setFocusableInTouchMode( true );
		this.linkView.setChoiceMode( AbsListView.CHOICE_MODE_SINGLE );
		this.registerForContextMenu( this.linkView );
		this.linkView.setOnItemClickListener( this );
		this.linkView.setOnItemLongClickListener( new OnItemLongClickListener()
		{

			public boolean onItemLongClick( AdapterView<?> adapterView, View view, int pos, long id )
			{

				TextView text = (TextView)view.findViewById( android.R.id.text1 );
				final String link = text.getText().toString();
				AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
				builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick( DialogInterface dialog, int p2 )
					{
						dialog.dismiss();
					}

				} );
				builder.setMessage( link ).setPositiveButton( android.R.string.copy,
						new DialogInterface.OnClickListener()
						{
							public void onClick( DialogInterface dialog, int p2 )
							{
								putText( link );
								Toast.makeText( MainActivity.this, R.string.copied, Toast.LENGTH_LONG ).show();
							}

						} );
				builder.create().show();
				return false;
			}

		} );
		this.appView = (GridView)this.findViewById( R.id.gridview );
		this.appView.setOnItemClickListener( new OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int pos, long id )
			{
				MainActivity.this.appAdapter.setSelected( pos );
				savePref();
			}
		} );

		this.progressBar = (ProgressBar)this.findViewById( R.id.progressBar1 );
		this.progressBar.setMax( 5 );

		this.appTask = new LoadAppTask();

		
		if ( this.mainUrl != null )
		{
			Bundle extras = getIntent().getExtras();
			if ( extras != null )
			{
				boolean fromSelf = extras.getBoolean( getPackageName(), false );
				Log.i( "from", "" + fromSelf ); //$NON-NLS-1$ //$NON-NLS-2$
				if ( fromSelf )
				{
					this.starUrl = this.mainUrl;
				}
			}
			List<String> links = new ArrayList<String>();
			links.add( this.mainUrl );
			this.linkView.setAdapter( new LinkAdapter( this, null, links ) );
		}
		else
		{
			int m = LayoutParams.MATCH_PARENT;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( m, m );
			this.appView.setLayoutParams( params );
			this.progressBar.setVisibility( View.GONE );
			clearDefault( "https://www.google.com" ); //$NON-NLS-1$
			clearDefault( "http://www.google.com" ); //$NON-NLS-1$
		}
	}

	public void viewAd(){
		Uri uri = getIntent().getData();
		if(uri!=null){
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
			String pkg = pref.getString( "package", "" ); //$NON-NLS-1$ //$NON-NLS-2$
			String clazz = pref.getString( "class", "" ); //$NON-NLS-1$ //$NON-NLS-2$
			
			if(pkg!=null){
				if ( "www.googleadservices.com".equalsIgnoreCase( uri.getHost() ) ) { //$NON-NLS-1$
					viewLink(uri.toString(),pkg,clazz);
				}
				else if ( "googleadservices.com".equalsIgnoreCase( uri.getHost() ) ) { //$NON-NLS-1$
					viewLink(uri.toString(),pkg,clazz);
				}
				else if ( "googleads.g.doubleclick.net".equalsIgnoreCase( uri.getHost() ) ) { //$NON-NLS-1$
					viewLink(uri.toString(),pkg,clazz);		
				}
			}
		}
	}
	
	private void initAd()
	{
		AdView adView = new AdView( this, AdSize.BANNER, "a151640e221df04" ); //$NON-NLS-1$
		LinearLayout layout = (LinearLayout)findViewById( R.id.adLayout );
		layout.addView( adView );
		AdRequest req = new AdRequest();
		adView.loadAd( req );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		this.appTask.run();//.execute();
	}

	protected void findLinksInternal( List<String> links, URI uri )
	{		
		List<NameValuePair> pairs = null;
		try 
		{
			pairs = URLEncodedUtils.parse( uri, "ISO8859-1" ); //$NON-NLS-1$
		}
		catch(Throwable ex)
		{
			Log.e( "findLinksInternal.parse", uri.toString(), ex ); //$NON-NLS-1$
			return;
		}
		for ( NameValuePair pair : pairs )
		{
			String value = pair.getValue();
			if ( value != null && isLink( value ) )
			{
				try
				{
					String link = URLDecoder.decode( value, "ISO8859-1" ); //$NON-NLS-1$
					links.add( link );
				}
				catch (Throwable ex)
				{
					Log.e( "findLinksInternal.decode", uri.toString(), ex ); //$NON-NLS-1$
				}
			}
		}
	}

	public boolean isShort( URI uri )
	{
		if ( uri == null )
			return false;
		if ( !"http".equals( uri.getScheme() ) && !"https".equals( uri.getScheme() ) ) //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		if ( uri.getHost().length() > 9 )
			return false;
		if ( uri.getRawQuery() != null && uri.getRawQuery().length() > 0 )
			return false;
		if ( uri.getRawPath() != null && uri.getRawPath().length() > 12 )
			return false;
		return true;
	}

	public boolean isLink( String value )
	{
		if ( value == null )
			return false;
		if ( value.startsWith( "http://" ) ) //$NON-NLS-1$
			return true;
		if ( value.startsWith( "https://" ) ) //$NON-NLS-1$
			return true;
		if ( value.startsWith( "file://" ) ) //$NON-NLS-1$
			return true;
		if ( value.startsWith( "ftp://" ) ) //$NON-NLS-1$
			return true;
		return false;
	}

	public boolean onCreateOptiondMenu( Menu menu )
	{
		super.onCreateOptionsMenu( menu );
		return true;
	}

	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo info )
	{
		super.onCreateContextMenu( menu, v, info );
	}

	public boolean onContextItemSelected( MenuItem item )
	{
		return true;
	}

	protected void savePref()
	{
		if ( this.appAdapter == null )
			return;
		AppInfo app = this.appAdapter.getSelectedApp();
		if ( app == null )
			return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		pref.edit().putString( "package", app.packageName ).putString( "class", app.name ).commit(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void viewLink( String link )
	{
		if ( link == null || this.appAdapter == null )
		{
			return;
		}
		AppInfo app = this.appAdapter.getSelectedApp();
		if ( app == null )
		{
			Toast.makeText( this, R.string.select_a_browser, Toast.LENGTH_LONG ).show();
			return;
		}
		viewLink(link, app.packageName, app.name);
	}
	
	private void viewLink( String link , String pkg, String clazz)
	{
		if (pkg==null)
			return;
		Uri data = null;
		data = Uri.parse( link );
		String type = MimeTypes.getType( link );
		if ( type == null )
		{
			type = "text/plain"; //$NON-NLS-1$
		}
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.addCategory( Intent.CATEGORY_DEFAULT );
		i.setClassName( pkg, clazz);
		if ( link.startsWith( "file://" ) ) //$NON-NLS-1$
		{
			i.setDataAndType( data, type );
		}
		else
		{
			i.setData( data );
		}
		try
		{
			startActivity( i );
			this.finish();
		}
		catch (Throwable ex)
		{
			Log.e( "tochrome", "viewLink:" + ex.getMessage(), ex ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		this.canceled = true;
		Log.i( "onPause", "onPause" ); //$NON-NLS-1$ //$NON-NLS-2$
		this.finish();
	}

	@Override
	protected void onDestroy()
	{
		Log.i( "onDestroy", "onDestroy" ); //$NON-NLS-1$ //$NON-NLS-2$
		super.onDestroy();
		if ( this.deskTask != null )
		{
			this.deskTask.cancel( true );
			this.deskTask = null;
		}
		if ( this.linkTask != null )
		{
			this.linkTask.cancel( true );
			this.linkTask = null;
		}
		if ( this.appTask != null )
		{
			this.appTask.cancel( true );
			this.appTask = null;
		}
	}

	public static void addLink( Context context, String newLink )
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		List<String> links = new ArrayList<String>();
		links.add( newLink );
		for ( int i = 1; i <= 25; ++i )
		{
			String link = prefs.getString( "link" + i, null ); //$NON-NLS-1$
			if ( link != null && link.trim().length() > 0 && !links.contains( link ) )
				links.add( link );
		}
		Editor edit = prefs.edit();
		for ( int i = 1; i <= links.size() && i <= 25; ++i )
		{
			edit.putString( "link" + i, links.get( i - 1 ) ); //$NON-NLS-1$
		}
		edit.commit();
	}

	@Override
	public void onItemClick( AdapterView<?> adapterView, View view, int position, long id )
	{
		TextView text = (TextView)view.findViewById( android.R.id.text1 );
		String link = text.getText().toString();
		viewLink( link );
	}

	@TargetApi (Build.VERSION_CODES.GINGERBREAD)
	public void clearDefault( String link )
	{
		if ( link == null || link.length() == 0 )
			return;
		Intent i = (new Intent( Intent.ACTION_VIEW, Uri.parse( link ) ));
		PackageManager pm = getPackageManager();
		ComponentName name = i.resolveActivity( pm );

		String pkg = name.getPackageName();
		if ( pkg != null && !"android".equals( pkg ) ) //$NON-NLS-1$
		{
			if ( this.getPackageName().equals( pkg ) )
				return;
			Intent intent = new Intent();
			intent.setAction( android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
			Uri uri = Uri.fromParts( "package", pkg, null ); //$NON-NLS-1$
			intent.setData( uri );
			startActivity( intent );
			Toast.makeText( this, R.string.clear_default, Toast.LENGTH_LONG ).show();
		}
	}

	public List<AppInfo> findActivity( Uri uri, String type )
	{
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = this.getPackageManager();
		Intent intent = new Intent( Intent.ACTION_VIEW, null );
		if ( "file".equalsIgnoreCase( uri.getScheme() ) ) //$NON-NLS-1$
		{
			intent.setDataAndType( uri, type );
		}
		else
		{
			intent.setData( uri );
		}
		List<ResolveInfo> rList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> acts = pm.queryIntentActivities( intent, 0 );
		rList.addAll( acts );
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		String selectedPkg = pref.getString( "package", "" ); //$NON-NLS-1$ //$NON-NLS-2$
		String selectedName = pref.getString( "class", "" ); //$NON-NLS-1$ //$NON-NLS-2$
		if ( "file".equals( uri.getScheme() ) ) //$NON-NLS-1$
		{
			intent = new Intent();
			intent.setClassName( "com.android.chrome", //$NON-NLS-1$
					"com.google.android.apps.chrome.Main" ); //$NON-NLS-1$
			acts = pm.queryIntentActivities( intent, 0 );
			if ( acts != null && acts.size() > 0 )
				rList.addAll( acts );

			intent = new Intent();
			intent.setClassName( "com.chrome.beta", //$NON-NLS-1$
					"com.google.android.apps.chrome.Main" ); //$NON-NLS-1$
			acts = pm.queryIntentActivities( intent, 0 );
			if ( acts != null && acts.size() > 0 )
				rList.addAll( acts );
		}

		for ( ResolveInfo r : rList )
		{
			if ( this.getPackageName().equals( r.activityInfo.packageName ) )
				continue;
			AppInfo app = new AppInfo();
			apps.add( app );
			app.packageName = r.activityInfo.packageName;
			app.name = r.activityInfo.name;
			app.icon = r.activityInfo.loadIcon( pm );
			if ( app.icon == null )
			{
				app.icon = getResources().getDrawable( R.drawable.link );
			}
			CharSequence label = r.activityInfo.loadLabel( pm );
			if ( label == null )
			{
				app.label = ""; //$NON-NLS-1$
			}
			else
			{
				app.label = label.toString();
			}
			if ( selectedPkg.equals( app.packageName ) )
			{
				if ( (app.name == null && selectedName.length() == 0) || selectedName.equals( app.name ) )
				{
					app.selected = true;
				}
			}
		}
		return apps;
	}

	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings ("deprecation")
	public void putText( String text )
	{
		int sdk = android.os.Build.VERSION.SDK_INT;
		if ( sdk < android.os.Build.VERSION_CODES.HONEYCOMB )
		{
			android.text.ClipboardManager clipboard =
					(android.text.ClipboardManager)this.getSystemService( Context.CLIPBOARD_SERVICE );
			clipboard.setText( text );
		}
		else
		{
			android.content.ClipboardManager clipboard =
					(android.content.ClipboardManager)this.getSystemService( Context.CLIPBOARD_SERVICE );
			android.content.ClipData clip = ClipData.newPlainText( "simple text", text ); //$NON-NLS-1$
			clipboard.setPrimaryClip( clip );
		}
	}

	class LinkException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LinkException( String msg )
		{
			super( msg );
		}
	}
}
