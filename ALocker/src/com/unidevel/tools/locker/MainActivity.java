package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
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
		loadSlots();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		box.setChecked(pref.getBoolean("boot",true));
		//ALocker a1505c63891818d
		AdView adView = new AdView(this, AdSize.BANNER, "a1505c63891818d"); 
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout); 
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);
	}

	private void loadSlots(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String pkg = pref.getString("slot1.pkg","");
		String name= pref.getString("slot1.name","");
		TextView text=(TextView) findViewById(R.id.textSlot1);
		if(pkg.length()==0)
			text.setText("点击添加快捷程序");
		else
			text.setText(name);
		ImageView image = (ImageView) findViewById(R.id.imageSlot1);
		image.setImageDrawable(getAppIcon(this,pkg));
		View.OnClickListener l =new View.OnClickListener(){

			public void onClick(View view)
			{
				Intent i=new Intent(ctx,AppListActivity.class);
				startActivityForResult(i,0);
			}

		};
		
		image.setOnClickListener(l);
		text.setOnClickListener(l);
	}
	
	public void onActivityResult(int req,int res,Intent intent){
		super.onActivityResult(req,res,intent);
		if(intent==null)return;
		String name=intent.getStringExtra("name");
		String pkg=intent.getPackage();
		if(pkg==null)return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putString("slot1.pkg",pkg).putString("slot1.name",name).commit();
		loadSlots();
		showNotify(ctx);
	}
	
	
	public static void showNotify(Context ctx)
	{
		boolean isRooted = RootUtil.isRooted();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putBoolean("root", isRooted).commit();
		
		NotificationManager nm=(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(1);
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
		
		String pkgName = pref.getString("slot1.pkg", "");
		String labelName = pref.getString("slot1.name", "");
		if ( pkgName.length()>0 )
		{
			Intent i = ctx.getPackageManager().getLaunchIntentForPackage(pkgName);
//			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.slot1, pi);
			view.setTextViewText(R.id.textSlot1,labelName);
			Bitmap icon=((BitmapDrawable)getAppIcon(ctx,pkgName)).getBitmap();
			view.setImageViewBitmap(R.id.imageSlot1,icon);
			view.setViewVisibility(R.id.slot1,View.VISIBLE);
		}
		else{
			view.setViewVisibility(R.id.slot1,View.GONE);
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
	
	private static Drawable getAppIcon(Context ctx,String pkg){
		try
		{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo info=pm.getPackageInfo(pkg, 0);
			return info.applicationInfo.loadIcon(pm);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return ctx.getResources().getDrawable(R.drawable.app);
		}
	}
}
