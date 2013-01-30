package com.unidevel.tochromebeta;

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
			MainActivity.startChrome(this, newUri);				
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
						MainActivity.startChrome(this, newUri);
					}
				}
			}
        }
		this.finish();
    }
}
