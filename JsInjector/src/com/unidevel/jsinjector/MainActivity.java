package com.unidevel.jsinjector;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnCheckedChangeListener,View.OnClickListener, OnDrawerOpenListener, OnDrawerCloseListener
{

	@Override
	public void onClick(View p1)
	{
		String js=this.codeView.getText().toString();
		this.callJS(js);
	}

	WebView view;
	Handler handler;
	JavaScriptLibrary jsLib;
	Console console;
	EditText codeView;
	TextView consoleView;
	EditText urlView;
	View runBtn;
	ImageButton handleBtn;
	SlidingDrawer codeSection;
	View webContainer;
	LocalActivityManager localActMgr;
	boolean urlChanged = false;
	TabHost tabHost; 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.handler = new Handler();
		this.jsLib = new JavaScriptLibrary(this);
		this.consoleView=(TextView) this.findViewById(R.id.console);
		this.codeView = (EditText)this.findViewById( R.id.code );
		this.urlView = (EditText) this.findViewById(R.id.url);
		this.runBtn = this.findViewById(R.id.run);
		this.handleBtn = (ImageButton)this.findViewById( R.id.handle );
		this.console = new Console(consoleView);
		this.webContainer = this.findViewById( R.id.webContainer );
		this.codeSection = (SlidingDrawer)this.findViewById( R.id.codeSection );
		this.codeSection.setOnDrawerOpenListener( this );
		this.codeSection.setOnDrawerCloseListener( this );
		this.tabHost = (TabHost)this.findViewById( R.id.tabhost );
		this.localActMgr = new LocalActivityManager(MainActivity.this, false);
		this.localActMgr.dispatchCreate(savedInstanceState);

		this.tabHost.setup(this.localActMgr);	

		view = (WebView)this.findViewById(R.id.web);//new WebView(this);
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setAllowFileAccess(true);
		view.getSettings().setSupportZoom(false);
		final String base = "file:///android_asset/www/";
		view.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("url", url);
				if (url.startsWith("file:/") || url.startsWith("http:/")
						|| url.startsWith("https:/")) {
					return false;
				} else {
					url = base + "/" + url;
					view.loadUrl(url);
					return true;
				}
			}
		});
		WebChromeClient client = new WebChromeClient() {
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		};
		view.setWebChromeClient(client);
		view.addJavascriptInterface(this.jsLib, "unidevel");
		view.addJavascriptInterface(this.console, "console");
		view.loadDataWithBaseURL(base, getHtmlData("www/index.html", null),
				"text/html", null, null);
		//url.addTextChangedListener(this);
		Uri uri = getIntent().getData();
		if (uri != null) {
			openUrl(uri.toString());
		}
		ToggleButton code= (ToggleButton) this.findViewById(R.id.showCode);
		code.setOnCheckedChangeListener(this);
		this.runBtn.setOnClickListener(this);
		this.urlView.addTextChangedListener( new TextWatcher()
		{
			
			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count )
			{
				urlChanged = true;
			}
			
			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after )
			{
			}
			
			@Override
			public void afterTextChanged( Editable s )
			{
			}
		} );
		this.urlView.setOnFocusChangeListener( new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange( View v, boolean hasFocus )
			{
				if ( !hasFocus && urlChanged )
				{
					openUrl( urlView.getText().toString() );
				}
			}
		} );
	}
	
	public void setupTabs()
	{
		tabHost.setup();  
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Console").setContent(R.id.consoleContainer));  
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Code").setContent(R.id.codeContainer));  
	}
	
	public void openUrl(String url)
	{
		this.view.loadUrl(url);
		this.urlView.setText(url);
		this.urlChanged = false;
	}
	
	@Override
	protected void onPause() {
		this.localActMgr.dispatchPause( isFinishing() );
		String callback = this.jsLib.getEventCallback("pause");
		if  ( callback != null )
		{
			callJS(callback+"()");
		}
		super.onPause();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String code = this.codeView.getText().toString();
		String url = this.urlView.getText().toString();
		sp.edit().putString("code",code).putString( "url", url ).commit();
	}
	
	@Override
	protected void onResume() {
		this.localActMgr.dispatchResume();
		String callback = this.jsLib.getEventCallback("resume");
		if  ( callback != null )
		{
			callJS(callback+"()");
		}
		super.onResume();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		String code = sp.getString("code","");
		String url = sp.getString( "url", "" );
		this.codeView.setText(code);
		String currentUrl = this.urlView.getText().toString();
		if ( !url.equals( currentUrl ) )
		{
			//this.openUrl( currentUrl );
		}
	}

	public String getHtmlData(String assetPath, String encoding) {
		InputStream in = null;
		try {
			in = this.getAssets().open(assetPath);
			InputStreamReader reader;
			if (encoding != null)
				reader = new InputStreamReader(in, encoding);
			else
				reader = new InputStreamReader(in);
			StringBuffer sbuf = new StringBuffer();
			char[] buf = new char[8192];
			int len;
			for (len = reader.read(buf); len > 0; len = reader.read(buf)) {
				sbuf.append(buf, 0, len);
			}
			return sbuf.toString();
		} catch (IOException e) {
			try {
				in.close();
			} catch (Throwable ex) {
			}
		}
		return null;
	}

	public void callJS(final String javaScript) {
		this.handler.post(new Runnable() {
			public void run() {
				view.loadUrl("javascript:" + javaScript);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String callback = this.jsLib.getCallback(requestCode);
		if (callback != null) {
			if (callback.startsWith("image:")) {
				String function = callback.substring(6);
				String path = null;
				if(resultCode==RESULT_OK){
					path=getPath(data);
				}
				if (path!=null){
					path="'"+path+"'";
				}
				else{
					path="null";
				}
				Log.i("Image Path:", String.valueOf(path));
				callJS(function + "(" + path + ")");
			}
			this.jsLib.removeCallback(callback);
		}
	}

	public String getPath(Intent intent) {
		Uri selectedImageUri = intent.getData();

		String imagePath = null;

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			imagePath = cursor.getString(column_index);
		}

		// OI FILE Manager
		if (imagePath == null)
			imagePath = selectedImageUri.getPath();
		return imagePath;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && view.canGoBack()) {
			view.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckedChanged( CompoundButton button, boolean checked )
	{
		if ( checked )
		{
			this.codeView.setVisibility( View.VISIBLE );
			this.consoleView.setVisibility( View.GONE );
		}
		else
		{
			this.codeView.setVisibility( View.GONE );
			this.consoleView.setVisibility( View.VISIBLE );
		}
	}

	@Override
	public void onDrawerClosed()
	{
		this.handleBtn.setImageResource( android.R.drawable.arrow_up_float );
	}

	@Override
	public void onDrawerOpened()
	{
		this.handleBtn.setImageResource( android.R.drawable.arrow_down_float );
	}
}
