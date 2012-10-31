package com.unidevel.tools.unlocker;

import android.opengl.*;
import java.nio.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;

public class PadRenderer implements GLSurfaceView.Renderer
{
	FloatBuffer shape;
	int mode;
	float x,y;
	boolean open;
	HoldDetector detector;
	public PadRenderer(HoldDetector detector){
		this.shape=createShape();
		this.detector = detector;
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		if(height==0)height=1;

		gl.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity(); 
		GLU.gluPerspective(gl,45.0f,ratio,0.1f, 100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity(); 		

		gl.glFrontFace(GL10.GL_CCW);
		gl.glCullFace(GL10.GL_BACK);
		gl.glEnable(GL10.GL_CULL_FACE);
	}

	private FloatBuffer createShape(){
		FloatBuffer buf;
		float v[]=new float[]{
			-0.5f,-1f,0.0f,
			0.5f,-1f,0.0f,
			0.5f,1.0f,0.0f,
			-0.5f,1.0f,0.0f,
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
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
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
		float r,g,b,a;
		r=0.7f;g=0.7f;b=0.7f;a=1.0f;
		if(detector.isInRange()){
			r=0.0f;g=0.7f;b=0.0f;a=1.0f;
		}
		if(mode==0)
		{
			gl.glPushMatrix();
			//portaite
			gl.glRotatef(x,1.0f,0.0f,0.0f);
			drawPad(gl,r,g,b,a);

			gl.glPopMatrix();
		}
		else
		{
			gl.glPushMatrix();

			gl.glRotatef(-90.0f,0.0f,0.0f,1.0f);
			gl.glRotatef(y,0.0f,1.0f,0.0f);
			drawPad(gl,r,g,b,a);
			gl.glPopMatrix();
		}
	}
	
	public void update(int mode, float x,float y, boolean open){
		this.mode=mode;
		this.x=x;
		this.y=y;
		this.open=open;
	}
}
