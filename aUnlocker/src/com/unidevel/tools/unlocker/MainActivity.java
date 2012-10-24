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

public class MainActivity extends Activity implements SensorEventListener {
	float x,y,z;
	float pi=3.14159f;
	public void onSensorChanged(SensorEvent e)
	{
		z= e.values[0];
		y= e.values[1];
		x= -90-e.values[2];
		glView.invalidate();
		rd.input(e.values[0],e.values[1],e.values[2]);
		msg("mode:"+mode+",x:"+x+", y:"+y+", z:"+z+",e0:"+e.values[0]+",e1:"+e.values[1]+",e2:"+e.values[2]+",rd:"+rd);//+"\nState:"+rd);
	}

	public void onAccuracyChanged(Sensor s, int v)
	{
		
	}
	
	public void onConfigurationChanged(Configuration config){
		this.orientation = config.orientation;
		if(config.orientation==Configuration.ORIENTATION_PORTRAIT){
			mode=0;
		}
		else{
			mode=1;
		}
		Log.i("orientation",""+config.orientation);
	}
	
	int orientation;
	TextView view;
	GLSurfaceView glView;
	SensorManager sm;
	Sensor accl;
	RotationDetector rd;
	MyRenderer renderer;
	int mode=0;
	float unlockX,unlockY,unlockZ;
	
	class MyRenderer implements GLSurfaceView.Renderer
	{
		FloatBuffer shape;
		public MyRenderer(){
			this.shape=createShape();
		}
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
		}

		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			if(height==0)height=1;
			
			gl.glViewport(0, 0, width, height);
			// for a fixed camera, set the projection too
			float ratio = (float) width / height;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity(); 
			GLU.gluPerspective(gl,45.0f,ratio,0.1f, 100.0f);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity(); 		
			
			gl.glFrontFace(GL10.GL_CCW);
			gl.glCullFace(GL10.GL_BACK);
			gl.glEnable(GL10.GL_CULL_FACE);
			
			//gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		}
		
		private FloatBuffer createShape(){
			FloatBuffer buf;
			float v[]=new float[]{
				
				//front
				-0.5f,-1f,0.0f,
				0.5f,-1f,0.0f,
				0.5f,1.0f,0.0f,
				-0.5f,1.0f,0.0f,
				
				//back
				-0.45f,-0.95f,-0.1f,
				0.45f,-0.95f,-0.1f,
				0.45f,0.95f,-0.1f,
				-0.45f,0.95f,-0.1f,
				//ground
				-2.0f,-2.0f,-1.0f,
				-2.0f,2.0f,-1.0f,
				2.0f,2.0f,-1.0f,
				2.0f-2.0f,-1.0f,
			};
			ByteBuffer bbuf=ByteBuffer.allocateDirect(v.length*4);
			bbuf.order(ByteOrder.nativeOrder());
			buf=bbuf.asFloatBuffer();
			buf.put(v);
			buf.position(0);
			return buf;
		}
		
		public void drawPad(GL10 gl,float r,float g,float b,float a)
		{
			shape.position(0);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glColor4f(r,g,b,a);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, shape);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0,4);
			//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			
			//shape.position(12);
			//gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			//gl.glColor4f(0.5f,0.5f,0.9f,1.0f);
			//gl.glVertexPointer(3, GL10.GL_FLOAT, 0, shape);
			//gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 4,4);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
		

		public void drawGround(GL10 gl)
		{
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glColor4f(0.2f,0.2f,0.2f,1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, shape);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 8,4);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}

		

		public void onDrawFrame(GL10 gl)
		{
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); 
			gl.glLoadIdentity();
			
			gl.glTranslatef(0.0f,0.0f,-3.0f);
			
		//	drawGround(gl);
			
			//gl.glRotatef(-x,-y,-z,1.0f);
			//gl.glRotatef(y,0.0f,1.0f,0.0f);
			if(mode==0)
			{
				gl.glPushMatrix();
				//portaite
				gl.glRotatef(x,1.0f,0.0f,0.0f);
				drawPad(gl,0.7f,0.7f,0.7f,1.0f);
				
				gl.glPopMatrix();
			}
			else
			{
				gl.glPushMatrix();

			//	gl.glRotatef(-90.0f,0.0f,0.0f,1.0f);
				gl.glRotatef(y,1.0f,0.0f,0.0f);
				drawPad(gl,0.7f,0.7f,0.7f,1.0f);
				gl.glPopMatrix();
				
			}
		//	drawGround(gl);
		}
		
	}
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
	//	setContentView(view);
		
		LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams param;
		{
			view=new TextView(this);
			
			param=new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(view,param);
		}
		
		{
			glView = new GLSurfaceView(this);
			renderer=new MyRenderer();
			glView.setRenderer(renderer);
			
			param=new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				240);
			//	LinearLayout.LayoutParams.MATCH_PARENT,
			//	LinearLayout.LayoutParams.MATCH_PARENT);
			layout.addView(glView,param);
		}
		
		
		setContentView(layout);
		Intent it=new Intent(this,UnlockService.class);
		startService(it);
		rd=new RotationDetector();
		
	}
	public void onResume(){
		super.onResume();
		glView.onResume();
		sm=(SensorManager) getSystemService(SENSOR_SERVICE);
		accl=sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sm.registerListener(this,accl,SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	public void onPause(){
		super.onPause();
		glView.onPause();
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
