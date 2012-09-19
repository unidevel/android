package com.unidevel.devicemod;

import android.content.Context;

public class SerialNoItem extends AbstractDeviceInfoItem{

	public SerialNoItem(Context context) {
		super(context);
	}
	
	public String getKey()
	{
		return "serial_no";
	}

	public String getLabel()
	{
		return context.getString(R.string.serial_no);
	}

	public String getValue()
	{
		try
		{
			return DeviceUtil.getSerialNo();
		}
		catch (Exception e)
		{}
		return "";
	}

	public void setValue(String newValue) throws Exception
	{
		DeviceUtil.setSerialNo(newValue);
	}
}
