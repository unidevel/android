package com.unidevel.nosleep;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class NoSleepService extends Service {
	public static final String NAME = "NoSleepService";
	public static final String NOSLEEP_START = "com.unidevel.nosleep.start";
	public static final String NOSLEEP_STOP = "com.unidevel.nosleep.stop";
	public static final String ALARM_STOP = "com.unidevel.nosleep.alarm.stop";
	
	PowerManager.WakeLock lock;
	int timeout;
	Handler handler;
	Runnable unlockProc;
	Runnable stopProc;
	BroadcastReceiver screenOnReceiver;
	BroadcastReceiver screenOffReceiver;
	BroadcastReceiver stopAlarmReceiver;
	ScreenLocker locker;
	int startTime;
	int stopTime;
	
	PendingIntent stopIntent, startIntent;
	class ScreenLocker extends Binder implements IServiceState{
		boolean started;
		public ScreenLocker(){
			started = false;
			lock = null;
		}
		
		public void start(){
			if ( started ) return;
			started = true;
			lock();
			sendBroadcast(new Intent(NOSLEEP_START));
		}
		
		public void stop(){
			if (! started ) return;
			unlock();
			started = false;
			sendBroadcast(new Intent(NOSLEEP_STOP));
		}
		
		public void lock(){
			if ( !started ) return;
			if (lock == null) {
				Log.i("NOSLEEP", "==============");
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NoSleep");
				lock.acquire();
			}
		}
		
		public void unlock(){
			if ( !started ) return;
			if (lock != null) {
				Log.i("SLEEP", "==============");
				lock.release();
				lock = null;
				Log.i("NoSleepService", "release wake lock");
			}
		}
		
		public boolean isStarted() {
			return started;
		}

		@Override
		public int getTimeout() {
			return timeout;
		}

		@Override
		public void setTimeout(int minutes) {
			timeout = minutes;
		}

		@Override
		public int getStartTime() {
			return startTime;
		}

		@Override
		public int getStopTime() {
			return stopTime;
		}

		@Override
		public void setStartTime(int time) {
			if ( time == startTime ) return;
			startTime = time;
			AlarmManager alarm = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
			alarm.cancel(startIntent);
			if ( time >= 0 ) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				Log.i("Calendar", ""+cal.get(Calendar.HOUR_OF_DAY)+","+cal.get(Calendar.MINUTE)+""+cal.get(Calendar.SECOND));
				cal.set(Calendar.HOUR_OF_DAY, time/60);
				cal.set(Calendar.MINUTE, time%60);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
//				Log.i("setRepeating", "startAlarm "+cal.getTimeInMillis()+","+System.currentTimeMillis());
				if ( cal.getTimeInMillis() < System.currentTimeMillis() ) {
					cal.add(Calendar.HOUR_OF_DAY, 24);
				}
//				Log.i("setRepeating", "startAlarm "+cal.getTimeInMillis()+","+System.currentTimeMillis());
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, startIntent);
			}
		}

		@Override
		public void setStopTime(int time) {
			if ( time == stopTime ) return;
			stopTime = time;
			AlarmManager alarm = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
			alarm.cancel(stopIntent);
			if ( time >= 0 ) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				cal.set(Calendar.HOUR_OF_DAY, time/60);
				cal.set(Calendar.MINUTE, time%60);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
//				Log.i("setRepeating", "stopAlarm "+cal.getTimeInMillis()+","+System.currentTimeMillis());
				if ( cal.getTimeInMillis() < System.currentTimeMillis() ) {
					cal.add(Calendar.HOUR_OF_DAY, 24);
				}
//				Log.i("setRepeating", "stopAlarm "+cal.getTimeInMillis()+","+System.currentTimeMillis());
				alarm.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, stopIntent);
			}
		}

		@Override
		public boolean saveSettings() {
			SharedPreferences prefs = NoSleepService.this.getSharedPreferences(NAME, MODE_PRIVATE);
			Editor edit = prefs.edit();
			edit.putInt("timeout", timeout);
			edit.putInt("startTime", startTime);
			edit.putInt("stopTime", stopTime);
			return edit.commit();
		}
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("onBind", intent.toString());
		if ( locker == null ) {
			locker = new ScreenLocker();
		}
		return locker;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("onUnbind", intent.toString());
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = this.getSharedPreferences(NAME, MODE_PRIVATE);
		timeout = prefs.getInt("timeout", 0);

		Intent intent;
		intent = new Intent(NoSleepService.NAME);
		startIntent = PendingIntent.getService(this, 0, intent, 0);
		intent = new Intent(NoSleepService.ALARM_STOP);
		stopIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		
		locker = new ScreenLocker();
		locker.setStartTime(prefs.getInt("startTime", -1));
		locker.setStopTime(prefs.getInt("stopTime", -1));
		
		
//		startTime = prefs.getInt("startTime", -1);
//		stopTime = prefs.getInt("stopTime", -1);
		
		
		handler = new Handler();
		unlockProc = new Runnable(){
			@Override
			public void run() {
				Log.i("unlockProc", "============");
				locker.unlock();
			}
		};
		stopProc = new Runnable(){
			@Override
			public void run() {
				Log.i("stopProc", "============");
				locker.stop();
			}
		};
		screenOffReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("SCREEN_OFF", "===========");
				if ( timeout> 0 ) {
					handler.postDelayed(unlockProc, timeout*60000L);
				}
			}
		};
		screenOnReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("SCREEN_ON", "===========");
				try{
					if ( handler != null && unlockProc != null )
						handler.removeCallbacks(unlockProc);
					locker.lock();
				}
				catch(Throwable ex){}
			}
		};
		stopAlarmReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				stopSelf();
			}
		};
		registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(stopAlarmReceiver, new IntentFilter(ALARM_STOP));
		Log.i("NoSleepService", "onCreate");
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		locker.stop();
		if ( handler != null ){
			handler.removeCallbacks(unlockProc);
			handler.removeCallbacks(stopProc);
		}
		unregisterReceiver(screenOnReceiver);
		unregisterReceiver(screenOffReceiver);
		unregisterReceiver(stopAlarmReceiver);
		locker.saveSettings();
		Log.i("<===NoSleepService", "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i("===>NoSleepService", "onStart");
		locker.start();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		locker.stop();
	}

//	PendingIntent startIntent;
//	void setAlarm(){
//		AlarmManager alarm = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
//		if ( startTime > 0 ) {
//			
//		}
//	}
//	
//	void clearAlarm(){
//		AlarmManager alarm = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
//		if ( startIntent != null ) {
//			alarm.cancel(startIntent);
//		}
//	}
}
