package com.unidevel.unshorturl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;

public class ViewSourceActivity extends Activity
{
	String URL_PATTERN = "((https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = getIntent();
		Uri uri = intent.getData();
		if ( uri != null ) 
		{
			MainActivity.addLink(this, uri.toString());
			Uri newUri=Uri.parse("view-source:"+uri.toString());
			startChrome(newUri);				
		}
        if ( Intent.ACTION_SEND.equals(intent.getAction()))
        {
    		Bundle extras = intent.getExtras();
    		if ( extras != null )
			{
				String text = extras.getString(Intent.EXTRA_TEXT);
				Pattern pattern = Pattern.compile(URL_PATTERN);
				if(text!=null){
					Matcher m = pattern.matcher(text);
					if (m.find()) {
						String url = m.group(1);
						MainActivity.addLink(this, url);
						Uri newUri=Uri.parse("view-source:"+url);
						startChrome(newUri);
					}
				}
			}
        }
		this.finish();
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
