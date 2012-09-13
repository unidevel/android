package com.unidevel.logo.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class GLView extends GLSurfaceView {

	public class ResultRender implements GLSurfaceView.Renderer {
		Triangle triangle;
		public ResultRender(){
			triangle = new Triangle();
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
			gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
			//Drawing
			gl.glTranslatef(0.0f, -1.2f, -6.0f);	//Move down 1.2 Unit And Into The Screen 6.0		
			gl.glTranslatef(0.0f, 2.5f, 0.0f);		//Move up 2.5 Units
			triangle.draw(gl);						//Draw the triangle	
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			if(height == 0) { 						//Prevent A Divide By Zero By
				height = 1; 						//Making Height Equal One
			}

			gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
			gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
			gl.glLoadIdentity(); 					//Reset The Projection Matrix

			//Calculate The Aspect Ratio Of The Window
			GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

			gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
			gl.glLoadIdentity(); 					//Reset The Modelview Matrix
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
			gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
			gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
			gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
			
			//Really Nice Perspective Calculations
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		}
	}
	
	public GLView(Context context) {
		super(context);
		this.setRenderer(new ResultRender());
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w, h ;
		w = MeasureSpec.getSize(widthMeasureSpec);
		h = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(w, h);
	}
}
