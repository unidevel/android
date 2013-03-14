package com.unidevel.softkeyenabler;

import java.util.Map;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.webkit.WebView;

import com.unidevel.WebActivity;
import com.unidevel.util.*;
import android.view.*;

public class MainActivity extends WebActivity {
	BuildPropFile buildPropFile;

	@Override
	protected void onCreateJavaScriptObjects(Map<String, Object> jsObjects) {
		super.onCreateJavaScriptObjects(jsObjects);
		this.buildPropFile = new BuildPropFile(this);
		jsObjects.put("prop", this.buildPropFile);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//this.debug = true;
        //RootUtil.run("ls");
        WebView view =new WebView(this);
        setContentView(view);
        setupWebView(view);
		view.setSystemUiVisibility(View.VISIBLE);
		try {
			extractAsset("www.zip", getFilesDir(), true);
		} catch (Exception e) {
			alert("Error", e.getMessage(),e);
		}
		//view.loadUrl(appFile("www/index.html").toURI().toString());
    	view.loadUrl("file:///sdcard/www/index.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public int getDPI(){
    	DisplayMetrics metrics = new DisplayMetrics();
    	this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	return metrics.densityDpi;
    }
}
