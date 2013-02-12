package com.unidevel.util;

import android.annotation.*;
import android.content.*;
import android.net.*;
import android.util.*;
import android.webkit.*;

public class WebUtil
{

	public static void viewLink(Context ctx, String link) {
		if (link == null) {
			return;
		}
		Uri data = null;
		data = Uri.parse(link);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		if ( link.startsWith("file://") )
		{
			String type = MimeTypes.getType(link);
			if (type == null) {
				type = "text/plain";
			}
			i.setDataAndType(data, type);
		}
		else
		{
			i.setData(data);
		}
		ctx.startActivity(i);
	}
	
	public static void browse(WebView view,Uri uri)
	{
		browse(view,uri);
	}
	
	public static void browse(WebView view,Uri uri, String jsObjectName, Object jsInterface)
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
		if(jsInterface!=null&&jsObjectName!=null){
			view.addJavascriptInterface(jsInterface, jsObjectName);
		}
		view.loadUrl(uri.toString());
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
