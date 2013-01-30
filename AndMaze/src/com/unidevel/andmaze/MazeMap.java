package com.unidevel.andmaze;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MazeMap extends View implements Maze.Listener{
	static final String TAG = "MazeMap";
	RefreshHandler refreshHandler;
	Maze maze;

	private int yInitRaw;

	private int xInitRaw;
	
	Path path;
	
	Maze.Listener listener;

	private static int moveSens=15;
	public enum STATE { READY, PAUSE, DONE } ;
	STATE state;
	long refreshDelay;
	long gameTime;
	int rows;
	int columns;

	int xSpan;
	int ySpan;
	int xOffset;
	int yOffset;
	int panelHeight;
	int level;
	
	Scores scores;
	
	private Bitmap[] bitmaps; 
	Paint paint = new Paint();
	Paint textPaint = new Paint();
	
	public static final int EASY=0;
	public static final int NORMAL=1;
	public static final int HARD=2;
	int[] initialSize=new int[]{15,35,21,45,31,69};
	public MazeMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MazeMap(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MazeMap(Context context) {
		super(context);
	}
	
	public void initNewGame(int level) {
		//mTileList.clear();
		Log.d(TAG, "game init");
		int rows=initialSize[2*level];
		int cols=initialSize[2*level+1];
		maze = new Maze(rows, cols);
		maze.generateMaze();
		maze.setListener(this);
		this.rows = maze.getRows();
		this.columns = maze.getColumns();
		this.refreshDelay = 500;
		this.refreshHandler = new RefreshHandler(this);
		this.refreshHandler.sendEmptyMessage(0);
		this.gameTime = System.currentTimeMillis();
		this.path = new Path();
		this.level=level;
		this.scores = new Scores(this.getContext());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calculateTileSize(w, h);
		loadBitmaps();
	}
	
    protected void calculateTileSize(int w, int h) {
    	Log.d(TAG, "OnSize changed, w = " + Integer.toString(w)+"h = " + Integer.toString(h));
    	if(h<360)panelHeight=20;
		else if(h<420)panelHeight=30;
		else if(h<610)panelHeight=40;
		else panelHeight=50;
		h-=panelHeight;
		xSpan = (int)Math.floor((w)/columns);
    	ySpan = (int)Math.floor((h)/rows);
        xOffset = 0;
        yOffset = panelHeight;
		moveSens =(int)(xSpan>ySpan?ySpan/1.1f:xSpan/1.1f);
		this.textPaint.setColor(Color.WHITE);
		this.textPaint.setTextSize(panelHeight*0.8f);
    }

	int imagePause=5;
	int imagePlay=6;
	int imageExit=7;
    protected void loadBitmaps(){
    	bitmaps = new Bitmap[8];
    	Resources r = this.getContext().getResources();
    	loadBitmap(Maze.emptyCode, r.getDrawable(R.drawable.empty), xSpan, ySpan);
    	loadBitmap(Maze.wallCode, r.getDrawable(R.drawable.wall), xSpan/3+1, ySpan/3+1);
		loadBitmap(Maze.manCode, r.getDrawable(R.drawable.man), xSpan, ySpan);
		loadBitmap(Maze.doorCode, r.getDrawable(R.drawable.door), xSpan, ySpan);
    	loadBitmap(Maze.pathCode, r.getDrawable(R.drawable.path), xSpan/3+1, ySpan/3+1);
		loadBitmap(imagePause, r.getDrawable(android.R.drawable.ic_media_pause),panelHeight,panelHeight);
		loadBitmap(imagePlay, r.getDrawable(android.R.drawable.ic_media_play),panelHeight,panelHeight);
		loadBitmap(imageExit, r.getDrawable(android.R.drawable.ic_lock_power_off),panelHeight,panelHeight);
    }
    
	public void setListener(Maze.Listener listener)	{
		this.listener = listener;
	}
	
    public void loadBitmap(int key, Drawable tile, int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, w, h);
        tile.draw(canvas);
        bitmaps[key] = bitmap;
    }
    
	private static class RefreshHandler extends Handler {
		final MazeMap map;
		public RefreshHandler(MazeMap map)
		{
			this.map = map;
		}
		@Override
		public void handleMessage(Message msg) {
			if(map.state == STATE.READY) {
				map.invalidate();
				removeMessages(0);
				sendEmptyMessageDelayed(0,map.refreshDelay);
			}
			else{
				map.invalidate();
			}
		}
	};
	
	public void setState(STATE state) {
		this.state = state;
		if (state==STATE.READY){
			path.resume();
		}
		else if (state == STATE.PAUSE) {
			path.pause();
		}
	}
	
	public Bundle saveState(){
		Bundle bundle = new Bundle();
		bundle.putIntArray("maze",maze.toArray());
		bundle.putBundle("path",path.store());
		bundle.putInt("level",level);
		setState(STATE.PAUSE);
		return bundle;
	}
	
	public void loadState(Bundle bundle){
		maze.fromArray(bundle.getIntArray("maze"));
		path.load(bundle.getBundle("path"));
		level=bundle.getInt("level");
		//setState(STATE.READY);
	}
	
	private void drawMazeXLine(Canvas canvas, Bitmap bitmap, int startX, int endX, int y){
		float xx, yy;
		float dx = (float)xSpan/3.0f;
		float dy = (float)ySpan/3.0f;
		xx = xOffset + startX * xSpan + dx;
		yy = yOffset + y * ySpan + dy;
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
		xx = xOffset + x * xSpan + dx;
		yy = yOffset + startY * ySpan + dy;
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
					canvas.drawBitmap(bitmap,xOffset+x*xSpan,yOffset+y*ySpan,paint);
				}
			}
		}
	}
	
	Rect drawRect = new Rect();
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawRect.set(0,0,getWidth(),getHeight());
		canvas.drawRect(drawRect, paint);
		drawPanel(canvas);
		drawMaze(canvas,maze,Maze.wallCode);
		drawMaze(canvas,maze,Maze.pathCode);
		drawBitmap(canvas,maze,Maze.manCode);
		drawBitmap(canvas,maze,Maze.doorCode);
		if(state==STATE.PAUSE){
			paint.setAlpha(160);
			canvas.drawRect(0,panelHeight,this.getWidth(),this.getHeight(),paint);
			paint.setAlpha(255);
		}
	}
	
	boolean stopped;
	class PlaybackThread extends Thread{
		public void run(){
			stopped=false;
			maze.reset();
			for(Iterator<Path.Item> it = path.iterator(); it.hasNext()&&!stopped; ){
				Path.Item item = it.next();
				maze.moveTo(item.x, item.y);
				refreshHandler.sendEmptyMessage(0);
				try
				{
					sleep(50);
				}
				catch (InterruptedException e)
				{}
			}
		}
	}
	
	public void playback(){
		maze.reset();
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
					xInitRaw = (int) FloatMath.floor(event.getRawX());
					yInitRaw = (int) FloatMath.floor(event.getRawY());
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE&&state==STATE.READY ) {
					xCurRaw = (int)FloatMath.floor(event.getRawX());
					yCurRaw = (int)FloatMath.floor(event.getRawY());
					int dx= 0;
					int dy= 0;
					boolean needUpdate = false;
					boolean tryAgain = true;
					boolean moved = false;
					if (Math.abs(xInitRaw - xCurRaw) > moveSens ) {
						dx = xCurRaw-xInitRaw>0?1:-1;
						moved = maze.move(dx,0);
						needUpdate |= moved;
						if ( needUpdate ) tryAgain = false;
						if ( moved ) {
							path.addMove(maze.getManX(),maze.getManY());
						}
					}
					if( Math.abs(yInitRaw - yCurRaw) > moveSens ) {
						dy = yCurRaw - yInitRaw>0?1:-1;
						moved = maze.move(0,dy);
						needUpdate |= moved;
						if (moved){
							path.addMove(maze.getManX(),maze.getManY());
						}
					}
					if ( needUpdate && tryAgain ) {
						if (Math.abs(xInitRaw - xCurRaw) > moveSens ) {
							dx = xCurRaw-xInitRaw>0?1:-1;
							moved = maze.move(dx,0);
							if ( moved ){
								path.addMove(maze.getManX(),maze.getManY());
							}
						}
					}
					if ( needUpdate ) {
						xInitRaw = xCurRaw;
						yInitRaw = yCurRaw;
						update();
					}
				}
				else if(event.getAction()==MotionEvent.ACTION_UP){
					xCurRaw = (int)FloatMath.floor(event.getRawX());
					yCurRaw = (int)FloatMath.floor(event.getRawY());
					if(state==STATE.PAUSE )
					{
						setState(STATE.READY);
						update();
						return true;
					}
					else if(state==STATE.DONE){
						stopped=true;
						initNewGame(this.level);
						setState(STATE.READY);
						update();
						return true;
					}
					if(xCurRaw>this.getWidth()-panelHeight-10&&yCurRaw<panelHeight){
						if (state==STATE.READY)
							setState(STATE.PAUSE);
						update();
					}
					
				}
				Log.i(TAG, "Raw("+xInitRaw+","+yInitRaw+"), Cur("+xCurRaw+","+yCurRaw+")");
			}
			catch (InterruptedException e)
			{
				return true;
			}
		}
		return true;
	}
	
	private void drawPanel(Canvas canvas){
		String s="";
		int id=imagePause;
		if(state==STATE.PAUSE){
			s="Pause";
			id=imagePlay;
		}
		else if(state==STATE.DONE){
			s="Done!";
			id=imagePlay;
		}
		float x=0,y=0;
		x=this.getWidth()-panelHeight-10;
		canvas.drawBitmap(bitmaps[id],x,y,paint);
		x=this.getWidth()/2-20;y=panelHeight*0.8f;
		if(s.length()>0){
			canvas.drawText(s,x,y,textPaint);
		}
		long d = path.getMoveTime();
		if(state!=STATE.DONE)
			d+=System.currentTimeMillis()-path.getLastMoveTime();
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm ss");
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		s=fmt.format(new Date(d));
		//s=String.valueOf(d);
		Rect b = new Rect();
		textPaint.getTextBounds(s,0,s.length(),b);
		x=10;
		canvas.drawText(s,x,y,textPaint);
	}

	private void update()
	{
		this.refreshHandler.sendEmptyMessage(0);
	}
	
	public void done(){
		if (state==STATE.DONE){
			notifyDone();
			return;
		}
		state=STATE.DONE;
		this.gameTime=System.currentTimeMillis()-this.gameTime;
		this.refreshHandler.post(new Runnable(){
				@Override
				public void run() {
					Builder builder = new Builder(MazeMap.this.getContext());
					builder.setMessage(R.string.playback);
					builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								PlaybackThread thread = new PlaybackThread();
								thread.start();
							}
						});
					builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								notifyDone();
							}
						});
					builder.create().show();
				}
			});
	}
	
	public void notifyDone(){
		if (this.listener!=null){
			this.listener.done();
		}
	}
}
