package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.text.*;
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
	MainActivity ctx=this;
	boolean isRooted = false;
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		this.setContentView(R.layout.main);
		isRooted = RootUtil.isRooted();
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean useRoot = pref.getBoolean("root", isRooted);
		pref.edit().putBoolean("root", useRoot).commit();

		TextView changelog=(TextView) findViewById(R.id.changelog);
		changelog.setText(Html.fromHtml(getString(R.string.changelog)));
		if(pref.getBoolean("boot",true))
			showNotify(this);
		CheckBox box=(CheckBox) this.findViewById(R.id.checkBoot);
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
					pref.edit().putBoolean("boot",value).commit();
					if(value){
						showNotify(ctx);
					}
					else{
						NotificationManager nm=(NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
						nm.cancel(1);
					}
				}
			
		});
		loadSlots();
		box.setChecked(pref.getBoolean("boot",true));
		((CheckBox)findViewById(R.id.useRoot)).setChecked(useRoot);
		if ( !isRooted ) findViewById(R.id.useRoot).setEnabled(false);
		else {
			findViewById(R.id.useRoot).setEnabled(true);
			((CheckBox)findViewById(R.id.useRoot)).setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton button, boolean checked) {
					pref.edit().putBoolean("root", checked).commit();
					showNotify(ctx);
				}
			});
		}
		box=(CheckBox) this.findViewById(R.id.checkUnlocker);
		box.setChecked(pref.getBoolean("unlock",true));
		
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton button, boolean value)
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
					pref.edit().putBoolean("unlock",value).commit();
					if(value){
						stopUnlocker(ctx);
					}
					else{
						startUnlocker(ctx);
					}
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
			if(pref.getBoolean("unlock",true)){
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
		boolean isRooted = pref.getBoolean("root", false);
		
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
}
