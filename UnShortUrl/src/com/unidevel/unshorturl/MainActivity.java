
package com.unidevel.unshorturl;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.ads.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.*;
import org.apache.http.client.utils.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import java.nio.*;

public class MainActivity extends Activity implements OnItemClickListener,Runnable
{

	public void run()
	{
		if (false && !canceled && appLoaded && linkLoaded && realLinks.size() > 0)
		{
			String url=realLinks.get(realLinks.size() - 1);
			finish();
			viewLink(url);
		}
	}

	/** Called when the activity is first created. */
	ListView linkView;
	GridView appView;
	LoadLinksTask task;
	LoadAppTask appTask;
	CreateDeskLinkTask deskTask;
	AppAdapter appAdapter;
	ProgressBar progressBar;
	Handler handler;
	boolean appLoaded=false;
	boolean linkLoaded=false;
	boolean canceled=false;
	List<String> realLinks = new ArrayList<String>();

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>>
	{

		@Override
		protected List<AppInfo> doInBackground(Void... params)
		{
			List<AppInfo> apps;
			Uri uri = getIntent().getData();
			String type;
			if (uri != null)
			{
				type = getIntent().getType();
			}
			else
			{
				uri = Uri.parse("http://www.googke.com/");
				type = "text/html";
			}
			apps = findActivity(uri, type);
			return apps;
		}

		@Override
		protected void onPostExecute(List<AppInfo> result)
		{
			super.onPostExecute(result);
			MainActivity.this.appAdapter = new AppAdapter(MainActivity.this, result);
			MainActivity.this.appView.setAdapter(MainActivity.this.appAdapter);
			int sel=appAdapter.getSelected();
			if (sel >= 0)
			{
				appView.smoothScrollToPosition(sel);
				appLoaded = true;
				handler.postDelayed(MainActivity.this, 3000);
			}
			//.setSelection(sel);
		}
	}

	class LoadLinksTask extends AsyncTask<String, Integer, Void>
	{

