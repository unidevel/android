package com.unidevel.barwidget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AppSelectListView extends ListView implements
		OnCheckedChangeListener {
	ProgressDialog loadingAppsDialog;
	List<AppInfo> allApps;
	
	Handler handler;
	AppsAdapter adapter;
	Context context;
	int maxApps = 0;
	
	Set<String> selectedApps;
	Set<String> allAppKeys;
	OnAppClickListener onAppClickListener;
	onSelectionChangedListener onSelectionChangedListener;

	public interface OnAppClickListener {
		public void onClick(String packageName, String activityName);
	}

	public interface onSelectionChangedListener {
		public void onSelectionChanged();
	}

	public void setOnAppClickListener(OnAppClickListener onAppClickListener) {
		this.onAppClickListener = onAppClickListener;
	}

	public void setOnSelectionChangedListener(
			onSelectionChangedListener onSelectionChangedListener) {
		this.onSelectionChangedListener = onSelectionChangedListener;
	}

	public AppSelectListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		selectedApps = new HashSet<String>();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void refreshApps() {
		context = this.getContext();
		loadingAppsDialog = ProgressDialog.show(context, context
				.getString(R.string.progress_title_loadapps), context
				.getString(R.string.progress_msg_loadapps));
		allApps = new ArrayList<AppInfo>();
		allAppKeys = selectedApps;
		selectedApps = new HashSet<String>();
		handler = new Handler();
		adapter = null;
		LoadAppsThread thread = new LoadAppsThread();
		thread.start();
	}

	public class LoadAppsThread extends Thread {

		@Override
		public void run() {
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			final List<ResolveInfo> pkgs = context.getPackageManager()
					.queryIntentActivities(intent, 0);
			for (ResolveInfo pkg : pkgs) {
				AppInfo info = new AppInfo();
				info.packageName = pkg.activityInfo.packageName;
				info.activityName = pkg.activityInfo.name;
				info.name = pkg.loadLabel(context.getPackageManager())
						.toString();
				info.icon = pkg.loadIcon(context.getPackageManager());
				allApps.add(info);
			}
			handler.post(new BuildAppList());
		}
	}

	public class AppsAdapter extends BaseAdapter {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
			if (view == null) {
				view = inflater.inflate(R.layout.checked_list_item, null);
			}
			AppInfo info = allApps.get(position);
			((TextView) view.findViewById(R.id.label)).setText(info.name);
			((ImageView) view.findViewById(R.id.icon))
					.setImageDrawable(info.icon);
			CheckBox checkBox = ((CheckBox) view.findViewById(R.id.checkbox));
			checkBox.setOnCheckedChangeListener(AppSelectListView.this);
			checkBox.setTag(position);
			String key = info.packageName + ":" + info.activityName;
			if (allAppKeys.contains(key)) {
				checkBox.setChecked(true);
				selectedApps.add(key);
			} else {
				checkBox.setChecked(false);
			}
			return view;
		}

	}

	public class BuildAppList implements Runnable {
		public void run() {
			loadingAppsDialog.dismiss();
			ListView listView = (ListView) AppSelectListView.this
					.findViewById(R.id.listApps);
			adapter = new AppsAdapter();
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {
					if (onAppClickListener != null) {
						AppInfo info = allApps.get(position);
						onAppClickListener.onClick(info.packageName,
								info.activityName);
					}
				}
			});
		}
	}

	public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
		int pos = (Integer) checkBox.getTag();
		AppInfo app = allApps.get(pos);
		String key = app.packageName+":"+app.activityName;
		if (checked) {
			if (!selectedApps.contains(key) ){
				if (maxApps > 0 && selectedApps.size() >= maxApps) {
					String msg = String.format(context
							.getString(R.string.error_max_apps), maxApps);
					Toast.makeText(context, msg, 2).show();
					checkBox.setChecked(false);				
					return;
				}
				selectedApps.add(key);
			}
		} else {
			selectedApps.remove(key);
		}
		if (onSelectionChangedListener != null) {
			onSelectionChangedListener.onSelectionChanged();
		}
	}

	public void setMaxApps(int maxApps) {
		this.maxApps = maxApps;
	}

	public int getMaxApps() {
		return maxApps;
	}

	public void setSelectedApps(String... apps) {
		selectedApps.clear();
		for (int i = 0; i < apps.length; i += 2) {
			String packageName = apps[i];
			String className = apps[i + 1];
			selectedApps.add(packageName + ":" + className);
			// Intent intent = new Intent(Intent.ACTION_MAIN, null);
			// intent.addCategory(Intent.CATEGORY_LAUNCHER);
			// intent.setClassName(packageName, className);
			// final List<ResolveInfo> pkgs =
			// context.getPackageManager().queryIntentActivities( intent, 0);
			// if ( pkgs.size() > 0 ){
			// AppInfo app = new AppInfo();
			// app.packageName = packageName;
			// app.activityName = className;
			// app.name =
			// pkgs.get(0).loadLabel(context.getPackageManager()).toString();
			// app.icon = pkgs.get(0).loadIcon(context.getPackageManager());
			// }
		}
	}

	public List<String> getSelectedApps() {
		List<String> apps = new ArrayList<String>();
		for ( String app: selectedApps ) {
			String[] appInfo = app.split(":"); 
			apps.add(appInfo[0]);
			apps.add(appInfo[1]);
		}
		return apps;
	}
}
