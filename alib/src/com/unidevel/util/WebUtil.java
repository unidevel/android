package com.unidevel.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebUtil
{
	public static void browse(WebView view,String filePath)
	{
		String htmlText = 
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public static void browse(WebView view,String basePath, String htmlText, String encoding, String mimeType, String jsObjectName, Object jsInterface)
	{
		view.getContext().getFilesDir();
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setAllowFileAccess(true);
		view.getSettings().setSupportZoom(false);
		view.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("url", url);
				return false;
			}
		});
		WebChromeClient client = new WebChromeClient() {
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		};
		view.setWebChromeClient(client);
		view.addJavascriptInterface(jsInterface, jsObjectName);
		view.loadDataWithBaseURL(basePath, htmlText, mimeType, encoding, null);		
	}
}
