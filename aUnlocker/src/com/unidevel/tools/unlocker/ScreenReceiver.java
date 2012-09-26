package com.unidevel.tools.unlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent it) {
		Intent serviceIntent = new Intent(ctx, UnlockService.class);
		if (it.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			Log.i("ScreenService", "screen off");
			ctx.startService(serviceIntent);
        } else if (it.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			Log.i("ScreenService", "screen on");
        	ctx.stopService(serviceIntent);
        }
	}

}
