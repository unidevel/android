package com.unidevel.tools.unlocker;

import android.content.*;
import android.os.*;
import android.util.*;

public class ScreenReceiver extends BroadcastReceiver {
	ScreenListener service;
	public ScreenReceiver(ScreenListener service){
		this.service = service;
	}
	
	@Override
	public void onReceive(Context ctx, Intent it) {
		try {
			if (it.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				Log.i("ScreenService", "screen off");
				this.service.onScreenOff();
	        } else if (it.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				Log.i("ScreenService", "screen on");
	        	this.service.onScreenOn();
	        }
		}
		catch(Throwable ex){
			Log.e("onReceive", ex.getMessage(), ex);
		}
	}
}
