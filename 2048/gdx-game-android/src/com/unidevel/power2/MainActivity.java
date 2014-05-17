package com.unidevel.power2;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import android.content.*;
import android.preference.*;

public class MainActivity extends AndroidApplication implements GameListener
{
	Power2Game game;
	SharedPreferences pref;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		pref=PreferenceManager.getDefaultSharedPreferences(this);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        //cfg.useGL20 = false;
		cfg.hideStatusBar=false;
		cfg.useWakelock=false;
		int score=pref.getInt("score",0);
        this.game = new Power2Game(score);
        initialize(game, cfg);
    }
	
	@Override
	public void onGameOver()
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		int maxScore=this.game.getScore();
		pref.edit().putInt("score", maxScore).commit();
	}

	public void onResume(){
		super.onResume();
	}
}
