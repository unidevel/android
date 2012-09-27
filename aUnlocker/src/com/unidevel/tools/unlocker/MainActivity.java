package com.unidevel.tools.unlocker;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.content.*;

public class MainActivity extends Activity implements SensorEventListener {
	public void onSensorChanged(SensorEvent e)
	{
		rd.input(e.values[0],e.values[1],e.values[2]);
		msg("x:"+e.values[0]+"\ny:"+e.values[1]+"\nz:"+e.values[2]+"\nState:"+rd);
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
		// TODO: Implement this method
		//msg("v:"+v);
	}
	
	TextView view;
	SensorManager sm;
	Sensor accl;
	RotationDetector rd;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		view=new TextView(this);
		setContentView(view);
		Intent it=new Intent(this,UnlockService.class);
		startService(it);
		rd=new RotationDetector();
	}
	public void onResume(){
		super.onResume();
		sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		accl=sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sm.registerListener(this,accl,SensorManager.SENSOR_DELAY_NORMAL);
	}
	public void onPause(){
		super.onPause();
		sm.unregisterListener(this);
	}
	
	public void printf(String fmt, Object... args){
		String s=String.format(fmt,args);
		view.append(s);
	}
	
	public void msg(String s){
		view.setText(s);
	}
}
