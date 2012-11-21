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
import android.preference.*;

public class SettingActivity  extends Activity implements SensorEventListener 
{
	int mode;
	float x,y,z;
	TextView text;
	SensorManager sensorManager;
	GLSurfaceView glLockView,glUnlockView;
	PadRenderer lockRenderer,unlockRenderer;
	HoldDetector lockDetector,unlockDetector;
	CheckBox swapXY;
	SharedPreferences pref;
	public void onSensorChanged(SensorEvent e)
	{
		z= e.values[0];
		x= e.values[1];
		y= e.values[2];
		//y= -90-e.values[1];
		//x= -90-e.values[2];
		msg("mode:"+mode+",x:"+x+",y:"+y+",z:"+z);
		lockDetector.input(x,y,z);
		boolean open=lockDetector.inRange();
		lockRenderer.update(mode,x,y,open);
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
	//	mode = getWindowManager().getDefaultDisplay().getOrientation();
		
		lockDetector.setMode(mode);
	}	
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		pref= PreferenceManager.getDefaultSharedPreferences(this);
		lockDetector = new HoldDetector();
		lockDetector.setCondition(0,60,10);
		unlockDetector = new HoldDetector();
		text=new TextView(this);
		swapXY=new CheckBox(this);
		swapXY.setText("Swap XY");
		glLockView=new GLSurfaceView(this);
		glUnlockView=new GLSurfaceView(this);
		lockRenderer = new PadRenderer(lockDetector);
		glLockView.setRenderer(lockRenderer);
		unlockRenderer = new PadRenderer(unlockDetector);
		glUnlockView.setRenderer(unlockRenderer);
		ViewUtil util=new ViewUtil(this);
		ViewUtil.LinearView root= util.addLinearLayout(true)
			.addChild(text);
		ViewUtil.LinearView settings=root
			.addLinearLayout(false)
			.addChild(glLockView,320,false)
			.addChild(glUnlockView,320,false);
			
	//	Intent it=new Intent(this,UnlockService.class);
	//	startService(it);
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
	
	public void printf(String fmt, Object... args){
		String s=String.format(fmt,args);
		text.append(s);
	}

	public void msg(String s){
		text.setText(s);
	}
}
