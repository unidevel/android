package com.unidevel.chargealarm;

import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;

public class PowerConnectionReceiver extends BroadcastReceiver
 {
    @Override
    public void onReceive(Context context, Intent intent) { 
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
			status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		
		Intent i=new Intent("ChargeStat");
		if(Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
			context.startService(i);
		}
		else if(Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())){
			context.stopService(i);
			playNotify(context);			
		}
		else if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
			//if(status==BatteryManager.BATTERY_STATUS_FULL){
				playNotify(context);
			//}
		}
		else{
			context.startService(i);
		}
	}
	
	public void playNotify(Context context){
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {}
	}
}
