package com.unidevel.webide;

import android.app.*;
import android.os.*;
import android.util.*;
import android.webkit.*;
import java.io.*;

public class MainActivity extends Activity {
	WebView view;
	Handler handler;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler=new Handler();
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
		WebChromeClient client=new WebChromeClient(){
			public boolean onJsAlert(WebView view,String url, String message, JsResult result)
			{
				return super.onJsAlert(view,url,message,result);
			}
		};
		view.setWebChromeClient(client);
		view.addJavascriptInterface(new JavaScriptLibrary(this),"unidevel");
		view.loadDataWithBaseURL(base, getHtmlData("www/index.html", null), "text/html", null, null);
	}

	public String getHtmlData(String assetPath, String encoding) {
		InputStream in = null;
		try {
			in = this.getAssets().open(assetPath);
			InputStreamReader reader ;
			if ( encoding!=null)
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
	
	public void callJS(final String javaScript){
		this.handler.post(new Runnable(){

				public void run()
				{
					view.loadUrl("javascript:"+javaScript);
				}
		});
	}
}
