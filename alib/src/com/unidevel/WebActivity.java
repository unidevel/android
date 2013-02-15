package com.unidevel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.unidevel.util.ZipUtil;
import com.unidevel.www.JavaScriptLibrary;

public abstract class WebActivity extends BaseActivity {
	WebView webView;
	Handler handler;
	JavaScriptLibrary jsLib;

	@SuppressLint("SetJavaScriptEnabled")
	public void setupWebView(WebView view) {
		this.webView = view;
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setAllowFileAccess(true);
		view.getSettings().setSupportZoom(false);

		WebChromeClient client = new WebChromeClient() {
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		};
		view.setWebChromeClient(client);
		view.addJavascriptInterface(this.jsLib, "unidevel");
	}

	public void extractJQM() {
		File dir = this.getFilesDir();
		File f = new File(this.getFilesDir(), "www/lib/jquery.mobile.js");
		if (!f.exists()) {
			try {
				InputStream in = this.getAssets().open("www.zip");
				ZipUtil.extract(in, dir);
			} catch (IOException e) {
				Log.e("WebActivity", e.getMessage(), e);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.jsLib = new JavaScriptLibrary(this);
		this.handler = new Handler();
	}

	@Override
	protected void onPause() {
		String callback = this.jsLib.getEventCallback("pause");
		if (callback != null) {
			callJS(callback + "()");
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		String callback = this.jsLib.getEventCallback("resume");
		if (callback != null) {
			callJS(callback + "()");
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
				webView.loadUrl("javascript:" + javaScript);
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
				if (resultCode == RESULT_OK) {
					path = getPath(data);
				}
				if (path != null) {
					path = "'" + path + "'";
				} else {
					path = "null";
				}
				Log.i("Image Path:", String.valueOf(path));
				callJS(function + "(" + path + ")");
			}
			this.jsLib.removeCallback(callback);
		}
	}

	@SuppressWarnings("deprecation")
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
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
