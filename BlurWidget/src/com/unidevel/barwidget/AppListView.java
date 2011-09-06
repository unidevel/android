package com.unidevel.barwidget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListView extends TouchInterceptor implements TouchInterceptor.DragListener, TouchInterceptor.DropListener{
    AppsAdapter adapter;
    Context context;
    List<AppInfo> allApps;
	AppInfo dummyApp; 
    
    public AppListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setDragListener(this);
		setDropListener(this);
		allApps = new ArrayList<AppInfo>();
		adapter = new AppsAdapter();
		setAdapter(adapter);
		dummyApp = new AppInfo();
		dummyApp.name = context.getString(R.string.add_app);
		dummyApp.icon = new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_icon));
//		setOnItemLongClickListener(this);
    }
    
    public void refresh(){
    	adapter.notifyDataSetChanged();
    }
    
    public class AppsAdapter extends BaseAdapter {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		public int getCount() {
			return allApps.size();
		}

		public Object getItem(int position) {
			return allApps.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup group) {
			View view = convertView;
			if ( view == null ){
				view = inflater.inflate(R.layout.app_item, null);
			}
			AppInfo info = allApps.get(position);
			((TextView)view.findViewById(R.id.label)).setText(info.name);
			
			if ( info.iconFile != null && info != dummyApp ) {
				File file = new File(info.iconFile);
				if ( file.exists() ){
					Bitmap bitmap = BitmapFactory.decodeFile(info.iconFile);
					((ImageView)view.findViewById(R.id.icon)).setImageBitmap(bitmap);
				}
				else {
					((ImageView)view.findViewById(R.id.icon)).setImageDrawable(info.icon);
				}
			}
			else {
				((ImageView)view.findViewById(R.id.icon)).setImageDrawable(info.icon);
			}
			return view;
		}
    }

	public void drag(int from, int to) {
		
	}

	public void drop(int from, int to) {
		if ( from == to ) return;
		AppInfo fromApp = allApps.get(from);
		if ( from < to ) {
			allApps.add(to+1, fromApp);
			allApps.remove(from);
		}
		else {
			allApps.remove(from);
			allApps.add(to, fromApp);
		}
		adapter.notifyDataSetChanged();
	}
	
	public List<String> getApps(){
		List<String> apps = new ArrayList<String>();
		for (AppInfo info: allApps ) {
			apps.add(info.packageName);
			apps.add(info.activityName);
		}
		return apps;
	}
	
	public int getCount(){
		return allApps.size();
	}
	
	public String getAppClass(int index){
		return allApps.get(index).activityName;
	}
	
	public String getAppIcon(int index){
		return allApps.get(index).iconFile;
	}
	
	public String getAppPackage(int index){
		return allApps.get(index).packageName;
	}
	
	public boolean addApp(String packageName, String className, String iconFile){
		return addApp(packageName, className, iconFile, true);
	}
	
	public boolean addApp(String packageName, String className, String iconFile, boolean notifyChanged){
		if ( className == null ) {
			allApps.add(dummyApp);
		}
		else {
	        Intent intent = new Intent(Intent.ACTION_MAIN, null);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName(packageName, className);
	        final List<ResolveInfo> pkgs = context.getPackageManager().queryIntentActivities( intent, 0);
	        if ( pkgs.size() > 0 ){
	        	AppInfo app = new AppInfo();
	        	app.packageName = packageName;
	        	app.activityName = className;
	        	app.iconFile = iconFile;
	        	app.name = pkgs.get(0).loadLabel(context.getPackageManager()).toString();
	        	app.icon = pkgs.get(0).loadIcon(context.getPackageManager());
	        	allApps.add(app);
	        }
		}
    	if ( notifyChanged ) adapter.notifyDataSetChanged();
        return true;
	}
	
	public void clearApps(){
		allApps.clear();
		adapter.notifyDataSetChanged();
	}
	
	public void setApps(String... apps){
		allApps.clear();
		addApps(apps);
	}
	
	public void addApps(String... apps){
		for ( int i = 0; i < apps.length; i += 2 ) {
			String packageName = apps[i];
			String className = apps[i+1];
			addApp(packageName, className, null, false);
		}
		if ( apps.length> 1) adapter.notifyDataSetChanged();
	}
	
	public void removeApp(int position){
		allApps.remove(position);
		adapter.notifyDataSetChanged();		
	}
	
	
	public void clearApp(int position){
		allApps.set(position, dummyApp);
		adapter.notifyDataSetChanged();		
	}
	
	public void setAppIcon(int index, String iconFile){
		setAppIcon(index, iconFile, true);
	}
	
	public void setAppIcon(int index, String iconFile, boolean notifyChanged){
		AppInfo app = allApps.get(index);
		if ( app == dummyApp ) return;
		String appIcon = app.iconFile;
		app.iconFile = iconFile;		
		if ( iconFile == null ) {
			if ( appIcon == null )return;
		}
		else if (iconFile.equals(appIcon)) return;
		if (notifyChanged) adapter.notifyDataSetChanged();
	}

//	public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position,
//			long id) {
//		AppInfo app = allApps.get(position);
//		Builder builder = new Builder(this.getContext());
//		String msg = String.format(getContext().getString(R.string.msg_remove_app), app.name);
//		builder.setTitle(R.string.title_remove_app).setMessage(msg);
//		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int which) {
//				removeApp(position);
//				dialog.dismiss();
//			}
//		});
//		builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//		return true;
//	}
}
