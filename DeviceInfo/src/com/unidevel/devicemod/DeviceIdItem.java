package com.unidevel.devicemod;

import android.content.Context;

public class DeviceIdItem extends AbstractDeviceInfoItem {

	public DeviceIdItem(Context context) {
		super(context);
	}

	public String getKey()
	{
		return "device_id";
	}

	public String getLabel()
	{
		return context.getString(R.string.device_id);
	}

	public String getValue()
	{
		return DeviceUtil.getDeviceId(context);
	}
}
