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
		public void input(float x,float y, float z){
			if(state==0){
				stamp=System.currentTimeMillis();
			}
			if(y>5||y<-5){
				stamp=System.currentTimeMillis();
				state=0;
				return;
			}
			long now=System.currentTimeMillis();
			if(z>-5||z<5){
				if(state==0){
					stamp=now;
					state=1;
				}
				else if(state==2){
					if(now-stamp<1000)
						state=3;
					else state=0;
					stamp=now;
					
				}
				return;
			}
			if(z>50&&z<85){
				if(state==1)now-stamp<1000){
					state=2;
					stamp=now;
				}
				else if(state==3){
					state=4;
					stamp=now;
				}
				return;
			}
		}
		
		public boolean isMatch(){
			return state==4;
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
		accl=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
