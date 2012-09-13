package com.unidevel.nosleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoSleepReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(NoSleepService.NAME));
	}
}
