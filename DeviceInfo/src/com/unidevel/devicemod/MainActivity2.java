package com.unidevel.devicemod;

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
import android.preference.*;
import java.util.*;
import java.io.*;
import android.view.*;
import android.os.*;

public class MainActivity2 extends PreferenceActivity implements View.OnClickListener
{

	public void onClick(View view)
	{
		if(R.id.save_button==view.getId()){
			this.saveDeviceInfo();
		}
		else{
			String id=savedDeviceInfo.getProperty("android_id");
			if(id!=null)
				this.updateAndroidId(id);
		}
	}

	
	private static final Uri gsfUri = Uri.parse("content://com.google.android.gsf.gservices");
	static final int ANDROID_ID = 0;
	static final int ANDROID_ID_GSF = 1;
	static final int DEVICE_ID = 2;
	static final int SERIAL_NO = 3;
	static final int MAC_ADDRESS = 4;
	static final String[] KEYS=new String[]{"android_id","amdroid_id_gsf","device_id","serial_no","mac_address"};
	
	Properties savedDeviceInfo;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
		Button button=(Button) this.findViewById(R.id.save_button);
		button.setOnClickListener(this);
		button = (Button) this.findViewById(R.id.restore_button);
		button.setOnClickListener(this);
		//refreshInfo();
		PreferenceScreen screen = this.getPreferenceManager().createPreferenceScreen(this);
		createPreference("android_id","android_id",screen);	
		createPreference("android_id_gsf","android_id(GSF)",screen);	
		createPreference("device_id","device_id",screen);	
		createPreference("serial_no","serial_no",screen);	
		createPreference("mac_address","mac_address",screen);	
		this.setPreferenceScreen(screen);
		loadDeviceInfo();
		refreshInfo();
    } 
	
	private void createPreference(String key, String summary, PreferenceScreen screen){
		EditTextPreference pref = new EditTextPreference(this);
		pref.setKey(key);
		pref.setTitle(summary);
		screen.addPreference(pref);
	}
	
	public void refreshInfo(){
		showDeviceInfo(ANDROID_ID,"android_id");
		showDeviceInfo(ANDROID_ID_GSF,"android_id_gsf");
		showDeviceInfo(DEVICE_ID,"device_id");
		showDeviceInfo(SERIAL_NO,"serial_no");
		showDeviceInfo(MAC_ADDRESS,"mac_address");
        
        //((TextView)findViewById(R.id.device_info)).setText(deviceInfo);
    }
	
	private void showDeviceInfo(int type, String key){
		String info;
		try{
			info=getDeviceInfo(type);
		}
		catch(Throwable ex){
			info=ex.getMessage();
		}
		//((TextView)findViewById(id)).setText(info);
		EditTextPreference pref = (EditTextPreference) this.getPreferenceScreen().getPreference(type);
		pref.setSummary(info);
		pref.setText(info);
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
	
	
	private File getSaveFile(){
		File f = new File(this.getExternalFilesDir(null),"devinfo.txt");
		return f;
	}
	
	private void saveDeviceInfo(){
		Properties prop=new Properties();
		for(int i=0;i<5;++i){
			try{
				String value=getDeviceInfo(i);
				prop.put(KEYS[i],value);
			}
			catch(Throwable ex){}
		}
		File file=getSaveFile();
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			prop.save(out,"");
			out.close();
			Toast.makeText(this,"Saved to "+file.getPath(),Toast.LENGTH_LONG).show();
		}
		catch (Exception e)
		{
			Toast.makeText(this,"save error "+this.getExternalFilesDir(null)+ " "+e.getMessage(),Toast.LENGTH_LONG).show();
		}
	}
	
	private void loadDeviceInfo(){
		Properties prop = new Properties();
		try
		{
			FileInputStream in = new FileInputStream(getSaveFile());
			prop.load(in);
			in.close();
		}
		catch (Exception e)
		{}
		savedDeviceInfo = prop;
	}
	
	private void updateAndroidId(String androidId){
		try
		{
			//3cc1413ba88af5e0
			String cmd = "/system/xbin/sqlite3 /data/data/com.android.providers.settings/databases/settings.db "+
				"\"update secure set value='"+androidId+"' where name='android_id';\"\nexit\n";
			//String cmd = "/system/xbin/sqlite3 /data/data/com.android.providers.settings/databases/settings.db \"select * from secure where name='android_id';\">/sdcard/a.txt\n";
			//String cmd="echo \"hello\">/sdcard/b.txt\n";
			java.lang.Process proc=Runtime.getRuntime().exec("su");
			proc.getOutputStream().write(cmd.getBytes());
			proc.getOutputStream().flush();
			//Thread.sleep(300);
			//proc.getOutputStream().write(cmd2.getBytes());
			//proc.getOutputStream().flush();
			//byte[] buf=new byte[1024];
			//proc.getInputStream().read(buf);
			//Toast.makeText(this,new String(buf),3).show();
		}
		catch (Exception e)
		{
			Toast.makeText(this,e.getMessage(),3).show();
		}
	}
}
