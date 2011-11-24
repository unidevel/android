package com.unidevel.findmyapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyAppActivity extends Activity {
	ListView appView;
	ListAdapter appAdapter;
	Handler handler;
	ProgressDialog progressDialog;
	File appFile;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File dir = new File(Environment.getExternalStorageDirectory(),
				"FindMyApp");
		if (!dir.exists())
			dir.mkdirs();
		appFile = new File(dir, "apps.txt");

		handler = new Handler();
		appView = new ListView(this);
		setContentView(appView);

		loadInstalled(appFile, appView);

		registerForContextMenu(appView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.getMenuInflater().inflate(R.menu.myapp, menu);
		return true;
	}

	public void loadInstalled(File file, ListView view) {
		progressDialog = ProgressDialog.show(this, "Loading", "Loading apps");
		new LoadAppsThread(file, view).start();
	}

	public class AppInfo {
		public String name;
		public String activityName;
		public String packageName;
		public String path;
		public Drawable icon;
		public boolean installed;
		public boolean isNew;

		public String toString() {
			return "App(" + name + ", " + packageName + ", " + activityName	+ ")";
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == appView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			AppInfo app = (AppInfo) appView.getAdapter().getItem(info.position);
			menu.setHeaderTitle(app.name);
			menu.add(Menu.NONE, R.id.menuFind, 1, "Find in Market");
			if (!app.installed) {
				menu.add(Menu.NONE, R.id.menuDelete, 2, "Remove");
			}
			else {
				menu.add(Menu.NONE, R.id.menuRun, 0, "Run");
				menu.add(Menu.NONE, R.id.menuUninstall, 4, "Uninstall");
			}
			if ( app.path != null ) {
				menu.add(Menu.NONE, R.id.menuBackup, 2, "Backup");
			}
		}
	}

	public class LoadAppsThread extends Thread {
		ListView view;
		File file;

		public LoadAppsThread(File file, ListView view) {
			this.view = view;
			this.file = file;
		}

		@Override
		public void run() {
			Context context = MyAppActivity.this;
			LinkedHashMap<String, AppInfo> apps = new LinkedHashMap<String, AppInfo>();

			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			final List<ResolveInfo> pkgs = context.getPackageManager()
					.queryIntentActivities(intent, 0);
			for (ResolveInfo pkg : pkgs) {
				AppInfo info = new AppInfo();
				info.packageName = pkg.activityInfo.packageName;
				info.activityName = pkg.activityInfo.name;
				info.name = pkg.loadLabel(context.getPackageManager()).toString();
				info.icon = pkg.loadIcon(context.getPackageManager());
				info.installed = true;
				info.isNew = true;
				apps.put(info.packageName, info);
			}

			FileInputStream input;
			Properties props = new Properties();
			try {
				input = new FileInputStream(file);
				props.load(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Object key : props.keySet()) {
				AppInfo info = new AppInfo();
				info.packageName = String.valueOf(key);
				info.name = String.valueOf(props.get(key));
				info.installed = false;
				info.isNew = false;
				if (!apps.containsKey(info.packageName))
					apps.put(info.packageName, info);
				else {
					info = apps.get(info.packageName);
					info.isNew = false;
				}
			}
			
			PackageManager pm = getPackageManager();
			List<PackageInfo> pkginfo_list = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
			List<ApplicationInfo> appinfo_list = pm.getInstalledApplications(0);
			for (int x=0; x < pkginfo_list.size(); x++){             
			  PackageInfo pkginfo = pkginfo_list.get(x);
			  String pkgName = pkginfo.packageName;
			  AppInfo app = apps.get(pkgName);
			  if ( app != null ) {
				  app.path = appinfo_list.get(x).publicSourceDir;  //store package path in array 
			  }
			  Log.i("Find", pkgName+": "+appinfo_list.get(x).publicSourceDir);
			}
//			try {
//				File dataDir = new File("/data/app");
//				Log.i("Data.isDir", ""+dataDir.isDirectory());
//				File[] files = dataDir.listFiles();
//				for (File file: files){
//					if ( file.isDirectory() )continue;
//					Log.i("Process", file.getPath());
//					try {
//
//					    PackageInfo info = pm.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
//					    String pkgName = info.packageName;
//					    AppInfo app = apps.get(pkgName);
//					    if( app != null ) 
//					    	app.path = file.getPath();
//					}
//					catch(Throwable ex){}
//				}
//			} catch(Throwable ex){
//				Log.e("Read /data/app", ex.getMessage(), ex);
//			}
//			try {
//				File dataDir = new File("/system/app");
//				File[] files = dataDir.listFiles();
//				for (File file: files){
//					if ( file.isDirectory() )continue;
//					Log.i("Process", file.getPath());
//					try {
//
//					    PackageInfo info = pm.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
//					    String pkgName = info.packageName;
//					    AppInfo app = apps.get(pkgName);
//					    if ( app != null )
//					    	app.path = file.getPath();
//					}
//					catch(Throwable ex){}
//				}
//			} catch(Throwable ex){
//				Log.e("Read /system/app", ex.getMessage(), ex);
//			}
			
			AppInfo[] appArray = apps.values().toArray(new AppInfo[0]);
			Arrays.sort(appArray, 0, appArray.length, new Comparator<AppInfo>(){
				public int compare(AppInfo app1, AppInfo app2) {
					if ( app1.name == null ) return -1;
					return app1.name.compareTo(app2.name);
				}
			});
			
			handler.post(new BuildAppList(view, appArray));
		}
	}

	public class AppAdapter extends BaseAdapter {
		List<AppInfo> apps;

		public AppAdapter(AppInfo[] apps) {
			this.apps = new ArrayList<AppInfo>();
			Collections.addAll(this.apps, apps);
		}

		public List<AppInfo> getApps() {
			return apps;
		}

		public void remove(int position){
			apps.remove(position);
			notifyDataSetChanged();
		}
		
		LayoutInflater inflater = (LayoutInflater) MyAppActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public int getCount() {
			return apps.size();
		}

		public Object getItem(int position) {
			return apps.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup group) {
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(android.R.layout.simple_list_item_2,
						null);
			}
			AppInfo info = (AppInfo) getItem(position);
			if (info.installed) {
				
				if (info.isNew) {
					((TextView) view.findViewById(android.R.id.text1))
						.setTextColor(Color.CYAN);						
				}
				else {
					((TextView) view.findViewById(android.R.id.text1))
						.setTextColor(Color.BLUE);
				}
			} else {
				((TextView) view.findViewById(android.R.id.text1))
						.setTextColor(Color.WHITE);
			}
			((TextView) view.findViewById(android.R.id.text1))
					.setText(info.name);
			((TextView) view.findViewById(android.R.id.text2))
					.setText(info.packageName);
			return view;
		}
	}

	public class BuildAppList implements Runnable {
		ListView view;
		AppInfo[] apps;

		public BuildAppList(ListView view, AppInfo[] apps) {
			this.view = view;
			this.apps = apps;
		}

		public void run() {
			progressDialog.dismiss();
			AppAdapter adapter = new AppAdapter(apps);
			view.setAdapter(adapter);
//			view.setOnItemClickListener(new OnItemClickListener() {
//				public void onItemClick(AdapterView<?> adapterView, View view,
//						int position, long id) {
//					AppInfo info = apps[position];
//					String APP_MARKET_URL = "market://details?id="
//							+ info.packageName;
//					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
//							.parse(APP_MARKET_URL));
//					startActivity(intent);
//				}
//			});
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if ( item.getItemId() == R.id.menuFind ) {
			AdapterView.AdapterContextMenuInfo info = 
				(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			AppInfo app = (AppInfo) appView.getAdapter().getItem(info.position);
			String APP_MARKET_URL = "market://details?id="+ app.packageName;
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_MARKET_URL));
			startActivity(intent);
		}
		else if ( item.getItemId() == R.id.menuRun ) {
			AdapterView.AdapterContextMenuInfo info = 
				(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			AppInfo app = (AppInfo) appView.getAdapter().getItem(info.position);
			Intent intent = new Intent();
			intent.setClassName(app.packageName, app.activityName);
			startActivity(intent);			
		}
		else if ( item.getItemId() == R.id.menuDelete ) {
			AdapterView.AdapterContextMenuInfo info = 
				(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			AppAdapter apps = (AppAdapter) appView.getAdapter();
			apps.remove(info.position);
		}
		else if ( item.getItemId() == R.id.menuUninstall ) {
			AdapterView.AdapterContextMenuInfo info = 
				(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			AppInfo app = (AppInfo) appView.getAdapter().getItem(info.position);
			Uri packageURI = Uri.parse("package:"+app.packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			startActivity(uninstallIntent);			
		}
		else if ( item.getItemId() == R.id.menuBackup ) {
			AdapterView.AdapterContextMenuInfo info = 
				(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			AppInfo app = (AppInfo) appView.getAdapter().getItem(info.position);
			File file = new File(app.path);
			try {
				File dir = new File(Environment.getExternalStorageDirectory(),"FindMyApp");
				if ( !dir.exists() )dir.mkdirs();
				File newFile = new File(dir, file.getName());
				FileInputStream input = new FileInputStream(file);
				FileOutputStream output = new FileOutputStream(newFile);
				byte[] buf = new byte[8192];
				int len;
				while ( (len = input.read(buf)) > 0 ){
					output.write(buf,0, len);
				}
				output.flush();
				output.close();
				input.close();
				Toast.makeText(this, "Backup "+app.name+" to "+newFile.getPath(), 3).show();
			}
			catch(Throwable ex){
				Log.e("Backup failed", ex.getMessage(), ex);
				Toast.makeText(this, "Backup "+app.name+ " failed", 3).show();
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (R.id.menuRefresh == item.getItemId()) {
			loadInstalled(appFile, appView);
		} else if (R.id.menuExport == item.getItemId()) {
			AppAdapter adapter = (AppAdapter) appView.getAdapter();
			List<AppInfo> apps = adapter.getApps();
			Properties props = new Properties();
			for (AppInfo app : apps) {
				props.setProperty(app.packageName, app.name);
			}
			FileOutputStream out;
			try {
				out = new FileOutputStream(appFile);
				props.save(out, "");
				Toast.makeText(this, "Succeeded export to file " + appFile, 3)
						.show();
			} catch (FileNotFoundException e) {
				Toast.makeText(this, "Error:" + e.getMessage(), 3).show();
			}
		}
		// else if ( R.id.menuImport == item.getItemId() ) {
		// loadImport(file, appView);
		// }
		return super.onOptionsItemSelected(item);
	}
}