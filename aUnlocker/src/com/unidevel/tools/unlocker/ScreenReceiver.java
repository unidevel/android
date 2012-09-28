package com.unidevel.tools.unlocker;

import android.content.*;
import android.os.*;
import android.util.*;

public class ScreenReceiver extends BroadcastReceiver
{
	public static final String BOOT_SERVICE="com.unidevel.tools.BootService";
	UnlockService service;
	public void setService(UnlockService service)
	{
		this.service = service;
	}

	@Override
	public void onReceive(Context ctx, Intent it)
	{
		Log.i("unidevel.ScreenReceiver.onRecive", "Action:"+it.getAction()+",service:"+this.service);
		if (Intent.ACTION_SCREEN_OFF.equals(it.getAction()))
		{
			this.service.onScreenOff();
		}
		else if (Intent.ACTION_SCREEN_ON.equals(it.getAction()))
		{
			this.service.onScreenOn();
		}
		else if (BOOT_SERVICE.equals(it.getAction()))
		{
			Intent serviceIntent=new Intent("com.unidevel.tools.UnlockService");
			ctx.startService(serviceIntent);
		}
	}
}
