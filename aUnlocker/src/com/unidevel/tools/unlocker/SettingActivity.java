package com.unidevel.tools.unlocker;


import android.app.*;
import android.content.*;
import android.content.res.*;
import android.hardware.*;
import android.opengl.*;
import android.os.*;
import android.widget.*;
import java.nio.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import android.util.*;
import com.unidevel.util.*;

public class SettingActivity  extends Activity implements SensorEventListener 
{
	int mode;
	float x,y,z;
	SensorManager sensorManager;
	GLSurfaceView glLockView;
	PadRenderer lockRenderer,unlockRenderer;
	HoldDetector lockDetector,unlockDetector;
	public void onSensorChanged(SensorEvent e)
	{
		z= e.values[0];
		y= -90-e.values[1];
		x= -90-e.values[2];
		lockDetector.input(x,y,z);
		lockRenderer.update(mode,x,y,false);
		glLockView.invalidate();
	}

	public void onAccuracyChanged(Sensor s, int v)
	{

	}

	public void onConfigurationChanged(Configuration config){
		super.onConfigurationChanged(config);
		if(config.orientation==Configuration.ORIENTATION_PORTRAIT){
			mode=0;
		}
		else{
			mode=1;
		}
		lockDetector.setMode(mode);
	}	
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		lockDetector = new HoldDetector();
		unlockDetector = new HoldDetector();
		
		glLockView=new GLSurfaceView(this);
		lockRenderer = new PadRenderer(lockDetector);
		glLockView.setRenderer(lockRenderer);
		ViewUtil util=new ViewUtil(this);
		util.addLinearLayout(true).addChild(glLockView,240,true);
	}
	
	public void onResume(){
		super.onResume();
		glLockView.onResume();
		sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

	}
	
	public void onPause(){
		super.onPause();
		glLockView.onPause();
		sensorManager.unregisterListener(this);
	}
	
}
