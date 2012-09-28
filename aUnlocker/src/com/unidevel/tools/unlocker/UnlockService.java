package com.unidevel.tools.unlocker;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class UnlockService extends Service implements SensorEventListener
{	
	WakeLock lock;
	RotationDetector rd;
	ScreenReceiver receiver=null;
	public static final long TIMEOUT=3000;
	public IBinder onBind(Intent p1)
	{
		return null;
	}
	
	private void screenOn(){
		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		if ( pm == null ) {
			Log.e("unidevel.ScreenOn", "Can't get PowerManager");
			return;
		}
		WakeLock lock=pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "ScreenOnLock");
		if ( lock != null )lock.acquire(TIMEOUT);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("unidevel.UnlockService","start");
		Intent intent=new Intent(ScreenReceiver.BOOT_SERVICE);
		sendBroadcast(intent);
	}
	
	public int onStartCommand(Intent intent,int flags, int startId){
		Log.i("unidevel.UnlockService","onStartCommand");
		if(this.receiver==null){
			Log.i("unidevel.UnlockService","registerReceiver");
			this.receiver=new ScreenReceiver();
			this.receiver.setService(this);
			IntentFilter it=new IntentFilter();
			it.addAction(Intent.ACTION_SCREEN_OFF);
			it.addAction(Intent.ACTION_SCREEN_ON);
			registerReceiver(this.receiver,it);
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			if(!pm.isScreenOn()){
				Log.i("unidevel.UnlockService","init with screen off");
				onScreenOff();
			}
		}
		return Service.START_STICKY;
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
	public void onDestroy() {
		Log.i("unidevel.UnlockService","stop");
		super.onDestroy();
		unregisterReceiver(this.receiver);
		this.receiver=null;
	}

	public void onScreenOn()
	{
		if ( lock != null ){
			lock.release();
			lock = null;
		}
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this);
	}
	
	public void onSensorChanged(SensorEvent e)
	{
		rd.input(e.values[0], e.values[1], e.values[2]);
		if ( rd.isMatch() ) {
			Log.i("unidevel.sensor","screen on");
			screenOn();
		}
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
	}
}
