package com.unidevel.andmaze;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MazeMap extends View {
	static final String TAG = "MazeMap";
	RefreshHandler refreshHandler;
	Activity activity;
	Maze maze;

	private int yInitRaw;

	private int xInitRaw;

	private static int moveSens=15;
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
	
	public void initNewGame(Activity activity, int rows, int cols) {
		//mTileList.clear();
		Log.d(TAG, "game init");
		maze = new Maze(rows, cols);
		maze.generateMaze();
		maze.dump();
		this.activity = activity;
		this.rows = maze.getRows();
		this.columns = maze.getColumns();
		this.refreshHandler = new RefreshHandler();
		this.refreshHandler.sendEmptyMessage(0);
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
		moveSens =(int)(xSpan>ySpan?ySpan/2.5f:xSpan/2.5f);
    }

    protected void loadBitmaps(){
    	bitmaps = new Bitmap[5];
    	Resources r = this.getContext().getResources();
    	loadBitmap(Maze.emptyCode, r.getDrawable(R.drawable.empty), xSpan, ySpan);
    	loadBitmap(Maze.wallCode, r.getDrawable(R.drawable.wall), xSpan/3+1, ySpan/3+1);
		loadBitmap(Maze.manCode, r.getDrawable(R.drawable.man), xSpan, ySpan);
		loadBitmap(Maze.doorCode, r.getDrawable(R.drawable.door), xSpan, ySpan);
    	loadBitmap(Maze.pathCode, r.getDrawable(R.drawable.path), xSpan/3+1, ySpan/3+1);
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
	
	private void drawMazeXLine(Canvas canvas, Bitmap bitmap, int startX, int endX, int y){
		float xx, yy;
		float dx = (float)xSpan/3.0f;
		float dy = (float)ySpan/3.0f;
		xx = startX * xSpan + dx;
		yy = y * ySpan + dy;
		canvas.drawBitmap(bitmap, xx, yy, paint);
		for ( int x = startX+1; x < endX; ++ x ) {
			xx+=dx;
			canvas.drawBitmap(bitmap,xx,yy,paint);
			xx+=dx;
			canvas.drawBitmap(bitmap,xx,yy,paint);
			xx+=dx;
			canvas.drawBitmap(bitmap,xx,yy,paint);
		}
	}
	
	private void drawMazeYLine(Canvas canvas, Bitmap bitmap, int startY, int endY, int x){
		float xx, yy;
		float dx = (float)xSpan/3.0f;
		float dy = (float)ySpan/3.0f;
		xx = x * xSpan + dx;
		yy = startY * ySpan + dy;
		canvas.drawBitmap(bitmap, xx, yy, paint);
		for ( int y = startY+1; y < endY; ++y ) {
			yy+=dy;
			canvas.drawBitmap(bitmap,xx,yy,paint);
			yy+=dy;
			canvas.drawBitmap(bitmap,xx,yy,paint);
			yy+=dy;
			canvas.drawBitmap(bitmap,xx,yy,paint);
		}
	}
	
	private void drawMaze(Canvas canvas, Maze maze, int pathId){
		Bitmap bitmap = bitmaps[pathId];
		int[][] data = maze.getData();
		for ( int y = 0; y < this.rows; ++ y ){
			for( int x = 0; x < this.columns; ++ x ) {
				if ( data[y][x] == pathId ) {
					int startX, endX;
					startX = x;
					for ( ; x < this.columns && data[y][x] == pathId; ++x );
					endX = x;
					drawMazeXLine(canvas, bitmap, startX, endX, y);
				}
			}
		}
		for( int x = 0; x < this.columns; ++ x ) {
			for ( int y = 0; y < this.rows; ++ y ){
				if ( data[y][x] == pathId ) {
					int startY, endY;
					startY = y;
					for ( ; y < this.rows && data[y][x] == pathId; ++y );
					endY = y;
					drawMazeYLine(canvas, bitmap, startY, endY, x);
				}
			}
		}	
	}
	
	private void drawBitmap(Canvas canvas, Maze maze, int id){
		Bitmap bitmap = bitmaps[id];
		int[][] data = maze.getData();
		for(int y=0; y<rows;++y){
			for(int x=0; x<columns; ++x){
				if (data[y][x]==id){
					canvas.drawBitmap(bitmap,x*xSpan,y*ySpan,paint);
				}
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect(0,0,getWidth(),getHeight());
		canvas.drawRect(rect, paint);
		drawMaze(canvas,maze,Maze.wallCode);
		drawMaze(canvas,maze,Maze.pathCode);
		drawBitmap(canvas,maze,Maze.manCode);
		drawBitmap(canvas,maze,Maze.doorCode);
	}
	
	//	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		//This prevents touchscreen events from flooding the main thread
		synchronized (event)
		{
			try
			{
				//Waits 16ms.
				event.wait(16);
				int xCurRaw = 0, yCurRaw = 0;

				//when user touches the screen
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					xInitRaw = (int) Math.floor(event.getRawX());
					yInitRaw = (int) Math.floor(event.getRawY());
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE ) {
					xCurRaw = (int) Math.floor(event.getRawX());
					yCurRaw = (int)Math.floor(event.getRawY());
					int dx= 0;
					int dy= 0;
					boolean needUpdate = false;
					boolean tryAgain = true;
					if (Math.abs(xInitRaw - xCurRaw) > moveSens ) {
						dx = xCurRaw-xInitRaw>0?1:-1;
						needUpdate = maze.move(dx,0);
						if ( needUpdate ) tryAgain = false;
					}
					if( Math.abs(yInitRaw - yCurRaw) > moveSens ) {
						dy = yCurRaw - yInitRaw>0?1:-1;
						needUpdate |= maze.move(0,dy);
					}
					if ( needUpdate && tryAgain ) {
						if (Math.abs(xInitRaw - xCurRaw) > moveSens ) {
							dx = xCurRaw-xInitRaw>0?1:-1;
							needUpdate |= maze.move(dx,0);
						}
					}
					if ( needUpdate ) {
						xInitRaw = xCurRaw;
						yInitRaw = yCurRaw;
						update();
					}
				}
				Log.i(TAG, "Raw("+xInitRaw+","+yInitRaw+"), Cur("+xCurRaw+","+yCurRaw+")");
//						int q = (xCurRaw - xInitRaw)/xMoveSens;
//						if(q > 1)
//							Log.d(TAG, "move left q = " + Integer.toString(q));
//						wasMoved = true;
//						xInitRaw = xCurRaw;
//						mapCur.resetMap();
//						mapCur.copyFrom(mapOld);
//						for (int i = 0; i < q; i++) {
//							if(curTetrino.moveRight(mapCur) &&
//									!curTetrino.isColusionY(curTetrino.getYPos()+1, curTetrino.getXPos(), curTetrino.sMap, mapCur, false)) {
//								if (mRedrawHandler.hasMessages(1) == true) {//TODO change to final Name
//									mRedrawHandler.removeMessages(1);
//									mRedrawHandler.sendEmptyMessageDelayed(0, 400);//TODO convert to parameter and change to final Name
//								}
//							}
//							mapCur.putTetrinoOnMap(curTetrino);
//						}
//						update();
//						
//					}
//					if ((yCurRaw - yInitRaw) > xMoveSens) {
//						long timeDelta = Math.abs(initTime - SystemClock.uptimeMillis());
//						if(timeDelta > deltaTh) {
//							yInitDrop = yCurRaw;
//							initTime = SystemClock.uptimeMillis();
//						}
//						wasMoved = true;
//						yInitRaw = yCurRaw;
//						//yInitDrop = yInitRaw;
//						mapCur.resetMap();
//						mapCur.copyFrom(mapOld);
//						curTetrino.moveDown(mapCur);
//						mapCur.putTetrinoOnMap(curTetrino);
//						update();
//						
//					}
//				}
//					
//				//when screen is released
//				if(event.getAction() == MotionEvent.ACTION_UP)
//				{
//					long timeDelta = Math.abs(initTime - SystemClock.uptimeMillis()); 
//					if(mGameState == READY && !pausePressed){
//						int yCurRaw = (int) Math.floor(event.getRawY());
//						if(yCurRaw - yInitDrop > dropSensativity && timeDelta < deltaTh) {
//							mapCur.resetMap();
//							mapCur.copyFrom(mapOld);
//							curTetrino.drop(mapCur);
//							mapCur.putTetrinoOnMap(curTetrino);
//							update();
//							mRedrawHandler.removeMessages(0);
//							mRedrawHandler.sendEmptyMessage(1);//TODO change to final name
//						}
//						//Rotate tetrino (release on same x pos) 
//						else if (!wasMoved && Math.abs(yCurRaw - yInitRaw) < rotateSens ) {
//							mapCur.resetMap();
//							mapCur.copyFrom(mapOld);
//							curTetrino.rotateTetrino(mapCur);
//							mapCur.putTetrinoOnMap(curTetrino);
//							update();
//						}
//					}
//					else
//						pausePressed = false;
//				}
//				
			}
			catch (InterruptedException e)
			{
				return true;
			}
		}
		return true;
	}

	private void update()
	{
		this.refreshHandler.sendEmptyMessage(0);
		if ( maze.isOut() ) {
			this.refreshHandler.post(new Runnable(){
				@Override
				public void run() {
					Builder builder = new Builder(MazeMap.this.getContext());
					builder.setMessage("Congratulations! Do you want to play again?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.finish();
						}
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);
						}
					});
					builder.create().show();
				}
			});
		}
	}
}
