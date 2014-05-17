package com.unidevel.power2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements GameListener
{
	static final String SCORE = "score";
	static final String MAX_SCORE = "max_score";
	static final String DATA = "data";
	
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
        this.game = new Power2Game();
        this.game.setGameListener( this );
        initialize(game, cfg);
    }
	
    @Override
    protected void onRestart()
    {
    	super.onRestart();
//        onGameResume();
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
        onGameResume();
	}
    
	@Override
	public void onGameOver()
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		int maxScore=this.game.getMaxScore();
		pref.edit().putInt(MAX_SCORE, maxScore).remove( DATA ).remove( SCORE ).commit();
	}

	@Override
	public void onGamePause()
	{
		int maxScore = this.game.getMaxScore();
		int score = this.game.getScore();
		int[] data = this.game.getData();
		String ds = toString(data);
		pref.edit().putInt(MAX_SCORE, maxScore).putString( DATA, ds ).putInt( SCORE, score ).commit();
	}
	
	@Override
	public void onGameResume()
	{
		int maxScore = pref.getInt( MAX_SCORE, 0 );
		int score = pref.getInt( SCORE, 0 );
		String ds = pref.getString( DATA, null );
		this.game.setMaxScore( maxScore );
		this.game.setScore( score );
		if ( ds!= null && ds.length()>0 )
		{
			int[] data = toArray(ds);
			this.game.setData( data );
		}
	}
	
	private int[] toArray(String value)
	{
		String[] items = value.split( "," );
		int[] data = new int[items.length];
		for ( int i = 0; i < data.length; i++ )
		{
			try 
			{
				data[i] = Integer.valueOf( items[i] );
			}
			catch(Exception ex){}
		}
		return data;
	}
	
	private String toString(int[] data)
	{
		StringBuffer buf = new StringBuffer();
		for ( int d: data)
		{
			if ( buf.length() != 0 )
				buf.append( "," );
			buf.append( d );
		}
		return buf.toString();
	}
}
