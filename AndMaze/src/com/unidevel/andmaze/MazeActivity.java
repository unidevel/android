package com.unidevel.andmaze;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MazeActivity extends Activity {
    
	private MazeMap mMainMapView;

	public static final String TAG = "Maze";
    
	/** Called when the activity is first created. */    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, "Create main layout");

        int level = getIntent().getIntExtra("level", MazeMap.EASY);
        mMainMapView = (MazeMap) findViewById(R.id.gameMap);
        mMainMapView.initNewGame(level);
        
        //TextView myText = (TextView) findViewById(R.id.txt);
        
        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
        	mMainMapView.setState(MazeMap.STATE.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle("maze");
            if (map != null) {
            	mMainMapView.loadState(map);
            } else {
            //	mMainMapView.setState(MazeMap.STATE.PAUSE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
       // mMainMapView.setState(MazeMap.STATE.PAUSE);
    }
    
    @Override
    protected void onStop() {
        super.onPause();
        // Pause the game along with the activity
        //mMainMapView.setState(MazeMap.STATE.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        //outState.putBundle(ICICLE_KEY, mMainMapView.saveState());
    	outState.putBundle("maze",mMainMapView.saveState());
	}
}
