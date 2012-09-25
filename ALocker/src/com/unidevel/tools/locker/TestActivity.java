package com.unidevel.tools.locker;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.hardware.*;

public class TestActivity extends Activity implements SensorEventListener
{
	class RotationDetector{
		int count;
		int state;
		long stamp;

		public RotationDetector()
		{
			this.state=0;
		}
		public void input(float y, float z){
			
		}
		
		public boolean isMatch(){
			
		}
	}
	public void onSensorChanged(SensorEvent e)
	{
		// TODO: Implement this method
		msg("x:"+e.values[0]+"\ny:"+e.values[1]+"\nz:"+e.values[2]);
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
		// TODO: Implement this method
		//msg("v:"+v);
	}
	
	TextView view;
	SensorManager sm;
	Sensor accl;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		view=new TextView(this);
		setContentView(view);
	}
	public void onResume(){
		super.onResume();
		sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		accl=sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sm.registerListener(this,accl,SensorManager.SENSOR_DELAY_NORMAL);
	}
	public void onPause(){
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
