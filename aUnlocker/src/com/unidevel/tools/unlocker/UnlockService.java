package com.unidevel.tools.unlocker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.*;

public class UnlockService extends Service implements SensorEventListener
{	
	WakeLock lock;
	AbstractDetector rd;
	ScreenReceiver receiver=null;
	public static final long TIMEOUT=3000;
	boolean enableLock = false;
	boolean enableUnlock = true;
	boolean isDetectorStarted = false;
	public IBinder onBind(Intent p1)
	{
		return null;
	}

	private void screenOn()
	{
		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		if (pm == null)
		{
			Log.e("unidevel.ScreenOn", "Can't get PowerManager");
			return;
		}
		WakeLock wakeLock=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ScreenOnLock");
		if (wakeLock != null)
		{
			wakeLock.acquire(TIMEOUT);
		}
		else
		{
			Log.e("unidevel.ScreenOn", "Can't create new full wake lock");
		}
	}

	private void screenOff()
	{
		Log.i("unidevel.UnlockService", "lock screen");
		try
		{
			Intent intent=new Intent();
			ComponentName name=new ComponentName("com.unidevel.tools.locker", "com.unidevel.tools.locker.ActionActivity");
			intent.setComponent(name);
			intent.putExtra("action", 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		catch (Throwable ex)
		{
			Log.e("unidevel.screenOff", ex.getMessage(), ex);
		}
	}

	@Override
	public void onCreate()
	{
		Intent intent=new Intent(ScreenReceiver.BOOT_SERVICE);
		sendBroadcast(intent);
		
	//	Intent intent=new Intent("com.unidevel.tools.UnlockService");
	//	startService(intent);
		Log.i("unidevel.UnlockService", "onCreate");
		super.onCreate();
	}

	public boolean isLockEnabled()
	{

		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		return pref.getBoolean("aunlocker.lock", false);
	}

	public boolean isUnlockEnabled()
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		return pref.getBoolean("aunlocker.unlock", true);
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("unidevel.UnlockService", "onStartCommand");
		if ( intent!=null&&intent.hasExtra("lock") && intent.hasExtra("unlock") )
		{
			boolean lock=intent.getBooleanExtra("lock", false);
			boolean unlock=intent.getBooleanExtra("unlock", true);
			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
			if(pref!=null){
				SharedPreferences.Editor edit=pref.edit();
				edit.putBoolean("aunlocker.lock", lock);
				edit.putBoolean("aunlocker.unlock", unlock);
				edit.commit();
			}
		}
		if (this.receiver == null)
		{
			Log.i("unidevel.UnlockService", "onStartCommand.registerReceiver");
			this.receiver = new ScreenReceiver();
			this.receiver.setService(this);
			IntentFilter it=new IntentFilter();
			it.addAction(Intent.ACTION_SCREEN_OFF);
			it.addAction(Intent.ACTION_SCREEN_ON);
			registerReceiver(this.receiver, it);

			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			if (!pm.isScreenOn())
			{
				Log.i("unidevel.UnlockService", "onStartCommand init with screen off");
				onScreenOff();
			}
			else
			{
				Log.i("unidevel.UnlockService", "onStartCommand init with screen on");
				onScreenOn();
			}			
		}
		return Service.START_STICKY;
	}

	private void startDetector()
	{
		if (!this.isDetectorStarted)
		{
			SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
			Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			this.isDetectorStarted = true;
		}
	}

	@SuppressWarnings("deprecation")
	public void onScreenOff()
	{
		if (this.isUnlockEnabled())
		{
			Log.i("onScreenOff", "unlock enabled");
			rd = new RotationDetector();
			rd.setStamp(System.currentTimeMillis()+TIMEOUT);
			this.startDetector();
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			this.lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, UnlockService.class.getName());
			this.lock.acquire();

			Notification n = new Notification(R.drawable.ic_launcher, "Unlocking", System.currentTimeMillis());
			Intent i=new Intent();
			PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
			n.setLatestEventInfo(this, "aUnlocker", "waiting for screen on", pi);
			n.flags |= Notification.FLAG_NO_CLEAR;
			this.startForeground(1, n);
		}
		else
		{
			Log.i("onScreenOff", "unlock disabled");
			this.stopDetector();
		}
	}

	@Override
	public void onDestroy()
	{
	//	Intent intent=new Intent(ScreenReceiver.BOOT_SERVICE);
	//	sendBroadcast(intent);
		Log.i("unidevel.UnlockService", "onDestroy");
		super.onDestroy();
		if (this.lock != null)
		{
			this.lock.release();
			this.lock = null;
		}
		stopDetector();
		if (this.receiver != null)
		{
			unregisterReceiver(this.receiver);
			this.receiver = null;
		}
		System.exit(0);
	}

	private void stopDetector()
	{
		if (this.isDetectorStarted)
		{
			SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
			sm.unregisterListener(this);
			if (this.rd != null)
			{
				this.rd = null;
			}
			this.isDetectorStarted = false;
		}
	}

	public void onScreenOn()
	{
		if (this.lock != null)
		{
			this.lock.release();
			this.lock = null;
		}
		this.stopForeground(true);

		if (this.isLockEnabled())
		{
			Log.i("onScreenOn", "lock enabled");
			rd = new LockDetector();
			this.startDetector();
		}
		else
		{
			Log.i("onScreenOn", "lock disabled");
			this.stopDetector();
		}
	}

	public void onSensorChanged(SensorEvent e)
	{
		if ( !isDetectorStarted ) return;
		AbstractDetector rd = this.rd;
		if ( rd != null ) rd.input(e.values[0], e.values[1], e.values[2]);
		else return;
		if (rd != null && rd.isMatch())
		{
			if (rd instanceof RotationDetector)
			{
				Log.i("unidevel.sensor", "screen on");
				screenOn();
			}
			else if (rd instanceof LockDetector)
			{
				Log.i("unidevel.sensor", "screen off");
				screenOff();
			}
		}
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
	}
}
