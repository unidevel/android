package com.unidevel.tools.unlocker;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.content.*;
import android.opengl.*;
import javax.microedition.khronos.opengles.*;
import javax.microedition.khronos.egl.*;

public class MainActivity extends Activity implements SensorEventListener {
	public void onSensorChanged(SensorEvent e)
	{
		rd.input(e.values[0],e.values[1],e.values[2]);
		msg("x:"+e.values[0]+"\ny:"+e.values[1]+"\nz:"+e.values[2]+"\nState:"+rd);
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
	}
	
	//TextView view;
	GLSurfaceView view;
	SensorManager sm;
	Sensor accl;
	RotationDetector rd;
	
	class MyRenderer implements GLSurfaceView.Renderer
	{

		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
		}

		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			gl.glViewport(0, 0, width, height);
			// for a fixed camera, set the projection too
			float ratio = (float) width / height;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity(); 
			gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		}

		public void onDrawFrame(GL10 gl)
		{
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); 
		
		}
		
	}
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		view = new GLSurfaceView(this);
		view.setRenderer(new MyRenderer());
		setContentView(view);
		/*
		view=new TextView(this);
		setContentView(view);
		Intent it=new Intent(this,UnlockService.class);
		startService(it);
		rd=new RotationDetector();
		*/
	}
	public void onResume(){
		super.onResume();
		view.onResume();
		/*sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		accl=sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sm.registerListener(this,accl,SensorManager.SENSOR_DELAY_NORMAL);
		*/
	}
	public void onPause(){
		super.onPause();
		view.onPause();
	//	sm.unregisterListener(this);
	}
	
	public void printf(String fmt, Object... args){
		String s=String.format(fmt,args);
	//	view.append(s);
	}
	
	public void msg(String s){
	//	view.setText(s);
	}
}
