package com.unidevel.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.unidevel.R;

public class AppListAdapter extends BaseAdapter implements ListAdapter {

	class ActivityItem {
		boolean enabled;
		String packageName;
		String className;
		String name;
		Drawable icon;
	}

	List<ActivityItem> items;
	PackageManager manager;
	LayoutInflater inflater;
	View itemView;
	TextView labelView;
	ImageView iconView;

	public AppListAdapter(Context context, View itemView) {
		this.items = new ArrayList<AppListAdapter.ActivityItem>();
		this.manager = context.getPackageManager();
		this.inflater = LayoutInflater.from(context);
		this.itemView = itemView;
		this.labelView = (TextView) itemView.findViewById(R.id.label);
		this.iconView = (ImageView) itemView.findViewById(R.id.image);
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.itemView;
		}
		ActivityItem item = items.get(position);
		if ( item.name != null )
		{
			this.labelView.setText(item.name);
		}
		this.iconView.setImageDrawable(item.icon);
		return convertView;
	}

	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	public void listApps() {
		List<ApplicationInfo> apps = this.manager
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);
		int count = 0;
		this.items = new ArrayList<AppListAdapter.ActivityItem>();
		
		for (ApplicationInfo app : apps) {
			ActivityItem item = new ActivityItem();
			if ( app.packageName == null || app.className == null )
				continue;
			item.packageName = app.packageName;
			item.className = app.className;
			item.name = app.loadLabel(this.manager).toString();
			item.icon = app.loadIcon(this.manager);
			items.add(item);
			count++;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	public void refresh()
	{
		this.notifyDataSetInvalidated();
	}
}
