package com.unidevel.andmaze;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

public class MainMap extends TileView {
	private RefreshHandler mRedrawHandler = new RefreshHandler();
	/**
	 * This is speed parameter of the game
	 */
	private long mMoveDelay;
	
	private int mGameState = PAUSE;
	/**
	 *  This parameter is the flag that indicate that Action_Down event 
	 *  was occur and tetrino was moved left or right
	 */
	public static final int READY = 1;
	public static final int PAUSE = 0;
	
	Maze maze;
	
	private class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if(mGameState == READY) {
				if(msg.what == 1) {//TODO change to final Name

					mRedrawHandler.sleep(mMoveDelay);
				}
				else {
					clearTiles();
					updateMap();
					MainMap.this.invalidate();
				}
			}
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendEmptyMessageDelayed(0, delayMillis);
			//sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};


	/**
	 * Constructs a MainMap View based on inflation from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public MainMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "MainMap constructor");
		initMainMap();
	}
	
	

	public MainMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "MainMap constructor defStyle");
		initMainMap();
	}

	
	/**
	 * Initialize MainMap Tail icons from drawable 
	 *
	 */
	private void initMainMap() {
		setFocusable(true);
	}
	    

	public void initNewGame(int rows, int cols) {
		//mTileList.clear();
		Log.d(TAG, "game init");
		maze = new Maze(rows, cols);
		maze.generateMaze();
		maze.dump();
		this.setSize(maze.getRows()-2, maze.getColumns()-2);
		
		mRedrawHandler.sendEmptyMessage(1);//TODO change to final name
	}
	
	/**
	 * Save game state so that the user does not lose anything
	 * if the game process is killed while we are in the 
	 * background.
	 * 
	 * @return a Bundle with this view's state
	 */
	public Bundle saveState() {
		Bundle map = new Bundle();
//		map.putIntArray("mapCur", coordArrayListToArray(mapCur));
//		map.putIntArray("mapLast", coordArrayListToArray(mapLast));
//		map.putIntArray("mapOld", coordArrayListToArray(mapOld));
//		map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
		return map;
	}

//	/**
//	 * Given a flattened array of ordinate pairs, we reconstitute them into a
//	 * ArrayList of Coordinate objects
//	 * 
//	 * @param rawArray : [x1,y1,x2,y2,...]
//	 * @return a ArrayList of Coordinates
//	 */
//	private TetrinoMap coordArrayToArrayList(int[] rawArray) {
//		TetrinoMap tMap = new TetrinoMap();//TODO change to get map from argument
//		int arrSize = rawArray.length;
//		for (int i = 0; i < arrSize; i++) {
//			tMap.setMapValue(i%TetrinoMap.MAP_X_SIZE,(i/TetrinoMap.MAP_Y_SIZE),rawArray[i]);
//		}
//		return tMap;
//	}
//
//	/**
//	 * Restore game state if our process is being relaunched
//	 * 
//	 * @param icicle a Bundle containing the game state
//	 */
	public void restoreState(Bundle icicle) {
		setMode(PAUSE);
//		mapCur = coordArrayToArrayList(icicle.getIntArray("mapCur"));
//		mapLast = coordArrayToArrayList(icicle.getIntArray("mapLast"));
//		mapOld = coordArrayToArrayList(icicle.getIntArray("mapOld"));
		mMoveDelay = icicle.getLong("mMoveDelay");
	}
//	    
//	/*
//	 * touch recognition
//	 */
//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		//This prevents touchscreen events from flooding the main thread
//		synchronized (event)
//		{
//			try
//			{
//				//Waits 16ms.
//				event.wait(16);
//
//				//when user touches the screen
//				if(event.getAction() == MotionEvent.ACTION_DOWN)
//				{
//					initTime = SystemClock.uptimeMillis();
//					xInitRaw = (int) Math.floor(event.getRawX());
//					yInitRaw = (int) Math.floor(event.getRawY());
//					yInitDrop = yInitRaw;
//					wasMoved = false;
//					if(xInitRaw > 360 && xInitRaw < 450 && yInitRaw > 560 && yInitRaw < 600) {
//						pausePressed = true;
//						if(mGameState == READY)
//							mGameState = PAUSE;
//						else
//							mGameState = READY;
//					}
//				}
//
//				if(event.getAction() == MotionEvent.ACTION_MOVE && mGameState == READY && !pausePressed) {
//					int xCurRaw = (int) Math.floor(event.getRawX());
//					int yCurRaw = (int)Math.floor(event.getRawY());
//					if ((xInitRaw - xCurRaw) > xMoveSens && Math.abs(yInitRaw - yCurRaw) < dropSensativity) {
//						int q = (xInitRaw - xCurRaw)/xMoveSens;
//						if(q > 1)
//							Log.d(TAG, "move left q = " + Integer.toString(q));
//						wasMoved = true;
//						xInitRaw = xCurRaw;
//						mapCur.resetMap();
//						mapCur.copyFrom(mapOld);
//						for (int i = 0; i < q; i++) { 
//							if (curTetrino.moveLeft(mapCur) && 
//									!curTetrino.isColusionY(curTetrino.getYPos()+1, curTetrino.getXPos(), curTetrino.sMap, mapCur, false)) {
//								if (mRedrawHandler.hasMessages(1) == true) {//TODO change to final Name
//									mRedrawHandler.removeMessages(1);
//									mRedrawHandler.sendEmptyMessageDelayed(0, 400);//TODO convert to parameter and change to final Name
//								}
//							}
//						
//						mapCur.putTetrinoOnMap(curTetrino);
//						}
//						update();
//					}
//					else if((xCurRaw - xInitRaw) > xMoveSens && Math.abs(yInitRaw - yCurRaw) < dropSensativity) {
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
//			}
//			catch (InterruptedException e)
//			{
//				return true;
//			}
//		}
//		return true;
//	}
//	
//	private int getRandomFromArr() {
//		if (randArr[1] == -1)
//			randArr[1] = (int)Math.floor(Math.random()*7);
//		randArr[0] = randArr[1];//shift to next
//		randArr[1] = (int)Math.floor(Math.random()*7);//next
//		mCurNext = randArr[1];
//		return randArr[0];
//	}
//	
//	private void gameMove() {
//		if(curTetrino.moveDown(mapCur)){
//			mapCur.putTetrinoOnMap(curTetrino);
//			mRedrawHandler.sleep(mMoveDelay);
//		}
//		else {//TODO convert to parametr and convert to final name
//			mRedrawHandler.sendEmptyMessageDelayed(1, 1000);
//		}
//	}

	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the snake's location.
	 */
	public void update() {
		if(mGameState == READY) {
			updateMap();
			MainMap.this.invalidate();		
		}
	}
		
	private void updateMap() {
		int[][] data = maze.getData();
		for ( int row = 1; row < maze.getRows()-1; row++ )
		{
			for ( int col = 1; col < maze.getColumns() -1; col++ )
			{
				setTile(data[row][col], col-1, row-1);
			}
		}
		setTile(17, 0, 0);
		setTile(18,maze.getColumns()-3, maze.getRows()-3);
	}

	public void setMode(int state) {
		// TODO Auto-generated method stub
		mGameState = state;
	}


	    
}
