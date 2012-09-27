package com.unidevel.tools.unlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
	UnlockService service;
	public ScreenReceiver(UnlockService service){
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
