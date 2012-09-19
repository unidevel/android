package com.unidevel.devicemod;

import android.content.Context;

public class GoogleServiceIdItem extends AbstractDeviceInfoItem {

	public GoogleServiceIdItem(Context context) {
		super(context);
	}


	public String getLabel()
	{
		return context.getString(R.string.gsf_id);
	}

	public String getKey()
	{
		return "gsf_id";
	}

	public String getValue()
	{
		try
		{
			return DeviceUtil.getGoogleServiceId(context);
		}
		catch (Exception e)
		{}
		return "";
	}

	@Override
	public boolean supportEdit() {
		return true;
	}
	
	public void setValue(String newValue) throws Exception
	{
		DeviceUtil.setGoogleServiceId(newValue);
	}
}
