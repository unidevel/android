package com.unidevel.andmaze;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MazeMap extends View {
	static final String TAG = "MazeMap";
	RefreshHandler refreshHandler;
	Maze maze;
	public enum STATE { READY, PAUSE } ;
	STATE state;
	long refreshDelay;
	int rows;
	int columns;

	int xSpan;
	int ySpan;
	int xOffset;
	int yOffset;
	
	private Bitmap[] bitmaps; 
	Paint paint = new Paint();
	
	public MazeMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MazeMap(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MazeMap(Context context) {
		super(context);
	}
	
	public void initNewGame(int rows, int cols) {
		//mTileList.clear();
		Log.d(TAG, "game init");
		maze = new Maze(rows, cols);
		maze.generateMaze();
		maze.dump();
		this.rows = rows;
		this.columns = cols;
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calculateTileSize(w, h);
		loadBitmaps();
	}
	
    protected void calculateTileSize(int w, int h) {
    	Log.d(TAG, "OnSize changed, w = " + Integer.toString(w)+"h = " + Integer.toString(h));
    	xSpan = (int)Math.floor((w)/columns);
    	ySpan = (int)Math.floor((h)/rows);
        xOffset = 0;
        yOffset = 0;
    }

    protected void loadBitmaps(){
    	bitmaps = new Bitmap[4];
    	Resources r = this.getContext().getResources();
    	loadBitmap(1, r.getDrawable(R.drawable.wall), xSpan/3, ySpan/3);
    	
    }
    
    public void loadBitmap(int key, Drawable tile, int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, w, h);
        tile.draw(canvas);
        bitmaps[key] = bitmap;
    }
    
	private class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if(state == STATE.READY) {
				if(msg.what == 1) {//TODO change to final Name
					refreshHandler.sleep(refreshDelay);
				}
				else {
					MazeMap.this.invalidate();
				}
			}
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendEmptyMessageDelayed(0, delayMillis);
		}
	};
	
	public void setState(STATE state) {
		this.state = state;
	}
	
	private void drawMazeLine(Canvas canvas, Bitmap bitmap, int startX, int startY, int endX, int endY){
		float xx, yy;
		xx = startX * xSpan + xSpan/3;
		yy = startY * ySpan + ySpan/3;
		canvas.drawBitmap(bitmap, x, y, paint);
		for ( int y = startY; y < endY; ++ y ) {

		}
	}
	
	private void drawMaze(Canvas canvas, Maze maze, int pathId){
		Bitmap bitmap = bitmaps[pathId];
		int[][] data = maze.getData();
		for ( int y = 0; y < maze.getRows(); ++ y ){
			for( int x = 0; x < maze.getColumns(); ++ x ) {
				if ( data[y][x] == pathId ) {
					int startX, endX;
					startX = x;
					for ( ; x < maze.getColumns() && data[y][x] == pathId; ++x );
					endX = x;
					drawMazeLine(canvas, bitmap, startX, y, endX, y);
				}
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
