package com.unidevel.combocalc;

import java.util.Map;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.webkit.WebView;

import com.unidevel.WebActivity;
import com.unidevel.util.*;
import android.view.*;
import android.webkit.*;

public class MainActivity extends WebActivity {

	@Override
	protected void onCreateJavaScriptObjects(Map<String, Object> jsObjects) {
		super.onCreateJavaScriptObjects(jsObjects);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//this.debug = true;
        //RootUtil.run("ls");
        WebView view =new WebView(this);
        setContentView(view);
        setupWebView(view);
		view.clearCache(true);
		view.getSettings().setAppCacheEnabled(false);
		view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		view.setSystemUiVisibility(View.VISIBLE);
		try {
			extractAsset("www.zip", getFilesDir(), true);
		} catch (Exception e) {
			alert("Error", e.getMessage(),e);
		}
		//view.loadUrl(appFile("www/index.html").toURI().toString());
    	view.loadUrl("file:///sdcard/wcalc/index.html");
    }
}
