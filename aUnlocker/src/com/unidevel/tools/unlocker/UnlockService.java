package com.unidevel.tools.unlocker;

import android.app.*;
import android.content.*;
import android.hardware.*;
import android.os.*;
import android.os.PowerManager.*;
import android.util.*;

public class UnlockService extends Service implements SensorEventListener
{	
	
	WakeLock lock;
	PowerManager pm;
	RotationDetector rd;
	SensorManager sm;
	Sensor sensor;
	ScreenReceiver receiver;
	public IBinder onBind(Intent p1)
	{
		return null;
	}
	
	private void screenOn(){
	//	if ( !pm.isScreenOn() ) {
		//	pm.userActivity(SystemClock.uptimeMillis()+1, false);
		//}
		
		WakeLock lock=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "TempWakeLock");
		lock.acquire();// do the work that needs the visible display...// Release the newest wakelock and fall back to the old oneTempWakeL
		lock.release();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("UnlockService","start");
		receiver=new ScreenReceiver(this);
		IntentFilter it=new IntentFilter();
		it.addAction(Intent.ACTION_SCREEN_OFF);
		it.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(this.receiver,it);
	}

	public void onScreenOff()
	{
		rd = new RotationDetector();
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		pm = (PowerManager) getSystemService(POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, UnlockService.class.getName());
		lock.acquire();
	}
	
	@Override
	public void onDestroy() {
		Log.i("UnlockService","stop");
		super.onDestroy();
		unregisterReceiver(this.receiver);
		//onScreenOn();
	}

	public void onScreenOn()
	{
		lock.release();
		sm.unregisterListener(this);
		lock = null;
		pm = null;
		sm = null;
	}
	
	public void onSensorChanged(SensorEvent e)
	{
	//	Log.i("UnlockService", "x:"+e.values[0]+"\ny:"+e.values[1]+"\nz:"+e.values[2]);
		rd.input(e.values[0], e.values[1], e.values[2]);
		Log.i("RatationDetector", rd.toString());
		if ( rd.isMatch() ) {
			Log.i("sensor","screen on");
			screenOn();
		//	stopSelf();
		}
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
	}
}
