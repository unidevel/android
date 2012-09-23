package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.google.ads.*;
import com.unidevel.util.*;

public class MainActivity extends Activity
{
	MainActivity ctx=this;
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		this.setContentView(R.layout.main);
		
		TextView changelog=(TextView) findViewById(R.id.changelog);
		changelog.setText(Html.fromHtml(getString(R.string.changelog)));
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
		boolean isRooted = RootUtil.isRooted();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putBoolean("root", isRooted).commit();
		
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
		int id = 1;
		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", ActionActivity.ACTION_LOCK);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelLock, pi);
		}

		if (isRooted){
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", ActionActivity.ACTION_SHUTDOWN);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelShutdown, pi);
		}
		else {
			view.setViewVisibility(R.id.labelShutdown, View.GONE);
		}

		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", ActionActivity.ACTION_VOLUME_DOWN);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelVolDown, pi);
		}

		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", ActionActivity.ACTION_VOLUME_UP);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelVolUp, pi);
		}
		
		{
			Intent i = new Intent(ctx, AppListActivity.class);
			i.putExtra("action", ActionActivity.ACTION_ADD);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelPlus, pi);
		}
		
		{
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", ActionActivity.ACTION_CANCEL);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelCancel, pi);
		}	
		
		n.contentView = view;
		nm.notify(1, n);
	}
}
