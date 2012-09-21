package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.widget.*;
import com.google.ads.*;

public class MainActivity extends Activity
{
	MainActivity ctx=this;
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		this.setContentView(R.layout.main);
		showNotify(this);
		CheckBox box=(CheckBox) this.findViewById(R.id.checkBoot);
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
					pref.edit().putBoolean("boot",value).commit();
				}
			
		});
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		box.setChecked(pref.getBoolean("boot",true));
		//ALocker a1505c63891818d
		AdView adView = new AdView(this, AdSize.BANNER, "a1505c63891818d"); 
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout); 
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);
	}

	public static void showNotify(Context ctx)
	{
		NotificationManager nm=(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);

		Notification n = new Notification();
		n.icon = R.drawable.icon;
		n.when = System.currentTimeMillis();
		n.flags |= Notification.FLAG_NO_CLEAR;
		{
			Intent i = new Intent(ctx, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, 10, i, 0);
			n.contentIntent = pi;	
		}

		RemoteViews view = new RemoteViews(ctx.getPackageName(), R.layout.tools);
		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", 1);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
			view.setOnClickPendingIntent(R.id.layoutLock, pi);
		}

		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", 2);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, 2, i, 0);
			view.setOnClickPendingIntent(R.id.imgShutdown, pi);
		}

		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", 9);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, 3, i, 0);
			view.setOnClickPendingIntent(R.id.imgCancel, pi);
		}
		n.contentView = view;
		nm.notify(1, n);
	}
}
