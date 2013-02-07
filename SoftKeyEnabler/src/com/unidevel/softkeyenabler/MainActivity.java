package com.unidevel.softkeyenabler;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
