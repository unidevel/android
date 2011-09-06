package com.unidevel.barwidget;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectAppPreference extends DialogPreference {
	LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	public SelectAppPreference(Context context) {
		super(context, null);
		setPositiveButtonText(null);
		this.context = context;
//		setWidgetLayoutResource(R.layout.pref_select_app);
		setTitle(R.string.title_select_app);
		setDialogTitle(R.string.title_select_app);
		setLayoutResource(R.layout.pref_select_app);
		setDialogLayoutResource(R.layout.pref_select_app_dialog);
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		if ( selectedApp != null ){
			((ImageView)view.findViewById(R.id.app_icon)).setImageDrawable(selectedApp.icon);
			((TextView)view.findViewById(R.id.app_name)).setText(selectedApp.name);
		}
	}
	
	@Override
	protected View onCreateDialogView() {
//		dialogView = inflater.inflate(R.layout.pref_select_app_dialog, null);
		dialogView = super.onCreateDialogView();
		return dialogView;
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		appView = super.onCreateView(parent);
		return appView;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}
	
	@Override
	public int getWidgetLayoutResource() {
		return R.layout.pref_select_app;
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
	}
	
	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);
		makeAppList();
	}
		
	public void setApp(String packageName, String className){
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(packageName, className);
        final List<ResolveInfo> pkgs = context.getPackageManager().queryIntentActivities( intent, 0);
        if ( pkgs.size() > 0 ){
        	selectedApp = new AppInfo();
        	selectedApp.packageName = packageName;
        	selectedApp.activityName = className;
        	selectedApp.name = pkgs.get(0).loadLabel(context.getPackageManager()).toString();
        	selectedApp.icon = pkgs.get(0).loadIcon(context.getPackageManager());
        	notifyChanged();
        }
	}
	
	public String getAppPackage(){
		return selectedApp==null?null:selectedApp.packageName;
	}
	
	public String getAppClassName(){
		return selectedApp==null?null:selectedApp.activityName;
	}
	
	public String getAppName(){
		return selectedApp==null?null:selectedApp.name;
	}
	
	View dialogView;
	View appView;
    ProgressDialog loadingAppsDialog;
    List<AppInfo> apps;
    Handler handler;
    AppsAdapter adapter;
    Context context;
    
	AppInfo selectedApp;
    protected void makeAppList(){
    	loadingAppsDialog = ProgressDialog.show(context, context.getString(R.string.progress_title_loadapps), context.getString(R.string.progress_msg_loadapps));
    	apps = new ArrayList<AppInfo>();
    	handler = new Handler();
    	adapter = null;
    	LoadAppsThread thread = new LoadAppsThread();
    	thread.start();
    }
    
    class AppInfo {
    	String name;
    	String activityName;
    	String packageName;
    	Drawable icon;
    }
        
    public class LoadAppsThread extends Thread {
    	
    	@Override
    	public void run() {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            final List<ResolveInfo> pkgs = context.getPackageManager().queryIntentActivities( intent, 0);
            for ( ResolveInfo pkg : pkgs ) {
            	AppInfo info = new AppInfo();
            	info.packageName = pkg.activityInfo.packageName;
            	info.activityName = pkg.activityInfo.name;
            	info.name = pkg.loadLabel(context.getPackageManager()).toString();
            	info.icon = pkg.loadIcon(context.getPackageManager());
            	apps.add(info);
            }
            handler.post(new BuildAppList());
    	}
    }
    
    public class AppsAdapter extends BaseAdapter {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			if ( view == null ){
				view = inflater.inflate(R.layout.app_item, null);
			}
			AppInfo info = apps.get(position);
			((TextView)view.findViewById(R.id.app_name)).setText(info.name);
			((ImageView)view.findViewById(R.id.app_icon)).setImageDrawable(info.icon);
			return view;
		}
    	
    }
    
    public class BuildAppList implements Runnable {
    	public void run() {
    		loadingAppsDialog.dismiss();
        	ListView listView = (ListView)dialogView.findViewById(R.id.listApps);
        	adapter = new AppsAdapter(); 
        	listView.setAdapter(adapter);
        	listView.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {
					selectedApp = apps.get(position);					
					SelectAppPreference.this.getDialog().dismiss();
					notifyChanged();
				}
        	});
    	}
    }
}
