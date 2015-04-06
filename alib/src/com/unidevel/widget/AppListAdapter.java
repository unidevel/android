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

	class AppObserver extends DataSetObserver {

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
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.itemView;
		}
		ActivityItem item = items.get(position);
		this.labelView.setText(item.name);
		if (item.icon == null) {
			ComponentName cname = new ComponentName(item.packageName,
					item.className);
			try {
				item.icon = manager.getActivityIcon(cname);
				this.iconView.setImageDrawable(item.icon);
			} catch (NameNotFoundException e) {
				Log.e("Load icon", "Package:" + item.packageName + ", Class:"
						+ item.className, e);
			}
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	public void listApps() {
		List<ApplicationInfo> apps = this.manager
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);
		int count = 0;
		for (ApplicationInfo app : apps) {
			ActivityItem item = new ActivityItem();
			item.packageName = app.packageName;
			item.className = app.className;
			item.name = app.loadLabel(this.manager).toString();
			items.add(item);
			count++;
			if (count > 32) {
				this.notifyDataSetInvalidated();
			}
		}
		this.notifyDataSetInvalidated();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
