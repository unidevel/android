package com.unidevel;

import android.app.Activity;
import android.os.Bundle;

import com.unidevel.util.DialogUtil;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final DialogUtil util = new DialogUtil(this);
        util.prompt("Test", new DialogUtil.OnPromptCallback() {
			
			@Override
			public void onResult(String value) {
				util.toast(value);
			}
		});
        	
        System.err.println("Finally");
    }
}
