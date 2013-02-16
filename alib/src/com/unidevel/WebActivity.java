package com.unidevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.unidevel.www.JavaScriptLibrary;

public abstract class WebActivity extends BaseActivity {
	private WebView webView;
	protected Handler handler;
	private Map<String, Object> jsObjects;
	protected JavaScriptLibrary jsLib;
	private MenuWrapper optionMenuObject;

	public static final String EVENT_RESUME = "onResume";
	public static final String EVENT_PAUSE = "onPause";
	public static final String EVENT_PREPARE_OPTION_MENU = "onPrepareOptionMenu";
	public static final String EVENT_CLICK_OPTION_MENU = "onClickOptionMenu";

	public static final String VAR_UNIDEVEL = "unidevel";
	public static final String VAR_OPTION_MENU = "_internal_option_menu";

	protected void onCreateJavaScriptObjects(Map<String, Object> jsObjects) {
		this.jsLib = new JavaScriptLibrary(this);
		jsObjects.put(VAR_UNIDEVEL, this.jsLib);
		this.optionMenuObject = new MenuWrapper();
		jsObjects.put(VAR_OPTION_MENU, this.optionMenuObject);
	}

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
		this.jsObjects = new HashMap<String, Object>();
		this.onCreateJavaScriptObjects(jsObjects);
		for (String name : this.jsObjects.keySet()) {
			view.addJavascriptInterface(this.jsObjects.get(name), name);
		}
	}

	protected void extractJQM() throws IOException {
		extractAsset("www.zip", this.getFilesDir(), true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler();
	}

	@Override
	protected void onPause() {
		String callback = this.getJsCallback(EVENT_PAUSE);
		if (callback != null) {
			callJS(callback + "()");
		}
		super.onPause();
	}

	protected String getJsCallback(String event) {
		if (this.jsLib != null) {
			return this.jsLib.getEventCallback(event);
		}
		return null;
	}

	@Override
	protected void onResume() {
		String callback = getJsCallback(EVENT_RESUME);
		if (callback != null) {
			callJS(callback + "()");
		}
		super.onResume();
	}

	public String getHtmlData(File file, String encoding) throws IOException {
		FileInputStream in = new FileInputStream(file);
		try {
			return getHtmlData(in, encoding);
		} finally {
			try {
				in.close();
			} catch (Throwable ex) {
			}
		}
	}

	public String getHtmlData(InputStream in, String encoding)
			throws IOException {
		try {
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
			e(e.getMessage(), e);
			throw e;
		}
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
		if (this.jsLib != null) {
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
					i(String.valueOf(path));
					callJS(function + "(" + path + ")");
				}
				this.jsLib.removeCallback(callback);
			}
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

	public static class MenuWrapper {
		Menu menu;

		public MenuWrapper() {

		}

		public void setMenu(Menu menu) {
			this.menu = menu;
		}

		public void add(int groupdId, int itemId, String title) {
			this.menu.add(groupdId, itemId, 0, title);
		}

		public void add(int id, String title) {
			add(0, id, title);
		}

		public MenuWrapper addSubMenu(String title) {
			SubMenu subMenu = this.menu.addSubMenu(title);
			MenuWrapper subMenuWrapper = new MenuWrapper();
			subMenuWrapper.setMenu(subMenu);
			return subMenuWrapper;
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		String callback = this.getJsCallback(EVENT_PREPARE_OPTION_MENU);
		if (callback != null && this.optionMenuObject != null) {
			menu.clear();
			this.optionMenuObject.setMenu(menu);
			callJS(callback + "(" + VAR_OPTION_MENU + ")");
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String callback = this.getJsCallback(EVENT_CLICK_OPTION_MENU);
		if (callback != null) {
			callJS(callback + "(" + item.getItemId() + ")");
		}
		return super.onOptionsItemSelected(item);
	}
}
