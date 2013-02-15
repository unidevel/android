package com.unidevel.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unidevel.www.JavaScriptLibrary;
import com.unidevel.www.MimeTypes;

public class WebUtil
{
	public static boolean isPublished(Context ctx)
	{
		File f = new File(ctx.getFilesDir(), "www/lib/jquery.mobile.js");
		return f.exists();
	}

	public static Uri makeUri(Context ctx, String relativePath)
	{
		File f = new File(ctx.getFilesDir(), relativePath);
		Uri uri = Uri.fromFile(f);
		return uri;
	}
	
	public static void publish(Context ctx) {
		try {
			InputStream in = ctx.getAssets().open("www.zip");
			File dir = ctx.getFilesDir();
			ZipUtil.extract(in, dir);
		} catch (IOException e) {
			Log.e("WebUtil", e.getMessage(), e);
		}
	}
	
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
	
	public static void setup(Activity activity, WebView view)
	{
		setup(view,"unidevel",new JavaScriptLibrary(activity));
	}
	
	public static void addJsObject(WebView view, String jsObjectName, Object jsInterface)
	{
		if(jsInterface!=null&&jsObjectName!=null){
			view.addJavascriptInterface(jsInterface, jsObjectName);
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public static void setup(WebView view, String jsObjectName, Object jsInterface)
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
