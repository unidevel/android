package com.unidevel.softkeyenabler;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.webkit.WebView;

import com.unidevel.WebActivity;
import com.unidevel.util.WebUtil;

public class MainActivity extends WebActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WebView view =new WebView(this);
        setContentView(view);
        setupWebView(view);
        extractJQM();
        view.loadUrl(WebUtil.makeUri(this,"www/index.html").toString());
//        WebUtil.browse(view, WebUtil.makeUri(this,"www/index.html"));
//        setContentView(R.layout.main);
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
