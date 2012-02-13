package com.unidevel.findmyapp;

import android.graphics.drawable.Drawable;

public class AppInfo implements Comparable<AppInfo>{
	String name;
	String activityName;
	String packageName;
	String path;
	String dataDir;
	boolean hasHistory;
	
	Drawable icon;
	
	public AppInfo(){
		hasHistory = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public Drawable getIcon() {
		return icon;
	}
	
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	public String getDataDir() {
		return dataDir;
	}
	
	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	@Override
	public int compareTo(AppInfo another) {
		AppInfo app1 = this;
		AppInfo app2 = another;
		if ( app1.name == null ) {
			if ( app2.name == null ) return 0;
			return -1;
		}
		return app1.name.compareTo(app2.name);
	}
}
