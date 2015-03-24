package com.unidevel.SMSTrack;

import android.graphics.drawable.Drawable;

public class AppInfo {
	public String name;
	public String activityName;
	public String packageName;
	public Drawable icon;
	public String iconFile;
	public String toString(){
		return "App("+name+", "+packageName+", "+activityName+")";
	}
}


