package com.unidevel.softkeyenabler;

import java.util.Map;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.webkit.WebView;

import com.unidevel.WebActivity;

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
        
        WebView view =new WebView(this);
        setContentView(view);
        setupWebView(view);
		try {
			extractAsset("www.zip", getFilesDir(), true);
			// extractJQM();
			// copyAssets("www", getFilesDir(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// try {
		// view.loadDataWithBaseURL(getFilesDir().toURL().toString(),
		// this.getHtmlData(appFile("www/test.html"), null),
		// "text/html", null, null);
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		view.loadUrl(appFile("www/test.html").toURI().toString());
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
