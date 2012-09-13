package com.unidevel.tools.locker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

public class LockService extends Service {
	public static final String NAME = "LockService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("==========", "1.==========");
		super.onStart(intent, startId);
		Log.i("==========", "2.==========");
		this.enforceCallingOrSelfPermission(android.Manifest.permission.DEVICE_POWER, null);
		Log.i("==========", "3.==========");
		try {
			PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
			pm.goToSleep(SystemClock.uptimeMillis());
		}
		finally {
			this.stopSelf(startId);
		}
		Log.i("==========", "4.==========");
	}

}
