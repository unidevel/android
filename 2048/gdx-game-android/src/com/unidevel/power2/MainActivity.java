package com.unidevel.power2;

import java.io.File;
import java.io.IOException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AndroidApplication implements GameListener
{
	static final String SCORE = "score";
	static final String MAX_SCORE = "max_score";
	static final String DATA = "data";
	
	Handler handler;
	Power2Game game;
	SharedPreferences pref;
	View gameView;
	AdView adView;
	//a15377fb6dcdf79 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
		pref=PreferenceManager.getDefaultSharedPreferences(this);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        //cfg.useGL20 = false;
        this.game = new Power2Game();
        this.game.setGameListener( this );
//        initialize(game, cfg);
        
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        this.gameView = initializeForView( game, cfg );
        
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-2348443469199344/1566590918");
        adView.setAdSize(AdSize.BANNER);
        
        //        	AdView adView = new AdView(this, AdSize.BANNER, "a15377fb6dcdf79"); // Put in your secret key here
        RelativeLayout layout = new RelativeLayout(this);
        // Add the libgdx view
        layout.addView(gameView);
        
        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest request = new AdRequest();
        adView.loadAd(adRequest);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams = 
            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(adView, adParams);
        
        setContentView(layout);
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
        adView.resume();
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
		
		builder.setNegativeButton( R.string.exit, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
				finish();
			}
		} );
		/*
		builder.setNegativeButton( R.string.share, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
				shareScreen(game.getScore(), screenShot);
			}
		} );
		*/ 
		builder.setTitle( R.string.game_over );
		builder.create().show();		
	}
	
	private void shareScreen(){
		game.addToRendererQueue( new Runnable(){
			@Override
			public void run()
			{
				Pixmap pixmap = game.getScreenshot();
				if ( pixmap == null ) 
					return;
				FileHandle fh = Gdx.files.local( "2048.png" );
				try
				{
					write(fh, pixmap);
				}
				catch(Exception ex)
				{
					Log.e( ex );
					//Toast.makeText( this, ex.getLocalizedMessage(), Toast.LENGTH_LONG ).show();
					return;
				}
				finally
				{
					pixmap.dispose();
				}
				Intent intent = new Intent( Intent.ACTION_SEND );
				intent.setType( "image/*" );
				Uri u = Uri.fromFile(fh.file());
				intent.putExtra( Intent.EXTRA_STREAM, u);
				//f.deleteOnExit();
				String shareText = getString(R.string.share_text, game.getScore());
				intent.putExtra( Intent.EXTRA_TEXT, shareText );
				intent.putExtra( "Kdescription", shareText );
				intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				//intent.setClassName( MMPKG, MMCLS );
				startActivity( intent );
			}
		});
	}
	
	public void write(FileHandle handle, Pixmap p) throws IOException {
	      int w = p.getWidth();
	      int h = p.getHeight();
	      
	      int[] pixels = new int[w * h];      
	      for (int y=0; y<h; y++) {
	         for (int x=0; x<w; x++) {
	            //convert RGBA to RGB
	            int value = p.getPixel(x, y);
	            int R = ((value & 0xff000000) >>> 24);
	            int G = ((value & 0x00ff0000) >>> 16);
	            int B = ((value & 0x0000ff00) >>> 8);
	            int A = ((value & 0x000000ff));
	            
	            int i = x + (y * w);
	            pixels[ i ] = (A << 24) | (R << 16) | (G << 8) | B;
	         }
	      }
	      
	      Bitmap b = Bitmap.createBitmap(pixels, w, h, Config.ARGB_8888);
	      b.compress(CompressFormat.PNG, 80, handle.write(false));
	   }
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		super.onCreateOptionsMenu( menu );
		MenuInflater inflater = new MenuInflater( this );
		inflater.inflate( R.menu.menu, menu );
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		if ( R.id.newGame == item.getItemId() )
		{
			game.newGame();
		}
		else if ( R.id.rateApp == item.getItemId() )
		{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData( Uri.parse("market://details?id=com.unidevel.power2") );
			startActivity( intent );
		}
		else if ( R.id.share == item.getItemId() )
		{
			this.shareScreen();
		}
		else if ( R.id.submit == item.getItemId() )
		{
			this.submitScore();
		}
		return true;
	}
	
	//1 High score CgkImN3rmbgNEAIQAQ
	//TRIANGLE 2048 461763178136
	private void submitScore()
	{
//		Games.Leaderboards.submitScore(getApiClient(), LEADERBOARD_ID, 1337);
//		startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), LEADERBOARD_ID), REQUEST_LEADERBOARD);
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
	
	@Override
	protected void onPause()
	{
		adView.pause();
		super.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		adView.destroy();
	}
}
