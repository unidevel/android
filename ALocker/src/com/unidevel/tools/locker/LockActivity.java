package com.unidevel.tools.locker;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;

public class LockActivity extends Activity
{
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		NotificationManager nm=(NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.icon; 
		CharSequence tickerText = "Hello"; 
		long when = System.currentTimeMillis();

		Notification n = new Notification(icon, tickerText, when);
		n.flags |= Notification.FLAG_NO_CLEAR;
		RemoteViews view = new RemoteViews(getPackageName(), R.layout.tools);
		{
			Intent i = new Intent(this, ActionActivity.class);
			i.putExtra("action", 1);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(this, 0, i,0);
			view.setOnClickPendingIntent(R.id.imgLock, pi);
		}
		
		{
			Intent i = new Intent(this, ActionActivity.class);
			i.putExtra("action", 2);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(this, 1, i, 0);
			view.setOnClickPendingIntent(R.id.imgReboot, pi);
		}

		{
			Intent i = new Intent(this, ActionActivity.class);
			i.putExtra("action", 9);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(this, 2, i, 0);
			view.setOnClickPendingIntent(R.id.imgCancel, pi);
		}
		n.contentView = view;
		nm.notify(1, n);
	}
}
