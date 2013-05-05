package com.unidevel.miboxhome;

import android.app.Application;

public class MiBoxApplication extends Application
{
	private static MiBoxApplication instance;

	public static MiBoxApplication getInstance()
	{
		return instance;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		instance = null;
	}
}
