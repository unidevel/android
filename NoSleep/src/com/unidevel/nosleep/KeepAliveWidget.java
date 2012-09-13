package com.unidevel.nosleep;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class KeepAliveWidget extends AppWidgetProvider {
	Intent intent = new Intent(NoSleepService.NAME);
	RemoteViews views = null;
	RemoteViews getRemoteViews(Context context){
		if ( views == null ) {
			views = new RemoteViews(context.getPackageName(), R.layout.widget);
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, Main.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.viewStatus, pendingIntent);
		}
		return views;
	}
	
	private void onUpdateInternal(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds, boolean isStart) {
		Log.i("onUpdateInternal", "update");
		final int N = appWidgetIds.length;
		if ( N == 0 ) return;
		for (int i = 0; i < N; i++){
			int appWidgetId = appWidgetIds[i];

			// Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = getRemoteViews(context);
            if ( isStart ){
            	Log.i("================", "on");
            	views.setImageViewResource(R.id.viewStatus, R.drawable.on);
            }
            else {
            	Log.i("================", "off");
            	views.setImageViewResource(R.id.viewStatus, R.drawable.off);
            }

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("onUpdate", "update");
		IServiceState state = (IServiceState)this.peekService(context, intent);
		onUpdateInternal(context, appWidgetManager, appWidgetIds, state == null?false:state.isStarted());
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.i("KeepAliveWidget", "onReceive: "+intent);
		if ( NoSleepService.NOSLEEP_START.equals(intent.getAction()) || NoSleepService.NOSLEEP_STOP.equals(intent.getAction())) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(
	                context, KeepAliveWidget.class));
			onUpdateInternal(context, appWidgetManager, appIds, NoSleepService.NOSLEEP_START.equals(intent.getAction()));
		}
	}
}
