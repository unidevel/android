package com.unidevel.findmyapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AppItemAdapter extends BaseAdapter {
	List<AppInfo> apps;
	Context context;
	LayoutInflater inflater;
	public AppItemAdapter(Context context, List<AppInfo> apps2) {
		this.apps = new ArrayList<AppInfo>();
		this.apps.addAll(apps2);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public List<AppInfo> getApps() {
		return apps;
	}

	public void remove(int position) {
		apps.remove(position);
		notifyDataSetChanged();
	}


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
			view = inflater.inflate(R.layout.fma_appitem, null);
		}
		view.findViewById(android.R.id.checkbox).setVisibility(View.GONE);
		AppInfo info = (AppInfo) getItem(position);
		if (info.hasHistory) {
			((TextView) view.findViewById(android.R.id.text1))
					.setTextColor(0xFF44FF44);
		} else {
			((TextView) view.findViewById(android.R.id.text1))
					.setTextColor(Color.WHITE);
		}
		
		if (info.icon != null) {
			((ImageView) view.findViewById(android.R.id.icon))
					.setImageDrawable(info.icon);
		} else {
			((ImageView) view.findViewById(android.R.id.icon))
					.setImageResource(R.drawable.icon);
		}
		((TextView) view.findViewById(android.R.id.text1)).setText(info.name);
		((TextView) view.findViewById(android.R.id.text2))
				.setText(info.packageName);
		return view;
	}

}
