package com.unidevel.tochrome;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;

public class ViewSourceActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Uri uri=getIntent().getData();
		if(uri!=null){
			this.finish();
			Uri newUri=Uri.parse("view-source:"+uri.toString());
			startChrome(newUri);
		}
    }

	public void startChrome(Uri uri){
		Intent it = new Intent(Intent.ACTION_VIEW);
		it.setData(uri);
		it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		it.setClassName("com.android.chrome","com.google.android.apps.chrome.Main");
		try{
			startActivity(it);
		}
		catch(ActivityNotFoundException ex){
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?id=com.android.chrome"));
			startActivity(intent);
		}
	}
}
