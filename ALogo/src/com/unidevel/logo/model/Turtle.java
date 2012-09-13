package com.unidevel.logo.model;

import android.graphics.Color;
import android.graphics.Paint;


public class Turtle {
	float x, y;
	float angle;
	static final float PI = 3.1415927f;
	Paint paint;
	static float[] cosTable;
	static {
		cosTable = new float[181];
		for (int i = 0; i < 90; ++ i ) {
			cosTable[i] = (float)Math.cos((float)i*PI/180.0f);
			cosTable[180-i] = -(float)Math.cos((float)i*PI/180.0f);
		}
		cosTable[0] = 1.0f;
		cosTable[90] = 0.0f;
		cosTable[180] = -1.0f;
	}
	
	public static float sin(float angle){
		int x = ((int)(angle+0.5))%360;
		if ( x <= 90) {
			return cosTable[90-x];
		}
		else if ( x <=270 ) {
			return cosTable[x-90];
		}
		else if ( x >=270 ) {
			return -cosTable[x-270];
		}
		return cosTable[x];
	}
	
	public static float cos(float angle){
		int x = ((int)(angle+0.5)) % 360;
		if ( x > 180 ) x = 360-x;
		return cosTable[x];
	}
	
	public Turtle (){
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeJoin(Paint.Join.MITER);
		paint.setStrokeCap(Paint.Cap.SQUARE);
		paint.setStrokeWidth(2.0f);
	}
	
	public void move(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public void move(float len){
		this.x -= len * sin(angle);
		this.y -= len * cos(angle);
	}
	
	public void rotate(float angle){
		this.angle += angle;
		if ( this.angle > 360.0f ) this.angle -= 360.0f;
		if ( this.angle < 0.0f ) this.angle += 360.0f;
	}
	
	public Paint getPaint() {
		return paint;
	}
}
