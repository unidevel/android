package com.unidevel.barwidget;

import java.util.List;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class Main extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	AppListView appView = (AppListView)findViewById(R.id.listApps);
    	String[] apps = new String[]{
				"com.android.alarmclock",
				"com.android.alarmclock.AlarmClock",

				"com.android.browser",	
				"com.android.browser.BrowserActivity",
		};
    	appView.addApps(apps);
//    	appView.setMaxApps(3);
        
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		
        PreferenceCategory titleCat = new PreferenceCategory(this);
		titleCat.setTitle(this.getString(R.string.category_title));
		root.addPreference(titleCat);

//		SelectAppPreference2 appPref = new SelectAppPreference2(this);
//		titleCat.addPreference(appPref);
//		appPref.setTitle("TEST");
//		appPref.setApps(new String[]{
//				"com.android.alarmclock.AlarmClock",
//				"com.android.alarmclock",
//
//				"com.android.browser.BrowserActivity",
//				"com.android.browser"	
//		});
		this.setPreferenceScreen(root);
    }    
    
    public void fillAppInfo(AppInfo app){
    	String packageName = app.packageName;
    	String className = app.activityName;
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
    	intent.setClassName(packageName, className);
    	final List<ResolveInfo> pkgs = this.getPackageManager().queryIntentActivities( intent, 0);
    	if ( pkgs.size() > 0 ){
    		app.name = pkgs.get(0).loadLabel(this.getPackageManager()).toString();
    		app.icon = pkgs.get(0).loadIcon(this.getPackageManager());
    	}
    }

	public void onBackPressed()
	{
		super.onBackPressed();
		AppListView appView = (AppListView)findViewById( R.id.listApps );
		// Log.i("Test", String.valueOf(appView.getSelection()));
	}
}