package com.unidevel.logo.model;

import android.graphics.Color;
import android.graphics.Paint;


public class Pen {
	boolean down;
	Paint paint;

	public Pen(){
		down = true;
		paint = new Paint();
		paint.setStrokeWidth(1.0f);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setStrokeCap(Paint.Cap.BUTT);
		paint.setStrokeJoin(Paint.Join.MITER);
		paint.setAntiAlias(true);
	}
	
	public void setColor(int color) {
		paint.setColor(color);
	}
	
	public void setSize(float size) {
		paint.setStrokeWidth(size);
	}
	
	public boolean isDown() {
		return down;
	}
	
	public void setDown(boolean down) {
		this.down = down;
	}
	
	public Paint getPaint() {
		return paint;
	}
}
