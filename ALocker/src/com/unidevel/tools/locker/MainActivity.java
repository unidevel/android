package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.text.method.*;
import android.text.util.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.google.ads.*;
import com.unidevel.*;
import com.unidevel.util.*;

public class MainActivity extends Activity
{
	public static final String PREF_USE_ROOT = "root";
	public static final String PREF_START_ALOCKER_WHEN_BOOT = "boot";
	public static final String PREF_ENABLE_AUNLOCKER = "unlock";
	public static final String PREF_SHOW_CLOSE_BUTTON = "show_close";
	MainActivity ctx=this;
	boolean isRooted = false;
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		this.setContentView(R.layout.main);
		isRooted = RootUtil.isRooted();
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean useRoot = pref.getBoolean(PREF_USE_ROOT, isRooted);
		pref.edit().putBoolean(PREF_USE_ROOT, useRoot).commit();
		enableShutdownApp(ctx,useRoot);
		
		// Option use root to lock
		((CheckBox)findViewById(R.id.useRoot)).setChecked(useRoot);
		if ( !isRooted ) findViewById(R.id.useRoot).setEnabled(false);
		else {
			findViewById(R.id.useRoot).setEnabled(true);
			((CheckBox)findViewById(R.id.useRoot)).setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton button, boolean checked) {
					pref.edit().putBoolean(PREF_USE_ROOT, checked).commit();
					showNotify(ctx);
					enableShutdownApp(ctx,checked);
				}
			});
		}

		// ChangeLog
		TextView changelog=(TextView) findViewById(R.id.changelog);
		changelog.setText(Html.fromHtml(getString(R.string.changelog)));
		
		// Option start alocker when boot
		boolean showOnStatus=pref.getBoolean(PREF_START_ALOCKER_WHEN_BOOT,true);
		if(showOnStatus){
			showNotify(this);
		}
		findViewById(R.id.checkClose).setEnabled(showOnStatus);
		
		CheckBox box=(CheckBox) this.findViewById(R.id.checkBoot);
		box.setChecked(pref.getBoolean(PREF_START_ALOCKER_WHEN_BOOT,true));
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
					pref.edit().putBoolean(PREF_START_ALOCKER_WHEN_BOOT,value).commit();
					if(value){
						showNotify(ctx);
					}
					else{
						NotificationManager nm=(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
						nm.cancel(1);
					}
					findViewById(R.id.checkClose).setEnabled(value);
				}
			
		});
		
		//Show close button
		box=(CheckBox) this.findViewById(R.id.checkClose);
		box.setChecked(pref.getBoolean(PREF_SHOW_CLOSE_BUTTON,true));
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
					pref.edit().putBoolean(PREF_SHOW_CLOSE_BUTTON,value).commit();
					showNotify(ctx);
				}

			});
		
		
		// Option for shortcut
		loadSlots();

		// Option enable aUnlock service
		box=(CheckBox) this.findViewById(R.id.checkUnlocker);
		boolean aUnlockerEnabled=pref.getBoolean(PREF_ENABLE_AUNLOCKER,true);
		box.setChecked(aUnlockerEnabled);
		findViewById(R.id.checkFlipLock).setEnabled(aUnlockerEnabled);
		findViewById(R.id.checkFlipUnlock).setEnabled(aUnlockerEnabled);
		
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
					pref.edit().putBoolean(PREF_ENABLE_AUNLOCKER,value).commit();
					if(value){
						startUnlocker(ctx);
					}
					else{
						stopUnlocker(ctx);
					}
					findViewById(R.id.checkFlipLock).setEnabled(value);
					findViewById(R.id.checkFlipUnlock).setEnabled(value);
				}
			});
		
		ToggleButton button=(ToggleButton) this.findViewById(R.id.checkFlipLock);
		button.setChecked(pref.getBoolean("aunlocker.lock",false));
		button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					pref.edit().putBoolean("aunlocker.lock",value).commit();
					stopUnlocker(ctx);
					startUnlocker(ctx);
				}
		});
		
		button=(ToggleButton) this.findViewById(R.id.checkFlipUnlock);
		button.setChecked(pref.getBoolean("aunlocker.unlock",true));
		button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					pref.edit().putBoolean("aunlocker.unlock",value).commit();
					stopUnlocker(ctx);
					startUnlocker(ctx);
				}
			});
		
		
		TextView link=(TextView) findViewById(R.id.textDownloadUnlocker);
		link.setAutoLinkMask(Linkify.ALL);
		link.setText(Html.fromHtml(getString(R.string.download_unlocker)));
		
		link=(TextView) findViewById(R.id.changelog);
		//link.setAutoLinkMask(Linkify.ALL);
		//link.setText(Html.fromHtml(getString(R.string.changelog)));
		link.setMovementMethod	(LinkMovementMethod.getInstance ());
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
			text.setText(getString(R.string.add_shortcut));
		else if(name!=null)
			text.setText(name);
		ImageView image = (ImageView) findViewById(R.id.imageSlot1);
		Drawable icon=getAppIcon(this,pkg);
		if(icon!=null)
			image.setImageDrawable(icon);
		else 
			image.setImageResource(R.drawable.app);
		View.OnClickListener l =new View.OnClickListener(){
			public void onClick(View view)
			{
				Intent i=new Intent(ctx,AppListActivity.class);
			//	Intent i=new Intent(Intent.ACTION_ALL_APPS);
				startActivityForResult(i,0);
			}
		};
		
		image.setOnClickListener(l);
		text.setOnClickListener(l);
	}
	
	public void onResume(){
		super.onResume();
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		if(checkUnlocker(this)){
			findViewById(R.id.checkUnlocker).setVisibility(View.VISIBLE);
			findViewById(R.id.layoutLock).setVisibility(View.VISIBLE);
			findViewById(R.id.layoutUnlock).setVisibility(View.VISIBLE);
			findViewById(R.id.textDownloadUnlocker).setVisibility(View.GONE);
			if(pref.getBoolean(PREF_ENABLE_AUNLOCKER,true)){
				startUnlocker(ctx);
			}
		}
		else{
			findViewById(R.id.checkUnlocker).setVisibility(View.GONE);
			findViewById(R.id.layoutLock).setVisibility(View.GONE);
			findViewById(R.id.layoutUnlock).setVisibility(View.GONE);
			findViewById(R.id.textDownloadUnlocker).setVisibility(View.VISIBLE);
		}
	}
	
	public void onPause(){
		super.onPause();
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
		int version = DeviceUtil.getSDKVersion();
		if ( version > 10 )
			showNotifyForSDK11(ctx);
		else 
			showNotifyForSDK8(ctx);
	}
	
	@SuppressWarnings("deprecation")
	public static void showNotifyForSDK8(Context ctx)
	{
		NotificationManager nm=(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(1);
		Notification n = new Notification(R.drawable.icon, ctx.getString(R.string.app_name), System.currentTimeMillis());
		n.flags |= Notification.FLAG_NO_CLEAR;
		{
			Intent i = new Intent(ctx, ActionUIActivity.class);
			//i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, 10, i, 0);
			n.setLatestEventInfo(ctx, ctx.getString(R.string.app_name), "", pi);
		}
		nm.notify(1, n);
	}
	
	public static void showNotifyForSDK11(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean isRooted = pref.getBoolean(PREF_USE_ROOT, false);
		
		NotificationManager nm=(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(1);
		Notification n = new Notification();
		n.icon = R.drawable.icon;
		n.when = System.currentTimeMillis();
		n.flags |= Notification.FLAG_NO_CLEAR;
		{
			Intent i = new Intent(ctx, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		if ( pkgName!=null&&pkgName.length()>0 )
		{
			Intent i = ctx.getPackageManager().getLaunchIntentForPackage(pkgName);
//			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			if(i!=null){
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.slot1, pi);
			if(labelName!=null)view.setTextViewText(R.id.textSlot1,labelName);
			Drawable img=getAppIcon(ctx,pkgName);
			if(img!=null){
				Bitmap icon=((BitmapDrawable)img).getBitmap();
				view.setImageViewBitmap(R.id.imageSlot1,icon);
			}
			else{
				view.setImageViewResource(R.id.imageSlot1,R.drawable.app);
			}
			view.setViewVisibility(R.id.slot1,View.VISIBLE);
			}
			else{
				view.setViewVisibility(R.id.slot1,View.GONE);
			}
		}
		else{
			view.setViewVisibility(R.id.slot1,View.GONE);
		}
		
		boolean showClose=pref.getBoolean(PREF_SHOW_CLOSE_BUTTON,true);
		if(showClose)
		{
			view.setViewVisibility(R.id.labelCancel, View.VISIBLE);
			
			Intent i = new Intent(ctx, ActionActivity.class);
			i.putExtra("action", ActionActivity.ACTION_CANCEL);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pi = PendingIntent.getActivity(ctx, id++, i, 0);
			view.setOnClickPendingIntent(R.id.labelCancel, pi);
		}	
		else
		{
			view.setViewVisibility(R.id.labelCancel, View.GONE);
		}
		n.contentView = view;
		nm.notify(1, n);
	}
	
	public static Drawable getAppIcon(Context ctx,String pkg){
		try
		{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo info=pm.getPackageInfo(pkg, 0);
			return info.applicationInfo.loadIcon(pm);
		}
		catch(Throwable e)
		{
			return ctx.getResources().getDrawable(R.drawable.app);
		}
	}
	
	public static final String UNLOCKER_PKG="com.unidevel.tools.unlocker";
	public static final String UNLOCKER_SERVICE="com.unidevel.tools.UnlockService";
	
	public static boolean checkUnlocker(Context ctx){
		PackageManager pm=ctx.getPackageManager();
		try
		{
			PackageInfo info=pm.getPackageInfo(UNLOCKER_PKG, 0);
			return info!=null;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return false;
		}
	}

	public static void startUnlocker(Context ctx){
		Intent intent=new Intent(UNLOCKER_SERVICE);
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean lock= pref.getBoolean("aunlocker.lock",false);
		boolean unlock=pref.getBoolean("aunlocker.unlock",true);
		intent.putExtra("lock",lock);
		intent.putExtra("unlock",unlock);
		Log.i("startService","Lock:"+lock+",Unlock:"+unlock);
		ctx.startService(intent);
	}
	
	public static void stopUnlocker(Context ctx){
		Intent intent=new Intent(UNLOCKER_SERVICE);
		ctx.stopService(intent);
	}
	
	public static void enableShutdownApp(Context ctx, boolean isEnabled){
		PackageManager pm=ctx.getPackageManager();
		ActivityInfo info=null;
		ComponentName name=new ComponentName(ctx,PowerActivity.class);
		try
		{
			info=pm.getActivityInfo(name, PackageManager.GET_META_DATA);
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		if (isEnabled&&info==null)
		{
			pm.setComponentEnabledSetting(name,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
		}
		else if(!isEnabled&&info!=null){
			pm.setComponentEnabledSetting(name,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
		}
	}
}
