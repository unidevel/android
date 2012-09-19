package com.unidevel.devicemod;

import android.content.Context;

public abstract class AbstractDeviceInfoItem {
	private String savedValue;
	protected Context context;
	public abstract String getKey();
	public abstract String getLabel();
	public abstract String getValue();

	public AbstractDeviceInfoItem(Context context)
	{
		this.context = context;
		savedValue = "";
	}
	
	public String getSavedValue()
	{
		return savedValue;
	}
	
	public void setSavedValue(String savedValue)
	{
		this.savedValue = savedValue;
	}
	
	public void setValue(String newValue) throws Exception
	{
		
	}
	
	public boolean supportEdit()
	{
		return false;
	}

}
