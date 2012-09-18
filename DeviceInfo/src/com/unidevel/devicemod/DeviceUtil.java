package com.unidevel.devicemod;

import android.content.*;
import android.database.*;
import android.net.*;
import android.net.wifi.*;
import android.provider.*;
import android.telephony.*;
import java.lang.reflect.*;

public class DeviceUtil
{
	public static String SQLITE_PATH="sqlite3";
	private static final Uri GSF_URI = Uri.parse("content://com.google.android.gsf.gservices");
	public static String getAndroidId(Context context) 
	{
       	String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
       	return androidId;
    }

    public static String getDeviceId(Context context)
	{
    	return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

	public static String getSubscriberId(Context context)
	{
    	return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }
	
    public static String getSerialNo() throws Exception
	{
    	String serialNo;
    	Class localClass1 = Class.forName("android.os.SystemProperties");
        Method localMethod1 = localClass1.getMethods()[2];
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = "ro.serialno";
        arrayOfObject1[1] = "Unknown";
        serialNo = (String)localMethod1.invoke(localClass1, arrayOfObject1);
        return serialNo; 
    }

    public static String getMacAddress(Context context) {
    	return ((WifiManager)context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }
	
	public static String getGoogleServiceId(Context context) throws Exception
    {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "android_id";
        Cursor localCursor = context.getContentResolver().query(GSF_URI, null, null, arrayOfString, null);
        if (localCursor==null || !localCursor.moveToFirst() || localCursor.getColumnCount() < 2) return null;
        try {
        	String str = Long.toHexString(Long.parseLong(localCursor.getString(1)));
        	return str;
       	}
        finally{
        	localCursor.close();
        }
    }

	public static void setAndroidId(String androidId)
	{
		String cmd = SQLITE_PATH+" /data/data/com.android.providers.settings/databases/settings.db "+
			"\"update secure set value='"+androidId+"' where name='android_id';\"";
		RootUtil.run(cmd);
	}
	
	public static void setGoogleServiceId(String serviceId)
	{
		String cmd = SQLITE_PATH+" /data/data/com.android.google.gsf/databases/gservices.db "+
			"\"update main set value='"+serviceId+"' where name='android_id';\"";
		RootUtil.run(cmd);
	}
	
	public static void setSerialNo(String serialNo)
	{
		String cmd = "setprop ro.serialno "+serialNo;
		RootUtil.run(cmd);
	}
	
	public static void setMacAddress(String mac){
		String cmd="busybox ifconfig wlan0 down\nbusybox iplink set wlan0 address "+mac+"\nbusybox ifconfig wlan0 up";
		RootUtil.run(cmd);
	}
}
