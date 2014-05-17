package com.unidevel.power2;

import java.io.File;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements GameListener
{
	static final String SCORE = "score";
	static final String MAX_SCORE = "max_score";
	static final String DATA = "data";
	
	Handler handler;
	Power2Game game;
	SharedPreferences pref;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
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
	public void onGameOver(final File screenShot)
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		int maxScore=this.game.getMaxScore();
		pref.edit().putInt(MAX_SCORE, maxScore).remove( DATA ).remove( SCORE ).commit();
		
		this.handler.post( new Runnable(){
			@Override
			public void run()
			{
				showPlayAgainDialog(screenShot);
			}
		});
	}
	
	private void showPlayAgainDialog(final File screenShot){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton( R.string.play_again, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
				game.newGame();
			}
		} );
		
		builder.setNegativeButton( R.string.share, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
				shareScreen(game.getScore(), screenShot);
			}
		} );
		builder.setTitle( R.string.game_over );
		builder.create().show();		
	}

	private void shareScreen(int score, File screenShot){
		if ( screenShot == null )
		{
			return;
		}
		Intent intent = new Intent( Intent.ACTION_SEND );
		intent.setType( "image/*" );
		Uri u = Uri.fromFile(screenShot);
		intent.putExtra( Intent.EXTRA_STREAM, u);
		//f.deleteOnExit();
		String shareText = getString(R.string.share_text, score);
		intent.putExtra( Intent.EXTRA_TEXT, shareText );
		intent.putExtra( "Kdescription", shareText );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		//intent.setClassName( MMPKG, MMCLS );
		startActivity( intent );
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
