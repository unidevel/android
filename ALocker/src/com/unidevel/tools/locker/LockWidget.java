package com.unidevel.tools.locker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class LockWidget extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		final int N = appWidgetIds.length;
		if (N == 0)
			return;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.lock);
            Intent intent = new Intent(LockService.NAME);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.imgLock, pendingIntent);

			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

	}
}