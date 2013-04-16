
package com.unidevel.book.jquerymobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends Activity
{
	WebView view;
	Handler handler;
	JavaScriptLibrary jsLib;
	LinearLayout adLayout = null;
	String sourceLink = null;
	final String BASE_URL = "file:///android_asset/demos/1.2.0/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.handler = new Handler();
		this.jsLib = new JavaScriptLibrary( this );
		setContentView( R.layout.main );
		view = (WebView)this.findViewById( R.id.webView );
		view.getSettings().setJavaScriptEnabled( true );
		view.getSettings().setAllowFileAccess( true );
		view.getSettings().setSupportZoom( false );
		view.setWebViewClient( new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading( WebView view, String url )
			{
				Log.i( "override url", url );
				if ( url.startsWith( "file:/" ) || url.startsWith( "http:/" ) || url.startsWith( "https:/" ) )
				{
					return false;
				}
				else
				{
					url = BASE_URL + url;
					view.loadUrl( url );
					return true;
				}
			}

			@Override
			public void onLoadResource( WebView view, String url )
			{
				Log.i( "load url", url );

				super.onLoadResource( view, url );
			}
		} );

		WebChromeClient client = new WebChromeClient()
		{
			public boolean onJsAlert( WebView view, String url, String message, JsResult result )
			{
				return super.onJsAlert( view, url, message, result );
			}
		};
		view.setWebChromeClient( client );
		view.addJavascriptInterface( this.jsLib, "unidevel" );
		view.loadDataWithBaseURL( BASE_URL + "index.html", getHtmlData( "demos/1.2.0/index.html", null ), "text/html",
				null, BASE_URL + "index.html" );
	}

	public static final int MENU_VIEW_SOURCE = 1001;

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		menu.add( 0, MENU_VIEW_SOURCE, 0, R.string.view_source );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		if ( item.getItemId() == MENU_VIEW_SOURCE )
		{
			sourceLink = view.getUrl();
			callJS( "var s=document.documentElement.outerHTML; s=s.replace(/</g, '&lt;'); document.body.innerHTML='<pre>'+s+'</pre>';" );
			this.findViewById( R.id.adLayout ).setVisibility( View.VISIBLE );
			if ( adLayout == null )
			{
				AdView adView = new AdView( this, AdSize.BANNER, "a15166706d2ac13" );
				adLayout = (LinearLayout)findViewById( R.id.adLayout );
				adLayout.addView( adView );
				AdRequest req = new AdRequest();
				adView.loadAd( req );
			}
			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	public String getHtmlData( String assetPath, String encoding )
	{
		InputStream in = null;
		try
		{
			in = this.getAssets().open( assetPath );
			InputStreamReader reader;
			if ( encoding != null )
				reader = new InputStreamReader( in, encoding );
			else
				reader = new InputStreamReader( in );
			StringBuffer sbuf = new StringBuffer();
			char[] buf = new char[ 8192 ];
			int len;
			for ( len = reader.read( buf ); len > 0; len = reader.read( buf ) )
			{
				sbuf.append( buf, 0, len );
			}
			return sbuf.toString();
		}
		catch (IOException e)
		{
			try
			{
				in.close();
			}
			catch (Throwable ex)
			{
			}
		}
		return null;
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if ( (keyCode == KeyEvent.KEYCODE_BACK) )
		{
			if ( view.canGoBack() )
			{
				if ( sourceLink == null )
				{
					view.goBack();
				}
				else
				{
					view.loadUrl( sourceLink );
					this.findViewById( R.id.adLayout ).setVisibility( View.GONE );
					sourceLink = null;
				}
			}
			else
			{
				if ( sourceLink != null )
				{
					view.loadUrl( sourceLink );
					this.findViewById( R.id.adLayout ).setVisibility( View.GONE );
					sourceLink = null;
				}
				else
				{
					this.finish();
				}
			}
			return true;
		}
		return super.onKeyDown( keyCode, event );
	}

	public void callJS( final String javaScript )
	{
		this.handler.post( new Runnable()
		{
			public void run()
			{
				view.loadUrl( "javascript:" + javaScript );
			}
		} );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		String callback = this.jsLib.getCallback( requestCode );
		if ( callback != null )
		{
			if ( callback.startsWith( "image:" ) )
			{
				String function = callback.substring( 6 );
				String path = null;
				if ( resultCode == RESULT_OK )
				{
					path = getPath( data );
				}
				if ( path != null )
				{
					path = "'" + path + "'";
				}
				else
				{
					path = "null";
				}
				Log.i( "Image Path:", String.valueOf( path ) );
				callJS( function + "(" + path + ")" );
			}
			this.jsLib.removeCallback( callback );
		}
	}

	public String getPath( Intent intent )
	{
		Uri selectedImageUri = intent.getData();

		String imagePath = null;

		String[] projection = {
			MediaStore.Images.Media.DATA
		};
		Cursor cursor = managedQuery( selectedImageUri, projection, null, null, null );
		if ( cursor != null )
		{
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
			cursor.moveToFirst();
			imagePath = cursor.getString( column_index );
		}

		// OI FILE Manager
		if ( imagePath == null )
			imagePath = selectedImageUri.getPath();
		return imagePath;
	}
}
