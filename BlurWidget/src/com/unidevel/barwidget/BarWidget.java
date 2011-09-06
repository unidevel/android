package com.unidevel.barwidget;

import java.io.File;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

class BarWidget extends AppWidgetProvider {
	static final int[] imageIds = new int[] { R.id.imageProg1, R.id.imageProg2,
			R.id.imageProg3, R.id.imageProg4, R.id.imageProg5, R.id.imageProg6,
			R.id.imageProg7, R.id.imageProg8, R.id.imageProg9,
			R.id.imageProg10, R.id.imageProg11, R.id.imageProg12,
			R.id.imageProg13, R.id.imageProg14, R.id.imageProg15,
			R.id.imageProg16, R.id.imageProg17, R.id.imageProg18,
			R.id.imageProg19, R.id.imageProg20, R.id.imageProg21,
			R.id.imageProg22, R.id.imageProg23, R.id.imageProg24,
			R.id.imageProg25, R.id.imageProg26, R.id.imageProg27,
			R.id.imageProg28, R.id.imageProg29, R.id.imageProg30,
			R.id.imageProg30, R.id.imageProg31, R.id.imageProg32,
			R.id.imageProg33, R.id.imageProg34, R.id.imageProg35, };

	// R.id.imageProg32, R.id.imageProg33, R.id.imageProg34,
	// R.id.imageProg35, R.id.imageProg36,};
	// R.id.imageProg6, R.id.imageProg7 };

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			SharedPreferences prefs = context.getSharedPreferences(
					BarWidgetConfigure.PREFS_NAME, Context.MODE_PRIVATE);
			int nPrograms = prefs.getInt(BarWidgetConfigure
					.getAppsKey(appWidgetId), 0);
			boolean checked = prefs.getBoolean(BarWidgetConfigure
					.getSideKey(appWidgetId), false);
			int widgetResId = prefs.getInt(BarWidgetConfigure
					.getWidgetResIdKey(appWidgetId), 0);
			if (widgetResId != 0)
				updateWidget(context, getConfigureClass(), appWidgetManager,
						appWidgetId, widgetResId, checked, nPrograms);
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		SharedPreferences prefs = context.getSharedPreferences(
				BarWidgetConfigure.PREFS_NAME, Context.MODE_PRIVATE);
		for (int widgetId : appWidgetIds) {
			int nPrograms = prefs.getInt(BarWidgetConfigure
					.getAppsKey(widgetId), 0);
			Editor edit = prefs.edit();
			edit.remove(BarWidgetConfigure.getAppsKey(widgetId));
			edit.remove(BarWidgetConfigure.getSideKey(widgetId));
			edit.remove(BarWidgetConfigure.getTitleImageKey(widgetId));
			edit.remove(BarWidgetConfigure.getTitleKey(widgetId));
			edit.remove(BarWidgetConfigure.getWidgetResIdKey(widgetId));
			for (int i = 0; i < nPrograms; ++i) {
				edit.remove(BarWidgetConfigure.getAppClassKey(widgetId, i));
				edit.remove(BarWidgetConfigure.getAppNameKey(widgetId, i));
				edit.remove(BarWidgetConfigure.getAppPackageKey(widgetId, i));
			}
			edit.commit();
		}
	}

	public static void updateWidget(Context context, Class<?> configClass,
			AppWidgetManager appWidgetManager, int appWidgetId,
			int appWidgetResId, boolean checked, int nPrograms) {
		SharedPreferences prefs = context.getSharedPreferences(
				BarWidgetConfigure.PREFS_NAME, Context.MODE_PRIVATE);
		RemoteViews view = new RemoteViews(context.getPackageName(),
				appWidgetResId);
		String title = prefs.getString(BarWidgetConfigure
				.getTitleKey(appWidgetId), "");
		int color = prefs.getInt(BarWidgetConfigure
				.getTitleColorKey(appWidgetId), 0);
		int titleResId = checked ? R.id.imageTitle2 : R.id.imageTitle1;
		view.setTextColor(titleResId, color);
		// view.setInt(titleResId, "setBackgroundColor", color);
		if (title != null && title.length() > 0) {
			view.setViewVisibility(titleResId, View.VISIBLE);
			view.setTextViewText(titleResId, title);
		} else {
			view.setTextViewText(titleResId, "");
			view.setViewVisibility(titleResId, View.GONE);
		}
		view.setViewVisibility(checked ? R.id.imageTitle1 : R.id.imageTitle2,
				View.GONE);
		{
			Intent intent = new Intent();
			intent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(context, configClass);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

			// make sure requestCode different, so that we can get the unique
			// PendingIntent
			PendingIntent pendingIntent = PendingIntent.getActivity(context,
					(int) System.currentTimeMillis(), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			view.setOnClickPendingIntent(titleResId, pendingIntent);
			if (title == null || title.length() == 0)
				view.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
		}

		for (int i = 0, n = 0; i < nPrograms && n < imageIds.length; ++i, ++n) {
			String pkg = prefs.getString(BarWidgetConfigure.getAppPackageKey(
					appWidgetId, i), null);
			String cls = prefs.getString(BarWidgetConfigure.getAppClassKey(
					appWidgetId, i), null);
			String iconFile = prefs.getString(BarWidgetConfigure.getAppIconKey(
					appWidgetId, i), null);
			if (pkg != null) {
				Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setClassName(pkg, cls);
				final List<ResolveInfo> pkgs = context.getPackageManager()
						.queryIntentActivities(intent, 0);
				if (pkgs.size() > 0) {
					File file = null;
					if (iconFile != null)
						file = new File(iconFile);
					Bitmap icon = null;
					if (file != null && file.exists()) {
						icon = BitmapFactory.decodeFile(file.getPath());
					}
					if (icon == null) {
						icon = ((BitmapDrawable) pkgs.get(0).loadIcon(
								context.getPackageManager())).getBitmap();
					}
					if (icon != null) {
						view.setImageViewBitmap(imageIds[n], icon);
						intent = context.getPackageManager()
								.getLaunchIntentForPackage(pkg);
						if (intent == null) {
							intent = new Intent(Intent.ACTION_MAIN, null);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
						}
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClassName(pkg, cls);
						PendingIntent pendingIntent = PendingIntent
								.getActivity(context, (int) System
										.currentTimeMillis(), intent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						view
								.setOnClickPendingIntent(imageIds[n],
										pendingIntent);
					}
				}
			}
		}
		appWidgetManager.updateAppWidget(appWidgetId, view);
	}

	protected Class<?> getConfigureClass() {
		return null;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.i("BarWidget", intent.getAction());
		try {
		if ( Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) ) {
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
			onUpdate(context, appWidgetManager, appIds );
		} 
		}catch(Throwable ex){
			Log.e("BarWidget", ex.getMessage(), ex);
		}
		Log.i("BarWidget", intent.getAction());
	}
}
