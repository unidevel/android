package com.unidevel.findmyapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Util {
	public static List<AppInfo>[] getAllApps(Context context){
		ArrayList<AppInfo> sysApps = new ArrayList<AppInfo>();
		ArrayList<AppInfo> userApps = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		
//		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		List<ApplicationInfo> appinfos = pm.getInstalledApplications(0);
		for (int x=0; x < appinfos.size(); x++){             
			ApplicationInfo appInfo = appinfos.get(x);
			AppInfo app = new AppInfo();
			app.setName(appInfo.loadLabel(pm).toString());
			app.setPackageName(appInfo.packageName);
			app.setIcon(appInfo.loadIcon(pm));
			app.setPath(appInfo.publicSourceDir);
			app.setDataDir(appInfo.dataDir);
		    Log.i("App", appInfo.loadLabel(pm)+","+appInfo.packageName+","+appInfo.publicSourceDir+","+appInfo.dataDir);
		    if ( appInfo.publicSourceDir != null && appInfo.publicSourceDir.startsWith("/system")){
		    	sysApps.add(app);
		    }
		    else{
		    	userApps.add(app);		    	
		    }
		}
		Collections.sort(sysApps);
		Collections.sort(userApps);
		
		@SuppressWarnings("unchecked")
		List<AppInfo>[] apps = (List<AppInfo>[]) new ArrayList[2];
		apps[0] = sysApps;
		apps[1] = userApps;
		return apps;
	}
}