		@Override
		protected Void doInBackground(String... params)
		{
			List<String> links = new ArrayList<String>();
			links.add(params[0]);
			DefaultHttpClient client = new DefaultHttpClient();
			final HttpParams httpParams = new BasicHttpParams();
			HttpClientParams.setRedirecting(httpParams, false);
			client.setParams(httpParams);
			int n = 0;
			int total = 0;
			while (links.size() > 0)
			{
				n++;
				total += links.size();
				String url = links.remove(0);
				URI uri = URI.create(url);
				realLinks.add(url);
				if (!isShort(uri))
				{
					findLinksInternal(links, uri);
					this.publishProgress(n);
					continue;
				}

				HttpGet request = new HttpGet(url);
				HttpResponse response;
				try
				{
					response = client.execute(request);
					StatusLine status = response.getStatusLine();
					if (status.getStatusCode() >= 300 && status.getStatusCode() < 400)
					{
						for (Header header : response.getAllHeaders())
						{
							String value = header.getValue();
							if (isLink(value))
							{
								URI linkURI = URI.create(value);
								links.add(value);
								findLinksInternal(links, linkURI);
								this.publishProgress(n);
							}
						}
					}
				}
				catch (Throwable e)
				{
					Log.e("LoadLinks", e.getMessage(), e); //$NON-NLS-1$
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			this.publishProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			MainActivity.this.progressBar.setProgress(5);
			MainActivity.this.progressBar.setVisibility(View.GONE);
			if (realLinks != null && realLinks.size() > 0)
			{
				linkView.setSelection(realLinks.size() - 1);
			}
			linkLoaded = true;
			handler.postDelayed(MainActivity.this, 3000);
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			List<String> links = new ArrayList<String>();
			links.addAll(realLinks);
			LinkAdapter linkAdapter = new LinkAdapter(MainActivity.this, links);
			linkAdapter.setOnStarClickListener(new LinkAdapter.StarClickListener(){

					public void onClick(String url)
					{
						deskTask=new CreateDeskLinkTask();
						deskTask.execute(url);
					}			
			});
			MainActivity.this.linkView.setAdapter(linkAdapter);
			int progress = values[0];
			if (progress >= 4)
				progress = 4;
			MainActivity.this.progressBar.setProgress(progress);
		}
	}

	class CreateDeskLinkTask extends AsyncTask<String, Void, Void>
	{
		ProgressDialog progDlg;
		String url;
		String title ;
		byte[] icon;
		protected Void doInBackground(String[] args)
		{
			title = args[0];
			url=title;
			StringBuffer buf = new StringBuffer();
			try
			{
				String encoding = null;
				URL urlLink = new URL(url);
				URL favLink = new URL(urlLink.getProtocol(), urlLink.getHost(), urlLink.getPort(), "/favicon.ico");
				HttpURLConnection conn = (HttpURLConnection) urlLink
					.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.setDoOutput(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent",
										"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
				InputStream in = conn.getInputStream();
				encoding = conn.getContentEncoding();

				InputStreamReader reader = null;
				if (encoding == null) reader = new InputStreamReader(in, "ISO8859-1");
				else reader = new InputStreamReader(in, encoding);
				int len;
				char cbuf[] = new char[1024];
				String htmlEncoding = null;
				Pattern pattern = Pattern.compile("<title>([^<]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Pattern charsetPattern = Pattern.compile("\\bcharset\\s*=\\s*\"?([^\"]+)\"");
				while ((len = reader.read(cbuf)) > 0)
				{
					if (encoding == null)
					{
						if (htmlEncoding == null)
						{
							String s = new String(cbuf);
							Matcher m = charsetPattern.matcher(s);
							if (m.find())
							{
								htmlEncoding = m.group(1).trim();
								String s2 = buf.toString();
								buf = new StringBuffer();
								buf.append(new String(s.getBytes("ISO8859-1"), htmlEncoding));
							}
						}
					}
					if (encoding != null)
						buf.append(cbuf, 0, len);
					else
					{
						buf.append(new String(new String(cbuf).getBytes("ISO8859-1"), htmlEncoding));
					}
					String body = buf.toString();
					Matcher m = pattern.matcher(body);
					if (m.find())
					{
						title = m.group(1).trim();
						int pos = title.indexOf('\n');
						if (pos >= 0)title = title.substring(0, pos).trim();
						break;
					}
				}
				in.close();
				conn.disconnect();

				conn = (HttpURLConnection)favLink.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.setDoOutput(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent",
										"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
				in = conn.getInputStream();
				ByteArrayOutputStream out=new ByteArrayOutputStream();
				byte[] data = new byte[1024];
				while ((len = in.read(data)) > 0)
				{
					out.write(data, 0, len);
				}
				in.close();
				out.close();
				conn.disconnect();

				icon = out.toByteArray();
			}
			catch (Throwable ex)
			{
				Log.e("ToDesktop", ex.getMessage(), ex);
			}

			return null;
		}

		protected void onCancelled()
		{
			progDlg.cancel();
			createLink(url,title,icon);
		}

		protected void onPreExecute()
		{
			super.onPreExecute();
			progDlg = ProgressDialog.show(MainActivity.this, getString(R.string.desk_link), null, true, true);
		}

		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			progDlg.dismiss();
			createLink(url,title,icon);
		}

		public void createLink(final String url, String title, byte[] icon)
		{
			Bitmap bitmap = null;
			if (icon != null && icon.length > 0)
			{
				try
				{
					bitmap = BitmapFactory.decodeByteArray(icon,0,icon.length);
					Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.link);
					source = source.copy(Bitmap.Config.ARGB_8888, true);
					Canvas canvas = new Canvas(source);
					Paint paint = new Paint();
					int x = source.getWidth() * 4 / 7;
					int y = source.getHeight() * 4 / 7;
					int w = source.getWidth() / 4;
					int h = source.getHeight() / 4;
					
//							canvas.drawBitmap(bitmap, w, h, paint);
					canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(x, y, x + w, y + h), paint);
					Log.i("unidevel", "using site icon");
				}
				catch (Throwable ex)
				{
					Log.w("unidevel", ex.getMessage(), ex);
				}
				finally
				{
				}
			}
			
			if(bitmap==null){
				bitmap=((BitmapDrawable)getResources().getDrawable(R.drawable.link)).getBitmap();
				Log.i("unidevel", "using default icon");
			}
			Log.i("unidevel", "url="+url);
			Log.i("unidevel", "title="+title);
				
			Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(url);
			shortcutIntent.setData(uri);
			Intent createIntent = new Intent();
			createIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			createIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			if(bitmap!=null)
			{
				createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
			}
			//createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
			//					  Intent.ShortcutIconResource.fromContext(this,
			//															  R.drawable.link));
			createIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			MainActivity.this.sendBroadcast(createIntent);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.handler = new Handler();

		Uri uri = getIntent().getData();

	 	requestWindowFeature(Window.FEATURE_LEFT_ICON);
		if (uri == null)
		{
			setTitle(R.string.settings);
		}
		else
		{
			setTitle(R.string.app_name);
		}
		setContentView(R.layout.main);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.link);

		Button buyButton= (Button)this.findViewById(R.id.buy);
		//if (uri!=null)
		{
			buyButton.setVisibility(View.GONE);
		}

		this.linkView = (ListView)this.findViewById(R.id.listView1);
		this.linkView.setItemsCanFocus(true);
		this.linkView.setFocusable(true);
		this.linkView.setFocusableInTouchMode(true);
		this.linkView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		this.registerForContextMenu(this.linkView);
		this.linkView.setOnItemClickListener(this);
		this.linkView.setOnItemLongClickListener(new OnItemLongClickListener(){

				public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id)
				{

					TextView text = (TextView)view.findViewById(android.R.id.text1);
					final String link = text.getText().toString();
					AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int p2)
							{
								dialog.dismiss();
							}

						});
					builder.setMessage(link).setPositiveButton(android.R.string.copy, new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int p2)
							{
								putText(link);
								Toast.makeText(MainActivity.this, R.string.copied, Toast.LENGTH_LONG).show();
							}

						});
					builder.create().show();
					return false;
				}

			});
		this.appView = (GridView)this.findViewById(R.id.gridview);
		this.appView.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id)
				{
					appAdapter.setSelected(pos);
					savePref();
				}
			});

		this.progressBar = (ProgressBar)this.findViewById(R.id.progressBar1);
		this.progressBar.setMax(5);


		this.appTask = new LoadAppTask();
		this.appTask.execute();

		if (uri != null)
		{
			final String url = uri.toString();
			this.task = new LoadLinksTask();
			this.task.execute(url);
		}
		else
		{
			int m=LinearLayout.LayoutParams.MATCH_PARENT;
			LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(m, m);
			this.appView.setLayoutParams(params);
			this.progressBar.setVisibility(View.GONE);
			clearDefault("https://www.google.com");
			clearDefault("http://www.google.com");
		}

		AdView adView = new AdView(this, AdSize.BANNER, "a151640e221df04");
		LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);
		layout.addView(adView); 
		AdRequest req =new AdRequest();
		adView.loadAd(req);
	}	

	private void findLinksInternal(List<String> links, URI uri)
	{
		List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "ISO8859-1"); //$NON-NLS-1$
		for (NameValuePair pair : pairs)
		{
			String value = pair.getValue();
			if (value != null && isLink(value))
			{
				try
				{
					String link = URLDecoder.decode(value, "ISO8859-1"); //$NON-NLS-1$
					links.add(link);
				}
				catch (Throwable ex)
				{
					Log.e("findLinksInternal", ex.getMessage(), ex); //$NON-NLS-1$
				}
			}
		}
	}

	public boolean isShort(URI uri)
	{
		if (uri == null)
			return false;
		if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme())) //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		if (uri.getHost().length() > 9)
			return false;
		if (uri.getRawQuery() != null && uri.getRawQuery().length() > 0)
			return false;
		if (uri.getRawPath() != null && uri.getRawPath().length() > 12)
			return false;
		return true;
	}

	public boolean isLink(String value)
	{
		if (value == null)
			return false;
		if (value.startsWith("http://")) //$NON-NLS-1$
			return true;
		if (value.startsWith("https://")) //$NON-NLS-1$
			return true;
		if (value.startsWith("file://")) //$NON-NLS-1$
			return true;
		if (value.startsWith("ftp://")) //$NON-NLS-1$
			return true;
		return false;
	}

	public boolean onCreateOptiondMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info)
	{
		super.onCreateContextMenu(menu, v, info);
	}

	public boolean onContextItemSelected(MenuItem item)
	{
		return true;
	}

	protected void savePref()
	{
		if (appAdapter == null)
			return;
		AppInfo app = appAdapter.getSelectedApp();
		if (app == null)
			return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.edit().putString("package", app.packageName).putString("class", app.name).commit(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void viewLink(String link)
	{
		if (link == null || appAdapter == null)
		{
			return;
		}
		AppInfo app = appAdapter.getSelectedApp();
		if (app == null)
		{
			Toast.makeText(this, R.string.select_a_browser, Toast.LENGTH_LONG).show();
			return;
		}
		//	savePref();
		Uri data = null;
		data = Uri.parse(link);
		String type = MimeTypes.getType(link);
		if (type == null)
		{
			type = "text/plain"; //$NON-NLS-1$
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		i.setClassName(app.packageName, app.name);
		if (link.startsWith("file://")) //$NON-NLS-1$
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
			this.finish();
		}
		catch (Throwable ex)
		{
			Log.e("tochrome", "viewLink:" + ex.getMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		canceled = true;
	}

	public static void addLink(Context context, String newLink)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		List<String> links = new ArrayList<String>();
		links.add(newLink);
		for (int i = 1; i <= 25; ++i)
		{
			String link = prefs.getString("link" + i, null); //$NON-NLS-1$
			if (link != null && link.trim().length() > 0 && !links.contains(link))
				links.add(link);
		}
		Editor edit = prefs.edit();
		for (int i = 1; i <= links.size() && i <= 25; ++i)
		{
			edit.putString("link" + i, links.get(i - 1)); //$NON-NLS-1$
		}
		edit.commit();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		TextView text = (TextView)view.findViewById(android.R.id.text1);
		String link = text.getText().toString();
		viewLink(link);
	}

	public void clearDefault(String link)
	{
		if (link == null || link.isEmpty())
			return;
		Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
		PackageManager pm = getPackageManager();
		ComponentName name = i.resolveActivity(pm);

		String pkg = name.getPackageName();
		if (pkg != null && !"android".equals(pkg)) //$NON-NLS-1$
		{
			if (this.getPackageName().equals(pkg))
				return;
			Intent intent = new Intent();
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", pkg, null); //$NON-NLS-1$
			intent.setData(uri);
			startActivity(intent);
			Toast.makeText(this, R.string.clear_default, Toast.LENGTH_LONG).show();
		}
	}

	public List<AppInfo> findActivity(Uri uri, String type)
	{
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_VIEW, null);
		if ("file".equalsIgnoreCase(uri.getScheme()))
		{
			intent.setDataAndType(uri, type);
		}
		else
		{
			intent.setData(uri);		
		}
		List<ResolveInfo> rList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> acts = pm.queryIntentActivities(intent, 0);
		rList.addAll(acts);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String selectedPkg = pref.getString("package", "");
		String selectedName = pref.getString("class", "");
		if ("file".equals(uri.getScheme()))
		{
			intent = new Intent();
			intent.setClassName("com.android.chrome",
								"com.google.android.apps.chrome.Main");
			acts = pm.queryIntentActivities(intent, 0);
			if (acts != null && acts.size() > 0)
				rList.addAll(acts);

			intent = new Intent();
			intent.setClassName("com.chrome.beta",
								"com.google.android.apps.chrome.Main");
			acts = pm.queryIntentActivities(intent, 0);
			if (acts != null && acts.size() > 0)
				rList.addAll(acts);
		}

		for (ResolveInfo r : rList)
		{
			if (this.getPackageName().equals(r.activityInfo.packageName))
				continue;
			AppInfo app = new AppInfo();
			apps.add(app);
			app.packageName = r.activityInfo.packageName;
			app.name = r.activityInfo.name;
			app.icon = r.activityInfo.loadIcon(pm);
			if (app.icon == null)
			{
				app.icon = getResources().getDrawable(R.drawable.link);
			}
			CharSequence label = r.activityInfo.loadLabel(pm);
			if (label == null)
			{
				app.label = ""; //$NON-NLS-1$
			}
			else
			{
				app.label = label.toString();
			}
			if (selectedPkg.equals(app.packageName))
			{
				if ((app.name == null && selectedName.length() == 0) || selectedName.equals(app.name))
				{
					app.selected = true;
				}
			}
		}
		return apps;
	}


	@SuppressWarnings("deprecation")
	public void putText(String text)
	{
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES. HONEYCOMB)
		{
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		}
		else
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE); 
			android.content.ClipData clip = ClipData.newPlainText("simple text", text);
			clipboard.setPrimaryClip(clip);
		}
	}

	class LinkException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LinkException(String msg)
		{
			super(msg);
		}
	}
}
