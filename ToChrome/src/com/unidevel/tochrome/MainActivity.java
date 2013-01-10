package com.unidevel.tochrome;

import java.util.ArrayList;
import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener
{
    /** Called when the activity is first created. */
	ListView linkView;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		Uri uri=getIntent().getData();
		if(uri!=null){
			addLink(this, uri.toString());
			startChrome(uri);
			this.finish();
			return;
		}
        setContentView(R.layout.main);
        this.linkView = (ListView) this.findViewById(R.id.listView1);
        this.linkView.setOnItemClickListener(this);
        
        //ToChrome: a150eec940a7ef9  
        AdView adView = new AdView(this, AdSize.BANNER, "a150eec940a7ef9"); 
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout); 
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	List<String> links = new ArrayList<String>();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		for ( int i = 1; i <= 25; ++ i )
		{
			String link = prefs.getString("link"+i, null);
			if ( link!=null && link.trim().length()>0 && !links.contains(link))
				links.add(link);
		}
    	this.linkView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, links));
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

	public static void addLink(Context context, String newLink)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		List<String> links = new ArrayList<String>();
		links.add(newLink);
		for ( int i = 1; i <= 25; ++ i )
		{
			String link = prefs.getString("link"+i, null);
			if ( link!=null && link.trim().length()>0 && !links.contains(link))
				links.add(link);
		}
		Editor edit = prefs.edit();
		for ( int i = 1; i <= links.size() && i<=25; ++ i )
		{
			edit.putString("link"+i, links.get(i-1));
		}
		edit.commit();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		TextView textView = (TextView)view;
		String link = textView.getText().toString();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		intent.setData(Uri.parse(link));
		startActivity(intent);
	}
}
