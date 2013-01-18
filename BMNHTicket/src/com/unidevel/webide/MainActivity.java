package com.unidevel.webide;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import java.io.*;

public class MainActivity extends Activity {
	WebView view;
	Handler handler;
	JavaScriptLibrary jsLib;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler();
		this.jsLib = new JavaScriptLibrary(this);
		view = new WebView(this);
		setContentView(view);
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
		view.loadDataWithBaseURL(base, getHtmlData("www/index.html", null),
				"text/html", null, null);
	}
	
	@Override
	protected void onPause() {
		String callback = this.jsLib.getEventCallback("pause");
		if  ( callback != null )
		{
			callJS(callback+"()");
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		String callback = this.jsLib.getEventCallback("resume");
		if  ( callback != null )
		{
			callJS(callback+"()");
		}
		super.onResume();
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
}
