package com.unidevel.tools.locker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class MainActivity extends Activity
{
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		NotificationManager nm=(NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

		Notification n = new Notification();
		n.icon = R.drawable.icon;
		n.when = System.currentTimeMillis();
		n.flags |= Notification.FLAG_NO_CLEAR;
		{
			Intent i = new Intent(this, ActionActivity.class);
			i.putExtra("action", 0);
			PendingIntent pi = PendingIntent.getActivity(this, -1, i,0);
			n.contentIntent = pi;	
		}
		
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
			i.putExtra("action", 3);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(this, 2, i, 0);
			view.setOnClickPendingIntent(R.id.imgShutdown, pi);
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
