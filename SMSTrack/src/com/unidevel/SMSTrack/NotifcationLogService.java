package com.unidevel.SMSTrack;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotifcationLogService extends AccessibilityService
{

	static final String TAG = "NotificationLogService";
	
	private String getEventType(AccessibilityEvent event)
	{
		switch (event.getEventType())
		{
			case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
				return "TYPE_NOTIFICATION_STATE_CHANGED";
			case AccessibilityEvent.TYPE_VIEW_CLICKED:
				return "TYPE_VIEW_CLICKED";
			case AccessibilityEvent.TYPE_VIEW_FOCUSED:
				return "TYPE_VIEW_FOCUSED";
			case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
				return "TYPE_VIEW_LONG_CLICKED";
			case AccessibilityEvent.TYPE_VIEW_SELECTED:
				return "TYPE_VIEW_SELECTED";
			case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
				return "TYPE_WINDOW_STATE_CHANGED";
			case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
				return "TYPE_VIEW_TEXT_CHANGED";
		}
		return "default";
	}

	private String getEventText(AccessibilityEvent event)
	{
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (CharSequence s : event.getText())
		{
			sb.append(i).append('.').append(s);
		}
		return sb.toString();
	}
	
	private String s(CharSequence s)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(s);
		return sb.toString();
	}
	

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event)
	{
		if(event.getEventType()==AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
		Log.v(TAG, String.format(
					  "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s [btext] %s [des] %s",
				  getEventType(event), event.getClassName(), event.getPackageName(),
				  event.getEventTime(), getEventText(event),
				  s(event.getBeforeText()), s(event.getContentDescription())));
	}

	@Override
	public void onInterrupt()
	{
		Log.v(TAG, "onInterrupt");
	}

	@Override
	protected void onServiceConnected()
	{
		super.onServiceConnected();
		Log.v(TAG, "onServiceConnected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.flags = AccessibilityServiceInfo.DEFAULT;
		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		setServiceInfo(info);
	}

}
