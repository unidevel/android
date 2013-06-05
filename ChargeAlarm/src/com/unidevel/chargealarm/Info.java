package com.unidevel.chargealarm;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.*;
import android.provider.*;

public class Info extends Activity {
	TextView view;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		view=(TextView)findViewById(R.id.msg);
		getBatteryStatus();
		view.setOnClickListener(new View.OnClickListener(){

				public void onClick(View p1)
				{
					playNotify(Info.this);
				}
				
		});
		Intent i=new Intent(this, ChargeStatService.class);
		startService(i);
	}
	
	public void playNotify(Context context){
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {
			Log.e("play", e.getMessage(),e);
		}
	}
	
	
	public void getBatteryStatus(){
		Intent batteryStatus=this.registerReceiver(null,
							  new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float)scale;
		view.setText("%"+batteryPct);
	}
}
