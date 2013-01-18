package com.unidevel.tochrome;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.ads.*;
import java.util.*;
import android.text.*;

public class MainActivity extends Activity implements OnItemClickListener
{
    /** Called when the activity is first created. */
	ListView linkView;
	LinkAdapter linkAdapter;
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
		this.linkView.setItemsCanFocus(true);
		this.linkView.setFocusable(true);
		this.linkView.setFocusableInTouchMode(true);
		this.linkView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
//		this.linkView.setOnItemClickListener(this);
//        Button btn=(Button)this.findViewById(R.id.clear);
//		btn.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View p1)
//				{
//					clearDefault();
//				}
//		});
		
		//ActionBar bar=this.getActionBar();
		//bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME,ActionBar.DISPLAY_SHOW_HOME);
        //ToChrome: a150eec940a7ef9  
        AdView adView = new AdView(this, AdSize.BANNER, "a150eec940a7ef9"); 
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout); 
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);
    }

	static final int ACT_DELETE=100;
	static final int ACT_VIEW=101;
	static final int ACT_CLEAR_DEFAULT=102;
	
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);

		MenuItem item=menu.add(Menu.NONE,ACT_DELETE,Menu.NONE,R.string.delete);
		item.setIcon(android.R.drawable.ic_menu_delete);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		item=menu.add(Menu.NONE,ACT_VIEW,Menu.NONE,R.string.view);
		item.setIcon(android.R.drawable.ic_menu_directions);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		item=menu.add(Menu.NONE,ACT_CLEAR_DEFAULT,Menu.NONE,R.string.clear_default);
		item.setIcon(android.R.drawable.ic_menu_manage);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId()==ACT_DELETE){
			deleteLink();
		}
		else if(item.getItemId()==ACT_VIEW){
			viewLink();
		}
		else if(item.getItemId()==ACT_CLEAR_DEFAULT){
			clearDefault();
		}
		else
			return super.onOptionsItemSelected(item);
		return true;
	}

	private void deleteLink()
	{
		this.linkAdapter.deleteSelected();
		List<String> links=this.linkAdapter.getLinks();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = prefs.edit();
		int i;
		for (i = 1; i <= links.size() && i <= 25; ++ i)
		{
			Log.i("tochrome", "link" + i + ":" + links.get(i - 1));
			edit.putString("link" + i, links.get(i - 1));
		}
		for (; i <= 25; ++ i)
		{
			edit.putString("link"+i,null);
		}
		edit.commit();
	}

	private void viewLink()
	{
		String link = this.linkAdapter.getSelectedLink();
		if ( link == null ) {
			return;
		}		
		Intent i=new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(link));
		startActivity(i);
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
		this.linkAdapter = new LinkAdapter(this,links);
    	this.linkView.setAdapter(this.linkAdapter);//(new ArrayAdapter<String>(this, R.layout.item, links));
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
	/*	TextView textView = (TextView)view;
		String link = textView.getText().toString();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		intent.setData(Uri.parse(link));
		startActivity(intent);
		*/
	//	this.linkAdapter.setSelected(position);
		TextView text=(TextView)view;
		text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		text.setMarqueeRepeatLimit(-1);
		text.setSelected(true);
		text.setSingleLine();
	//	view.setSelected(true);
	}
	
	public void clearDefault(){
		String link = this.linkAdapter.getSelectedLink();
		if ( link == null ) {
			Toast.makeText(this,"null",3).show();
			return;
		}
		if ( link == null || link.isEmpty()) return;
		Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
		PackageManager pm = getPackageManager();
		ComponentName name= i.resolveActivity(pm);
		final ResolveInfo mInfo = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
		//pm.getApplicationLabel(mInfo.activityInfo.applicationInfo), Toast.LENGTH_LONG).show();
		String pkg=name.getPackageName();
		if(pkg!=null&&!"android".equals(pkg))
		{
			Intent intent = new Intent();
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", pkg, null);
			intent.setData(uri);
			startActivity(intent);
		}
		else
		{
			String msg=String.format(getString(R.string.no_default),i.getData().getScheme());
			Toast.makeText(this,msg,3).show();
		}
	}
}
