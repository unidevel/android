package com.unidevel.logo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.unidevel.logo.engine.LogoVM;
import com.unidevel.logo.model.ALogoContext;
import com.unidevel.logo.model.Pen;
import com.unidevel.logo.model.Turtle;
import com.unidevel.logo.model.ALogoContext.OnUpdateListener;

public class ConsoleView extends View {
	Turtle turtle;
	Pen pen;
	Bitmap bitmap;
	LogoVM logoVM;
	
	public ConsoleView(Context context) {
		super(context);
		turtle = new Turtle();
		pen = new Pen();
		{
			bitmap = Bitmap.createBitmap(480, 480, Config.ARGB_4444);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight(), paint);
		}
	}
	
	public ConsoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void runScript(String script){
		ALogoContext ctx = new ALogoContext(turtle, pen, bitmap, new OnUpdateListener(){
			@Override
			public void update() {
				ConsoleView.this.postInvalidate();
			}
		});
		logoVM = new LogoVM();
		try {
			logoVM.eval(ctx, script);
		} catch (Throwable e) {
			Toast.makeText(getContext(), e.getMessage(), 3);
		}
		ConsoleView.this.postInvalidate();
	}
	
	protected void drawTurtle(Canvas canvas){
		float hx, hy, dx, dy;
		hx = turtle.getX()-(float)(20.0f * Turtle.sin(turtle.getAngle()));
		hy = turtle.getY()-(float)(20.0f * Turtle.cos(turtle.getAngle()));
		dx = -(float) (8.0f * Turtle.cos(turtle.getAngle()));
		dy = (float) (8.0f * Turtle.sin(turtle.getAngle()));
		
		canvas.drawLine(hx, hy, turtle.getX()+dx, turtle.getY()+dy, turtle.getPaint());
		canvas.drawLine(hx, hy, turtle.getX()-dx, turtle.getY()-dy, turtle.getPaint());
		canvas.drawLine(turtle.getX()-dx, turtle.getY()-dy, turtle.getX()+dx, turtle.getY()+dy, turtle.getPaint());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.translate(getWidth()/2, getHeight()/2);
		float x = -bitmap.getWidth()/2;
		float y = -bitmap.getHeight()/2;
		canvas.drawBitmap(bitmap, x, y, pen.getPaint());
		drawTurtle(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
