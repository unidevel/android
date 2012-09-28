package com.unidevel.tools.unlocker;

import android.app.*;
import android.content.*;
import android.hardware.*;
import android.os.*;
import android.os.PowerManager.*;
import android.util.*;


public class HoldService extends Service implements ScreenListener,SensorEventListener
{
	WakeLock lock;
	RotationDetector rd;
	ScreenReceiver receiver;
	public IBinder onBind(Intent p1)
	{
		return null;
	}

	private void screenOn()
	{
		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		if (pm == null)
		{
			Log.e("ScreenOn", "Can't get PowerManager");
			return;
		}
		WakeLock lock=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ScreenOnLock");
		lock.acquire();
		lock.release();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i("UnlockService", "start");
		receiver = new ScreenReceiver(this);
		IntentFilter it=new IntentFilter();
		it.addAction(Intent.ACTION_SCREEN_OFF);
		it.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(this.receiver, it);
	}

	@SuppressWarnings("deprecation")
	public void onScreenOff()
	{
		rd = new RotationDetector();
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, UnlockService.class.getName());
		lock.acquire();
	}

	@Override
	public void onDestroy()
	{
		Log.i("UnlockService", "stop");
		super.onDestroy();
		unregisterReceiver(this.receiver);
	}

	public void onScreenOn()
	{
		if (lock != null)
		{
			lock.release();
			lock = null;
		}
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this);
	}

	public void onSensorChanged(SensorEvent e)
	{
		rd.input(e.values[0], e.values[1], e.values[2]);
		if (rd.isMatch())
		{
			Log.i("sensor", "screen on");
			screenOn();
		}
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
	}
}
