
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener
{
	/** Called when the activity is first created. */
	ListView linkView;
	LinkAdapter linkAdapter;
	Handler handler;
	LoadLinksTask task;
	ProgressBar progressBar;

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
				realLinks.add( url );
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
					Log.e( "LoadLinks", e.getMessage(), e );
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
			progressBar.setProgress( 5 );
			progressBar.setVisibility(View.GONE);
		}

		@Override
		protected void onProgressUpdate( Integer... values )
		{
			List<String> links = new ArrayList<String>();
			links.addAll( realLinks );
			LinkAdapter linkAdapter = new LinkAdapter( MainActivity.this, links );
			linkView.setAdapter( linkAdapter );
			int progress = values[ 0 ];
			if ( progress >= 4 )
				progress = 4;
			progressBar.setProgress( progress );
		}
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		this.setTitle( R.string.app_name );
		super.onCreate( savedInstanceState );
		Uri uri = getIntent().getData();
		if ( uri == null )
		{
			this.finish();
			return;
		}

		setContentView( R.layout.main );
		this.linkView = (ListView)this.findViewById( R.id.listView1 );
		this.linkView.setItemsCanFocus( true );
		this.linkView.setFocusable( true );
		this.linkView.setFocusableInTouchMode( true );
		this.linkView.setChoiceMode( AbsListView.CHOICE_MODE_SINGLE );
		this.registerForContextMenu( this.linkView );
		this.linkView.setOnItemClickListener( this );

		this.progressBar = (ProgressBar)this.findViewById( R.id.progressBar1 );
		this.progressBar.setMax( 5 );

		this.handler = new Handler();
		final String url = uri.toString();

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

	private void viewLink( String link )
	{
		if ( link == null )
		{
			return;
		}
		Uri data = null;
		data = Uri.parse( link );
		String type = MimeTypes.getType( link );
		if ( type == null )
		{
			type = "text/plain"; //$NON-NLS-1$
		}
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.addCategory( Intent.CATEGORY_DEFAULT );
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
		TextView text = (TextView)view;
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
			String msg = String.format( getString( R.string.no_default ), i.getData().getScheme() );
			Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
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
