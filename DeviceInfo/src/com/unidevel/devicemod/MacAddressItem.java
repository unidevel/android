package com.unidevel.devicemod;

import android.content.Context;

public class MacAddressItem extends AbstractDeviceInfoItem{

	public MacAddressItem(Context context) {
		super(context);
	}

	public String getKey()
	{
		return "mac_address";
	}

	public String getLabel()
	{
		return context.getString(R.string.mac_address);
	}

	public String getValue()
	{
		return DeviceUtil.getMacAddress(context);
	}

	public void setValue(String newValue) throws Exception
	{
		DeviceUtil.setMacAddress(newValue);
	}
}
