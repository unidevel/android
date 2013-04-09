
package com.unidevel.unshorturl;

import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener
{
	/** Called when the activity is first created. */
	ListView linkView;
	GridView appView;
	LoadLinksTask task;
	LoadAppTask appTask;
	AppAdapter appAdapter;
	ProgressBar progressBar;

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>>
	{

		@Override
		protected List<AppInfo> doInBackground( Void... params )
		{
			List<AppInfo> apps;
			Uri uri = getIntent().getData();
			String type = getIntent().getType();
			apps = findActivity( uri, type );
			return apps;
		}

		@Override
		protected void onPostExecute( List<AppInfo> result )
		{
			super.onPostExecute( result );
			MainActivity.this.appAdapter = new AppAdapter( MainActivity.this, result );
			MainActivity.this.appView.setAdapter( MainActivity.this.appAdapter );
		}
	}

	class LoadLinksTask extends AsyncTask<String, Integer, Void>
	{
		List<String> realLinks = new ArrayList<String>();

		@Override
		protected Void doInBackground( String... params )
		{
			List<String> links = new ArrayList<String>();
			links.add( params[ 0 ] );
			DefaultHttpClient client = new DefaultHttpClient();
			final HttpParams httpParams = new BasicHttpParams();
			HttpClientParams.setRedirecting( httpParams, false );
			client.setParams( httpParams );
			int n = 0;
			int total = 0;
			while ( links.size() > 0 )
			{
				n++;
				total += links.size();
				String url = links.remove( 0 );
				URI uri = URI.create( url );
				this.realLinks.add( url );
				if ( !isShort( uri ) )
				{
					findLinksInternal( links, uri );
					this.publishProgress( n );
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
							if ( isLink( value ) )
							{
								URI linkURI = URI.create( value );
								links.add( value );
								findLinksInternal( links, linkURI );
								this.publishProgress( n );
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
			this.publishProgress( 0 );
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			MainActivity.this.progressBar.setProgress( 5 );
			MainActivity.this.progressBar.setVisibility( View.GONE );
		}

		@Override
		protected void onProgressUpdate( Integer... values )
		{
			List<String> links = new ArrayList<String>();
			links.addAll( this.realLinks );
			LinkAdapter linkAdapter = new LinkAdapter( MainActivity.this, links );
			MainActivity.this.linkView.setAdapter( linkAdapter );
			int progress = values[ 0 ];
			if ( progress >= 4 )
				progress = 4;
			MainActivity.this.progressBar.setProgress( progress );
		}
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		requestWindowFeature( Window.FEATURE_LEFT_ICON );
		setTitle( R.string.app_name );
		setContentView( R.layout.main );
		setFeatureDrawableResource( Window.FEATURE_LEFT_ICON, R.drawable.link );

		super.onCreate( savedInstanceState );

		Uri uri = getIntent().getData();
		if ( uri == null )
		{
			this.finish();
			return;
		}

		this.linkView = (ListView)this.findViewById( R.id.listView1 );
		this.linkView.setItemsCanFocus( true );
		this.linkView.setFocusable( true );
		this.linkView.setFocusableInTouchMode( true );
		this.linkView.setChoiceMode( AbsListView.CHOICE_MODE_SINGLE );
		this.registerForContextMenu( this.linkView );
		this.linkView.setOnItemClickListener( this );

		this.appView = (GridView)this.findViewById( R.id.gridview );
		this.appView.setOnItemClickListener( new OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int pos, long id )
			{
				appAdapter.setSelected( pos );
			}
		} );

		this.progressBar = (ProgressBar)this.findViewById( R.id.progressBar1 );
		this.progressBar.setMax( 5 );

		final String url = uri.toString();

		this.appTask = new LoadAppTask();
		this.appTask.execute();

		this.task = new LoadLinksTask();
		this.task.execute( url );

		/*
		 * this.linkView = (ListView) this.findViewById(R.id.listView1);
		 * this.linkView.setItemsCanFocus(true);
		 * this.linkView.setFocusable(true);
		 * this.linkView.setFocusableInTouchMode(true);
		 * this.linkView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		 * this.registerForContextMenu(this.linkView);
		 * this.linkView.setOnItemClickListener(this);
		 * 
		 * // ActionBar bar=this.getActionBar(); //
		 * bar.setDisplayOptions(ActionBar
		 * .DISPLAY_SHOW_HOME,ActionBar.DISPLAY_SHOW_HOME); // ToChrome:
		 * a150eec940a7ef9 AdView adView = new AdView(this, AdSize.BANNER,
		 * "a150eec940a7ef9"); LinearLayout layout = (LinearLayout)
		 * findViewById(R.id.adLayout); layout.addView(adView); AdRequest req =
		 * new AdRequest(); adView.loadAd(req);
		 */
	}

	private void findLinksInternal( List<String> links, URI uri )
	{
		List<NameValuePair> pairs = URLEncodedUtils.parse( uri, "ISO8859-1" ); //$NON-NLS-1$
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
					Log.e( "findLinksInternal", ex.getMessage(), ex ); //$NON-NLS-1$
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
		if ( appAdapter == null )
			return;
		AppInfo app = appAdapter.getSelectedApp();
		if ( app == null )
			return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		pref.edit().putString( "package", app.packageName ).putString( "class", app.name ).commit(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void viewLink( String link )
	{
		if ( link == null || appAdapter == null )
		{
			return;
		}
		AppInfo app = appAdapter.getSelectedApp();
		if ( app == null )
		{
			Toast.makeText( this, R.string.select_a_browser, Toast.LENGTH_LONG ).show();
			return;
		}
		savePref();
		Uri data = null;
		data = Uri.parse( link );
		String type = MimeTypes.getType( link );
		if ( type == null )
		{
			type = "text/plain"; //$NON-NLS-1$
		}
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.addCategory( Intent.CATEGORY_DEFAULT );
		i.setClassName( app.packageName, app.name );
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
		}
		catch (Throwable ex)
		{
			Log.e( "tochrome", "viewLink:" + ex.getMessage(), ex ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
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
		this.finish();
	}

	public void clearDefault( String link )
	{
		if ( link == null || link.isEmpty() )
			return;
		Intent i = (new Intent( Intent.ACTION_VIEW, Uri.parse( link ) ));
		PackageManager pm = getPackageManager();
		ComponentName name = i.resolveActivity( pm );

		String pkg = name.getPackageName();
		if ( pkg != null && !"android".equals( pkg ) ) //$NON-NLS-1$
		{
			Intent intent = new Intent();
			intent.setAction( android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
			Uri uri = Uri.fromParts( "package", pkg, null ); //$NON-NLS-1$
			intent.setData( uri );
			startActivity( intent );
		}
		else
		{
			// String msg = String.format( getString( R.string.no_default ),
			// i.getData().getScheme() );
			// Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
		}
	}

	public List<AppInfo> findActivity( Uri uri, String type )
	{
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = this.getPackageManager();
		Intent intent = new Intent( Intent.ACTION_VIEW, null );
		intent.setDataAndType( uri, type );
		List<ResolveInfo> rList = pm.queryIntentActivities( intent, 0 );
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		String selectedPkg = pref.getString( "package", "" );
		String selectedName = pref.getString( "class", "" );
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
