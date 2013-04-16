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
import android.widget.RemoteViews;

public class CellWidget extends AppWidgetProvider {
		@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			SharedPreferences prefs = context.getSharedPreferences(
					BarWidgetConfigure.PREFS_NAME, Context.MODE_PRIVATE);
			int widgetResId = prefs.getInt(BarWidgetConfigure
					.getWidgetResIdKey(appWidgetId), 0);
			if (widgetResId != 0)
				updateWidget(context, getConfigureClass(), appWidgetManager,
						appWidgetId, widgetResId);
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
			int appWidgetResId) {
		SharedPreferences prefs = context.getSharedPreferences(
				BarWidgetConfigure.PREFS_NAME, Context.MODE_PRIVATE);
		RemoteViews view = new RemoteViews(context.getPackageName(),
				appWidgetResId);
		String title = prefs.getString(BarWidgetConfigure
				.getTitleKey(appWidgetId), "");
		int color = prefs.getInt(BarWidgetConfigure
				.getTitleColorKey(appWidgetId), 0);
//		view.setInt(R.id.widget_layout, "setBackgroundColor", color);
		view.setTextViewText(R.id.imageTitle, title);
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
			view.setOnClickPendingIntent(R.id.imageTitle, pendingIntent);
		}

		{
			String name = prefs.getString(BarWidgetConfigure.getAppNameKey(
					appWidgetId, 0), null);
			String pkg = prefs.getString(BarWidgetConfigure.getAppPackageKey(
					appWidgetId, 0), null);
			String cls = prefs.getString(BarWidgetConfigure.getAppClassKey(
					appWidgetId, 0), null);
			String iconFile = prefs.getString(BarWidgetConfigure.getAppIconKey(
					appWidgetId, 0), null);
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
					if ( title == null || title.length() == 0 ){
						view.setTextViewText(R.id.imageTitle, name);
					}
					if (icon != null) {
						view.setImageViewBitmap(R.id.imageProg, icon);
						intent = context.getPackageManager().getLaunchIntentForPackage( pkg );
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
						view.setOnClickPendingIntent(R.id.imageProg, pendingIntent);
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
			if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {

				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				int[] appIds = appWidgetManager
						.getAppWidgetIds(new ComponentName(context, this
								.getClass()));
				onUpdate(context, appWidgetManager, appIds);
			}
		} catch (Throwable ex) {
			Log.e("BarWidget", ex.getMessage(), ex);
		}
		Log.i("BarWidget", intent.getAction());
	}
}
