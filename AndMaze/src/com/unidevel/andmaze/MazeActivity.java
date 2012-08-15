package com.unidevel.andmaze;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MazeActivity extends Activity {
    
	private MazeMap mMainMapView;

	public static final String TAG = "Maze";
    
	private static String ICICLE_KEY = "maze-view";
    
	/** Called when the activity is first created. */    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, "Create main layout");

        int rows = getIntent().getIntExtra("rows", 20);
        int cols = getIntent().getIntExtra("cols", 40);
        mMainMapView = (MazeMap) findViewById(R.id.gameMap);
        mMainMapView.initNewGame(rows, cols);
        
        //TextView myText = (TextView) findViewById(R.id.txt);
        
        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
        	mMainMapView.setState(MazeMap.STATE.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
            	//mMainMapView.restoreState(map);
            } else {
            	mMainMapView.setState(MazeMap.STATE.PAUSE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mMainMapView.setState(MazeMap.STATE.PAUSE);
    }
    
    @Override
    protected void onStop() {
        super.onPause();
        // Pause the game along with the activity
        mMainMapView.setState(MazeMap.STATE.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        //outState.putBundle(ICICLE_KEY, mMainMapView.saveState());
    }
}
