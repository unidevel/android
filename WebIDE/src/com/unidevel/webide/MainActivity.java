package com.unidevel.webide;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import android.util.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		WebView view=new WebView(this);
		final String base="file:///android_asset/www";
		view.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view,String url)
			{
				Log.i("url",url);
				if(url.startsWith("file:/")||url.startsWith("http:/")||url.startsWith("https:/")){
					return false;
				}
				else{
					url=base+"/"+url;
					view.loadUrl(url);
					return true;
				}
			}
		});
		view.loadUrl("file:///android_asset/www/index.html");
        setContentView(view);
    }
}
