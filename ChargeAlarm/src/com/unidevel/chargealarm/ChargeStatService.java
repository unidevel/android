package com.unidevel.chargealarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ChargeStatService extends Service
{
	NotificationManager nm;
	PendingIntent pi;
	Handler handler;
	public IBinder onBind(Intent i)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// handleCommand(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}
	
	@Override
    public void onCreate() {
		this.nm = (NotificationManager)getSystemService( NOTIFICATION_SERVICE );
		this.handler = new Handler();

        // Display a notification about us starting.
        showNotification();
		startStat();
    }
	
	public void startStat(){
		// AlarmManager am = (AlarmManager)
		// this.getSystemService(Context.ALARM_SERVICE);
		// Intent i=new Intent("ChargeStat");
		// pi=PendingIntent.getBroadcast(this, 0, i, 0);
		// am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
		// 1000L, pi);
		handler.postDelayed( new Runnable()
		{

			@Override
			public void run()
			{
				doStat();
			}

		}, 1000L );
	}
	
	public void doStat()
	{
		int state = getState();
		Log.i( "doStat", "" + state );
		if ( state >= 100 )
		{
			playNotify( this );
			stopStat();
			stopSelf();
			return;
		}
		startStat();
	}

	public void stopStat(){
		AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        nm.cancel(R.string.charge_start);//R.string.remote_service_started);

        // Tell the user we stopped.
       // Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();
    }
	
	private void showNotification() 
	{
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.charge_start);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
													 System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
																new Intent(this, Info.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name),
										text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        nm.notify(R.string.charge_start, notification);
    }
	
	
	public void playNotify(Context context){
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {
			Log.e("play", e.getMessage(),e);
		}
	}


	public int getState(){
		Intent batteryStatus=this.registerReceiver(null,
												   new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level *100 / (float)scale+0.5f;
		return (int)batteryPct;
	}
	
}
