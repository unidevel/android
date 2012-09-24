package com.unidevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidevel.util.UnitUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class AppListActivity extends Activity{
	ListView appList;
	AppListActivity me = this;
	AppAdapter appAdapter = new AppAdapter();
	
	class AppItem implements Comparable<AppItem>
	{
		String name;
		Drawable icon;
		String packageName;
		String activityName;
		public AppItem()
		{
		}
		
		@Override
		public int compareTo(AppItem item)
		{
			if ( name != null ) return name.compareTo(item.name);
			return -1;
		}
	}
	
	class ViewHolder 
	{
		ImageView image;
		TextView text;
	}
	
	class AppAdapter extends BaseAdapter 
	{
		List<AppItem> items;
		protected final int TEXTVIEW_ID=1;
		protected final int IMAGEVIEW_ID=1;
		public AppAdapter(){
			items = new ArrayList<AppItem>();
		}
		
		public void setApps(List<AppItem> items){
			if ( this.items != null ){
				this.items.clear();
			}
			this.items.addAll(items);
			this.notifyDataSetInvalidated();
		}
		
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppItem item = (AppItem)getItem(position);
			ViewHolder holder = null;
			if ( convertView == null ) {
				holder = new ViewHolder();
				convertView = createView(holder);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			if(item.icon!=null)holder.image.setImageDrawable(item.icon);
			if(item.name!=null)holder.text.setText(item.name);
			return convertView;
		}
		
		@Override
		public int getViewTypeCount() {
			return 1;
		}
		
		private View createView(ViewHolder holder){
			LinearLayout layout = new LinearLayout(me);
			ListView.LayoutParams params = new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layout.setLayoutParams(params);
			layout.setFocusable(false);
			int paddingSize = UnitUtil.dip2px(me, 10);

			ImageView imageView = new ImageView(me);
			imageView.setId(IMAGEVIEW_ID);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setPadding(paddingSize, 0, 0, 0);
			imageView.setFocusable(false);

			int pxSize = UnitUtil.dip2px(me, 48);
			layout.addView(imageView, pxSize, pxSize);
			TextView textView = new TextView(me);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setTextSize(20);
			textView.setPadding(paddingSize, paddingSize, 0, 0);
			textView.setId(TEXTVIEW_ID);
			textView.setFocusable(false);
			layout.addView(textView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			holder.image = imageView;
			holder.text = textView;
			return layout;
		}
	}
	
	public class LoadAppsTask extends AsyncTask<Void, Void, Void>
	{
		List<AppItem> apps = null;
		ProgressDialog dialog;
		protected void onPreExecute(){
			dialog=ProgressDialog.show(me,"",me.getString(R.string.loading_app_message));
		}
		@Override
		protected Void doInBackground(Void... params) {
			PackageManager pm = me.getPackageManager();
			this.apps = new ArrayList<AppItem>();
//			List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			List<ApplicationInfo> appinfos = pm.getInstalledApplications(0);
			for (int x=0; x < appinfos.size(); x++){             
				ApplicationInfo appInfo = appinfos.get(x);
				if(!appInfo.enabled)continue;
				Intent intent=pm.getLaunchIntentForPackage(appInfo.packageName);
				if(intent==null)continue;
				AppItem item = new AppItem();
				item.icon = appInfo.loadIcon(pm);
				if ( item.icon == null ) item.icon = me.getResources().getDrawable(R.drawable.empty_app);
				item.name = appInfo.loadLabel(pm).toString();
				item.packageName = appInfo.packageName;
				apps.add(item);
			}
			Collections.sort(this.apps);
			{
				AppItem item = new AppItem();
				item.name = me.getString(R.string.empty_app);
				item.packageName="";
				item.icon = me.getResources().getDrawable(R.drawable.empty_app);
				apps.add(0, item);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			appAdapter.setApps(apps);
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(params);
			this.addContentView(layout, params);
			this.setTitle(R.string.app_list_title);
		}
		{
			appList = new ListView(this);
			appAdapter = new AppAdapter();
			appList.setAdapter(appAdapter);
			appList.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					AppItem item = (AppItem) appAdapter.getItem(position);
					Intent intent = new Intent();
					intent.setPackage(item.packageName);
					intent.putExtra("name", item.name);
					me.setResult(0, intent);
					me.finish();
				}
			});
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,1.0f);
			layout.addView(appList, params);
		}
		if(false){
			LinearLayout buttonLayout = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,0.01f);
			buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER);
			buttonLayout.setLayoutParams(params);
			layout.addView(buttonLayout, params);
			{
				Button btnOk = new Button(this);
				btnOk.setText(android.R.string.ok);
				buttonLayout.addView(btnOk, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			{
				Button btnCancel = new Button(this);
				btnCancel.setText(android.R.string.cancel);
				buttonLayout.addView(btnCancel, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
		}
		LoadAppsTask loadTask = new LoadAppsTask();
		loadTask.execute();
	}
}
