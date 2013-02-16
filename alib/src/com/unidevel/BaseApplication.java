package com.unidevel;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;

public class BaseApplication extends Application {
	private Activity currentActivity = null;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	public Activity getCurrentActivity() {
		return currentActivity;
	}
}
