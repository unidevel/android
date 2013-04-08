package com.unidevel.unshorturl;

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
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;

public class MainActivity extends Activity implements OnItemClickListener {
	/** Called when the activity is first created. */
	ListView linkView;
	LinkAdapter linkAdapter;
	TextView text;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = getIntent().getData();
		/*
		if (uri != null) {
			addLink(this, uri.toString());
			startChrome(uri);
			this.finish();
			return;
		}
		*/
		setContentView(R.layout.main);
		text=(TextView)this.findViewById(R.id.text);
		unshortUrl(uri.toString());
		/*
		this.linkView = (ListView) this.findViewById(R.id.listView1);
		this.linkView.setItemsCanFocus(true);
		this.linkView.setFocusable(true);
		this.linkView.setFocusableInTouchMode(true);
		this.linkView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.registerForContextMenu(this.linkView);
		this.linkView.setOnItemClickListener(this);

		// ActionBar bar=this.getActionBar();
		// bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME,ActionBar.DISPLAY_SHOW_HOME);
		// ToChrome: a150eec940a7ef9
		AdView adView = new AdView(this, AdSize.BANNER, "a150eec940a7ef9");
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);
		*/
	}
	
	public void unshortUrl(String url){
		HttpClient client=new DefaultHttpClient();
		final HttpParams params = new BasicHttpParams();
		HttpClientParams.setRedirecting(params, false);
		text.setText("headers:");
		HttpGet get= new HttpGet(url);
		try {
			HttpResponse r= client.execute(get);
			text.setText("status:"+r.getStatusLine()+"\n");
			for(Header h:r.getAllHeaders()){
				text.append(h.getName()+":"+h.getValue()+"\n");
			}
		}
		catch(Exception e){
			text.setText(e.toString());
		}
	}

	static final int ACT_DELETE = 100;
	static final int ACT_VIEW = 101;
	static final int ACT_CLEAR_DEFAULT = 102;
	static final int ACT_VIEW_IN_CHROME = 103;
	static final int ACT_VIEW_SOURCE = 104;

	public boolean onCreateOptiondMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(Menu.NONE, ACT_DELETE, Menu.NONE,
				R.string.settings);
		item.setIcon(android.R.drawable.ic_menu_preferences);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);
		MenuItem item = menu.add(Menu.NONE, ACT_DELETE, Menu.NONE,
				R.string.delete);
		item.setIcon(android.R.drawable.ic_menu_delete);
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		item = menu.add(Menu.NONE, ACT_VIEW, Menu.NONE, R.string.view);
		item.setIcon(android.R.drawable.ic_menu_directions);
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		item = menu.add(Menu.NONE, ACT_CLEAR_DEFAULT, Menu.NONE,
				R.string.clear_default);
		item.setIcon(android.R.drawable.ic_menu_manage);
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item = menu.add(Menu.NONE, ACT_VIEW_IN_CHROME, Menu.NONE,
				R.string.app_name);
		item = menu
				.add(Menu.NONE, ACT_VIEW_SOURCE, Menu.NONE, R.string.vs_name);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		TextView text = (TextView) info.targetView;
		String link = text.getText().toString();
		if (item.getItemId() == ACT_DELETE) {
			deleteLink(link);
		} else if (item.getItemId() == ACT_VIEW) {
			viewLink(link);
		} else if (item.getItemId() == ACT_VIEW_IN_CHROME) {
			Uri uri = Uri.parse(link);
			startChrome(uri);
		} else if (item.getItemId() == ACT_VIEW_SOURCE) {
			Uri uri = Uri.parse("view-source:" + link);
			startChrome(uri);
		} else if (item.getItemId() == ACT_CLEAR_DEFAULT) {
			clearDefault(link);
		} else
			return super.onContextItemSelected(item);
		return true;
	}

	private void deleteLink(String link) {
		this.linkAdapter.deleteLink(link);
		List<String> links = this.linkAdapter.getLinks();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = prefs.edit();
		int i;
		for (i = 1; i <= links.size() && i <= 25; ++i) {
			Log.i("tochrome", "link" + i + ":" + links.get(i - 1));
			edit.putString("link" + i, links.get(i - 1));
		}
		for (; i <= 25; ++i) {
			edit.putString("link" + i, null);
		}
		edit.commit();
	}

	private void viewLink(String link) {
		if (link == null) {
			return;
		}
		Uri data = null;
		data = Uri.parse(link);
		String type = MimeTypes.getType(link);
		if (type == null) {
			type = "text/plain";
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		if ( link.startsWith("file://") )
		{
			i.setDataAndType(data, type);
		}
		else
		{
			i.setData(data);
		}
		try 
		{
			startActivity(i);
		}
		catch(Throwable ex)
		{
			Log.e("tochrome", "viewLink:"+ex.getMessage(), ex);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		List<String> links = new ArrayList<String>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		for (int i = 1; i <= 25; ++i) {
			String link = prefs.getString("link" + i, null);
			if (link != null && link.trim().length() > 0
					&& !links.contains(link))
				links.add(link);
		}
		this.linkAdapter = new LinkAdapter(this, links);
		this.linkView.setAdapter(this.linkAdapter);// (new
													// ArrayAdapter<String>(this,
													// R.layout.item, links));
													*/
	}

	public void startChromeInternal(String pkgName, String clsName, Uri uri) {
		Intent it = new Intent(Intent.ACTION_VIEW);
		it.setData(uri);
		it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		it.setClassName(pkgName,clsName);
		try {
			startActivity(it);
		} catch (ActivityNotFoundException ex) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://search?id="+pkgName));
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, R.string.install_chrome, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public void startChrome(Uri uri) {
		startChromeInternal("com.android.chrome",
						"com.google.android.apps.chrome.Main",uri);
	}
	
	public static void addLink(Context context, String newLink) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		List<String> links = new ArrayList<String>();
		links.add(newLink);
		for (int i = 1; i <= 25; ++i) {
			String link = prefs.getString("link" + i, null);
			if (link != null && link.trim().length() > 0
					&& !links.contains(link))
				links.add(link);
		}
		Editor edit = prefs.edit();
		for (int i = 1; i <= links.size() && i <= 25; ++i) {
			edit.putString("link" + i, links.get(i - 1));
		}
		edit.commit();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		TextView text = (TextView) view;
		String link = text.getText().toString();
		viewLink(link);
	}

	public void clearDefault(String link) {
		if (link == null || link.isEmpty())
			return;
		Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
		PackageManager pm = getPackageManager();
		ComponentName name = i.resolveActivity(pm);
		// final ResolveInfo mInfo = pm.resolveActivity(i,
		// PackageManager.MATCH_DEFAULT_ONLY);
		// pm.getApplicationLabel(mInfo.activityInfo.applicationInfo),
		// Toast.LENGTH_LONG).show();
		String pkg = name.getPackageName();
		if (pkg != null && !"android".equals(pkg)) {
			Intent intent = new Intent();
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", pkg, null);
			intent.setData(uri);
			startActivity(intent);
		} else {
			String msg = String.format(getString(R.string.no_default), i
					.getData().getScheme());
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
	}
}
