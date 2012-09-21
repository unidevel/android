package com.unidevel.tools.locker;

import android.app.*;
import android.os.*;
import android.widget.*;
import com.unidevel.tools.locker.*;
import android.app.admin.*;

public class ActionActivity extends Activity
{
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		int actId=getIntent().getIntExtra("action",0);
		switch(actId){
			case 1:
				lock();
				break;
			case 2:
				reboot();
				break;
			case 3:
				shutdown();
				break;
			case 9:
				cancel(1);
				break;
		}
		finish();
	//	System.exit(0);
	}

	private void shutdown()
	{
		// TODO: Implement this method
	}

	private void reboot()
	{
		// TODO: Implement this method
	}
	
	public void msg(String s){
		Toast.makeText(this,s,3).show();
	}
	
	public void cancel(int id){
		NotificationManager nm= (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(id);
	}
	
	public void lock(){
		DevicePolicyManager dpm = null;
		try{
			dpm=(DevicePolicyManager) this.getSystemService(DEVICE_POLICY_SERVICE);
		}
		catch(Exception ex){}
		if(dpm!=null){
			dpm.lockNow();
			return;
		}
	}
}
