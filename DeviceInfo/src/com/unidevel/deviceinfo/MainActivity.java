package com.unidevel.deviceinfo;

import java.lang.reflect.Method;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.*;

public class MainActivity extends Activity {
	
	private static final Uri gsfUri = Uri.parse("content://com.google.android.gsf.gservices");
	static final int ANDROID_ID = 0;
	static final int ANDROID_ID_GSF = 1;
	static final int DEVICE_ID = 2;
	static final int SERIAL_NO = 3;
	static final int MAC_ADDRESS = 4;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		refreshInfo();
    }
	
	public void refreshInfo(){
		showDeviceInfo(ANDROID_ID,R.id.android_id);
		showDeviceInfo(ANDROID_ID_GSF,R.id.android_id_gsf);
		showDeviceInfo(DEVICE_ID,R.id.device_id);
		showDeviceInfo(SERIAL_NO,R.id.serial_no);
		showDeviceInfo(MAC_ADDRESS,R.id.mac_address);
        
        //((TextView)findViewById(R.id.device_info)).setText(deviceInfo);
    }
	
	private void showDeviceInfo(int type, int id){
		String info;
		try{
			info=getDeviceInfo(type);
		}
		catch(Throwable ex){
			info=ex.getMessage();
		}
		((TextView)findViewById(id)).setText(info);
	}
	
	public String getDeviceInfo(int type)throws Exception{
		switch(type){
			case ANDROID_ID:
				return getAndroidId();
			case ANDROID_ID_GSF:
				return getGSFAndroidId(this);
			case DEVICE_ID:
				return getDeviceId();
			case SERIAL_NO:
				return getSerialNo();
			case MAC_ADDRESS:
				return getMacAddress();
		}
		return null;
	}
	
    private String getGSFAndroidId(Context context) throws Exception
    {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "android_id";
        Cursor localCursor = context.getContentResolver().query(gsfUri, null, null, arrayOfString, null);
        if (localCursor==null || !localCursor.moveToFirst() || localCursor.getColumnCount() < 2) return null;
        try {
        	String str = Long.toHexString(Long.parseLong(localCursor.getString(1)));
        	return str;
       	}
        finally{
        	localCursor.close();
        }
    }
    
    private String[] getAccounts() throws Exception{
    	Account[] arrayOfAccount = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
    	int n = arrayOfAccount.length;
    	String[] accounts = new String[n];
    	for (int i = 0; i< n ; i++){
    		accounts[i] = arrayOfAccount[n].name;
    	}
    	return accounts;
    }
    
    private String getAndroidId() throws Exception{
        String androidId;
       	androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
       	return androidId;
    }
    
    private String getDeviceId() throws Exception{
    	return ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }
    
    private String getSerialNo() throws Exception {
    	String serialNo;
    	Class localClass1 = Class.forName("android.os.SystemProperties");
        Method localMethod1 = localClass1.getMethods()[2];
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = "ro.serialno";
        arrayOfObject1[1] = "Unknown";
        serialNo = (String)localMethod1.invoke(localClass1, arrayOfObject1);
        return serialNo; 
    }
    
    private String getMacAddress() throws Exception{
    	return ((WifiManager)this.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }
}
