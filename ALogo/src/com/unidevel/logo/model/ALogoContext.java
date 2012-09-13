package com.unidevel.logo.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.graphic.LogoBack;
import com.unidevel.logo.engine.cmd.graphic.LogoForward;
import com.unidevel.logo.engine.cmd.graphic.LogoLeft;
import com.unidevel.logo.engine.cmd.graphic.LogoPenDown;
import com.unidevel.logo.engine.cmd.graphic.LogoPenUp;
import com.unidevel.logo.engine.cmd.graphic.LogoRight;
import com.unidevel.logo.engine.cmd.graphic.LogoSetPenColor;
import com.unidevel.logo.engine.cmd.graphic.LogoSetPenSize;

public class ALogoContext extends LogoContext {
	public interface OnUpdateListener {
		void update();
	}
	
	Turtle turtle;
	Pen pen;
	Bitmap bitmap;
	OnUpdateListener updateListener;
	
	public ALogoContext(Turtle turtle, Pen pen, Bitmap bitmap, OnUpdateListener updateListener){
		super();
		this.turtle = turtle;
		this.pen = pen;
		this.bitmap = bitmap;
		this.updateListener = updateListener;
	}
	
	private Canvas getCanvas(){
		Canvas canvas = new Canvas(bitmap);
		canvas.translate(bitmap.getWidth()/2, bitmap.getHeight()/2);
		return canvas;
	}
	
	@Override
	public void run(String cmd, Object... args) {
		if ( cmd == LogoForward.NAME ) {
			float len = toFloat(args[0]);
			float x = turtle.getX();
			float y = turtle.getY();
			turtle.move(len);
			if ( pen.isDown() )  {
				Canvas canvas = getCanvas();
				canvas.drawLine(x, y, turtle.getX(), turtle.getY(), pen.getPaint());
				updateListener.update();
			}
		}
		else if ( cmd == LogoBack.NAME ) {
			float len = -toFloat(args[0]);
			float x = turtle.getX();
			float y = turtle.getY();
			turtle.move(len);
			if ( pen.isDown() )  {
				Canvas canvas = getCanvas();
				canvas.drawLine(x, y, turtle.getX(), turtle.getY(), pen.getPaint());
				updateListener.update();
			}
		}
		else if ( cmd == LogoRight.NAME ) {
			float angle = 0.0f-toFloat(args[0]);
			turtle.rotate(angle);
		}
		else if ( cmd == LogoLeft.NAME ) {
			float angle = toFloat(args[0]);
			turtle.rotate(angle);
		}
		else if ( cmd == LogoPenUp.NAME ) {
			pen.setDown(false);
		}
		else if ( cmd == LogoPenDown.NAME ) {
			pen.setDown(true);
		}
		else if ( cmd == LogoSetPenSize.NAME ) {
			float width = toFloat(args[0]);
			pen.setSize(width);
		}
		else if ( cmd == LogoSetPenColor.NAME ) {
			int color = toInt(args[0]);
			pen.setColor(color);
		}
	}
	
	private int toInt(Object object) {
		if ( object instanceof Integer ) {
			return (Integer)object;
		}
		return 0;
	}

	protected float toFloat(Object arg){
		if ( arg instanceof Integer ) {
			return (Integer)arg;
		}
		else if ( arg instanceof Float ) {
			return (Float)arg;
		}
		throw new RuntimeException("Not number");
	}
}
