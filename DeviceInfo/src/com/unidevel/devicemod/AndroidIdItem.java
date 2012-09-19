package com.unidevel.devicemod;

import android.content.Context;

public class AndroidIdItem extends AbstractDeviceInfoItem {

	public AndroidIdItem(Context context) {
		super(context);
	}

	public String getLabel()
	{
		return context.getString(R.string.android_id);
	}

	public String getKey()
	{
		return "android_id";
	}

	public String getValue()
	{
		return DeviceUtil.getAndroidId(context);
	}

	@Override
	public boolean supportEdit() {
		return true;
	}
	
	public void setValue(String newValue) throws Exception
	{
		DeviceUtil.setAndroidId(newValue);
	}
}
