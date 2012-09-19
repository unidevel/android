package com.unidevel.devicemod;

import android.content.Context;

public class SubscriberIdItem extends AbstractDeviceInfoItem {
	public SubscriberIdItem(Context context) {
		super(context);
	}
	
	public String getKey()
	{
		return "subscriber_id";
	}

	public String getLabel()
	{
		return context.getString(R.string.subscriber_id);
	}

	public String getValue()
	{
		return DeviceUtil.getSubscriberId(context);
	}
}
