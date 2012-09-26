package com.unidevel.tools.unlocker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class UnlockService extends Service implements SensorEventListener{
	static final float R_THRESHOLD = 0.05f;
	static final float R1_THRESHOLD = 0.4f;
	static final float R2_THRESHOLD = 0.5f;
	static final long INTERVAL=1000;
	class RotationDetector{
		int count;
		int state;
		long stamp;

		public RotationDetector()
		{
			this.state=0;
		}
		public void input(float x,float y, float z){
			if(state==0){
				stamp=System.currentTimeMillis();
			}
			if(y>R_THRESHOLD||y<-R_THRESHOLD){
				stamp=System.currentTimeMillis();
				state=0;
				return;
			}
			long now=System.currentTimeMillis();
			if(z>-R_THRESHOLD&&z<R_THRESHOLD){
				if(state==0){
					stamp=now;
					state=1;
				}
				else if(state==2){
					if(now-stamp<INTERVAL)
						state=3;
					else state=0;
					stamp=now;
				}
				else if ( now-stamp>INTERVAL) {
					state = 0;
					stamp = now;
				}
				return;
			}
			if(z>R1_THRESHOLD&&z<R2_THRESHOLD){
				if(now-stamp>INTERVAL){
					state = 0;
					stamp = now;
					return;
				}
				if(state==1){
					state=2;
					stamp=now;
				}
				else if(state==3){
					state=4;
					stamp=now;
				}
				return;
			}
			if(now-stamp>INTERVAL){
				state = 0;
				stamp = now;
			}
		}
		
		public boolean isMatch(){
			return state==4;
		}
		
		public String toString(){
			return "State:"+state;
		}
	}
	
	WakeLock lock;
	PowerManager pm;
	RotationDetector rd;
	SensorManager sm;
	Sensor sensor;
	@Override
	public IBinder onBind(Intent it) {
		return null;
	}
	
	private void screenOn(){
		if ( !pm.isScreenOn() ) {
			pm.userActivity(1, false);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		rd = new RotationDetector();
		sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		sensor=sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		sm.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
		pm = (PowerManager) getSystemService(POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, UnlockService.class.getName());
		lock.acquire();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		lock.release();
		sm.unregisterListener(this);
		lock = null;
		pm = null;
		sm = null;
	}
	
	public void onSensorChanged(SensorEvent e)
	{
		Log.i("UnlockService", "x:"+e.values[0]+"\ny:"+e.values[1]+"\nz:"+e.values[2]);
		rd.input(e.values[0], e.values[1], e.values[2]);
		Log.i("RatationDetector", rd.toString());
		if ( rd.isMatch() ) {
			screenOn();
			stopSelf();
		}
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
	}
}
