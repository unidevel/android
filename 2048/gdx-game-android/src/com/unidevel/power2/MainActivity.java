package com.unidevel.power2;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.unidevel.power2.GameHelper.SignInFailureReason;
//import com.badlogic.gdx.graphics.g3d.*;

@SuppressLint ("InlinedApi")
public class MainActivity extends AndroidApplication implements GameListener,GameHelper.GameHelperListener
{
	static final String SCORE = "score";
	static final String LEVEL = "level";
	static final String MAX_SCORE = "max_score";
	static final String MAX_LEVEL = "max_number";
	static final String DATA = "data";
	boolean showLeaderboard = true;
	Handler handler;
	Power2Game game;
	SharedPreferences pref;
	View gameView;
	AdView adView;
	int level;
	protected GameHelper mHelper;

    // We expose these constants here because we don't want users of this class
    // to have to know about GameHelper at all.
    public static final int CLIENT_GAMES = GameHelper.CLIENT_GAMES;
    public static final int CLIENT_APPSTATE = GameHelper.CLIENT_APPSTATE;
    public static final int CLIENT_PLUS = GameHelper.CLIENT_PLUS;
    public static final int CLIENT_ALL = GameHelper.CLIENT_ALL;

    // Requested clients. By default, that's just the games client.
    protected int mRequestedClients = CLIENT_GAMES;
    protected int REQUEST_LEADERBOARD = 100;
    protected int REQUEST_ACHIEVEMENT = 200;
    
    public MainActivity()
	{
    	super();
	}
    
	//a15377fb6dcdf79 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
		mHelper = new GameHelper( this, mRequestedClients );
		mHelper.setup( this );
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
        //adView.setVisibility(View.GONE);
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
    protected void onStart()
    {
    	super.onStart();
		//mHelper.onStart(this);
    }
	
	//	@Override
	protected void onStop()
	{
		super.onStop();
		//mHelper.onStop();
	}
	
	public void login() {
		try {
			runOnUiThread(new Runnable(){
				public void run(){
					mHelper.beginUserInitiatedSignIn();
				}
			});
		}catch (final Exception ex){

		}
	}

	@Override
	protected void onActivityResult( int request, int response, Intent data )
	{
		super.onActivityResult( request, response, data );
		mHelper.onActivityResult( request, response, data );
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
        adView.resume();
        loadGame();
	}
    
	@Override
	public void onGameOver(final File screenShot)
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		saveGame();
		pref.edit().remove( DATA ).commit();
		
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
		builder.setNeutralButton( R.string.achievement, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					dialog.dismiss();
					submitScore(false);
				}
			} );
		
		/*
		builder.setNegativeButton( R.string.exit, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
				finish();
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
		*/ 
		
		builder.setNegativeButton( R.string.submit_score, new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					dialog.dismiss();
					//finish();
					submitScore(true);
				}
			} );

		
		builder.setMessage( R.string.game_over );
		builder.create().show();		
	}
	
	String  getAppFile(String name){
		String state=Environment.getExternalStorageState();
		File f=null;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			String p="Android/data/"+this.getPackageName();
			File dir=new File(Environment.getExternalStorageDirectory(),p);
			if(!dir.exists())dir.mkdirs();
			return "/"+p+"/"+name;//new File(dir, name);
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
		}
		return null;
	}
	
	private void shareScreen(){
		game.addToRendererQueue( new Runnable(){
			@SuppressWarnings ("unused")
			@Override
			public void run()
			{
				String f = getAppFile("2048.png");
				Log.i("Save screenshot to"+f);//.getPath());
				if( f == null )
				{
					Toast.makeText( MainActivity.this, R.string.sdcard_error, Toast.LENGTH_LONG ).show();
					return;
				}
				Pixmap pixmap = game.getScreenshot();
				if ( pixmap == null )
				{
					Toast.makeText( MainActivity.this, R.string.screenshot_error, Toast.LENGTH_LONG ).show();
					return;
				}
				FileHandle fh = Gdx.files.external( f);//.getPath() );
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
				String shareText = getString(R.string.share_text, game.getScore(), game.getLevel());
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
	
	public void newGame(){
		if(!game.over){
			AlertDialog.Builder b=new AlertDialog.Builder(this);
			b.setTitle(R.string.new_game);
			b.setMessage(R.string.new_game_warning);
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface d, int p2)
					{
						d.dismiss();
						game.newGame();
					}
				});
			b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface d, int p2)
					{
						d.dismiss();
					}
				});
			b.create().show();
		}
		else{
			game.newGame();
		}
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		if ( R.id.newGame == item.getItemId() )
		{
			newGame();
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
			this.submitScore(true);
		}
		else if ( R.id.exitApp == item.getItemId() )
		{
			this.finish();
		}
		else if ( R.id.undo == item.getItemId() )
		{
			this.undo();
		}
		else if ( R.id.achivement == item.getItemId() )
		{
			this.submitScore(false);
		}
		return true;
	}
	
	public void undo(){
		if(game.canUndo()){
			game.undo();
		}
		else{
			Toast.makeText(this, R.string.cannot_undo, Toast.LENGTH_LONG).show();
		}
	}
	
	//1 High score CgkImN3rmbgNEAIQAQ
	//TRIANGLE 2048 461763178136
	private void submitScore( boolean showLeaderboard ){
		this.showLeaderboard = showLeaderboard;
		Gdx.graphics.requestRendering();
		login();
	}
	
	@Override
	public void onGamePause()
	{
		saveGame();
	}
	
	private void saveGame()
	{
		int maxScore = this.game.getMaxScore();
		int maxNumber=this.game.getMaxLevel();
		int score = this.game.getScore();
		int level = this.game.getLevel();
		this.level=level;
		int[] data = this.game.getData();
		String ds = toString(data);
		pref.edit().putInt(MAX_SCORE, maxScore)
			.putString( DATA, ds )
			.putInt( SCORE, score )
			.putInt(MAX_LEVEL,maxNumber)
			.putInt( LEVEL, level )
			.commit();
	}
	
	private void loadGame()
	{
		int maxScore = pref.getInt( MAX_SCORE, 0 );
		int maxNumber = pref.getInt( MAX_LEVEL, 0 );
		int score = pref.getInt( SCORE, 0 );
		String ds = pref.getString( DATA, null );
		this.game.setMaxScore( maxScore );
		this.game.setMaxNumber( maxNumber );
		this.game.setScore( score );
		if ( ds!= null && ds.length()>0 )
		{
			int[] data = toArray(ds);
			this.game.setData( data );
		}
	}
	
	@Override
	public void onGameResume()
	{
		
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
	
	@Override
	public void onSignInFailed()
	{
		SignInFailureReason result = mHelper.getSignInError();
		android.util.Log.e( "MainActivity", "Login failed with error code "+result.mServiceErrorCode+", "+result.toString() );
	}
	
	@Override
	public void onSignInSucceeded()
	{
		String leaderboardId = getString(R.string.leaderboard_high_score);
		String leaderboardId2 = getString(R.string.leaderboard_max_number);
		String leaderboardId3 = getString(R.string.leaderboard_score_today);
		Games.Leaderboards.submitScore( mHelper.getApiClient(), leaderboardId, game.getMaxScore() );
		Games.Leaderboards.submitScore( mHelper.getApiClient(), leaderboardId2, game.getMaxLevel() );
		Games.Leaderboards.submitScore( mHelper.getApiClient(), leaderboardId3, game.getScore() );
		int levelId = 0;
		if(this.level>=2048){
			levelId = R.string.level5;
		}
		else if(this.level>=1024){
			levelId = R.string.level4;
		}
		else if(this.level>=512){
			levelId = R.string.level3;
		}
		else if(this.level>=256){
			levelId = R.string.level2;
		}
		else if(this.level>=128){
			levelId = R.string.level1;
		}
		else if(this.level>=64){
			levelId = R.string.level0;
		}
		if ( levelId != 0 )
		{
			Games.Achievements.unlock( mHelper.getApiClient(), getString(levelId) );
		}
		if ( showLeaderboard )
		{
			Intent intent = Games.Leaderboards.getAllLeaderboardsIntent( mHelper.getApiClient());
			//Intent intent = Games.Leaderboards.getLeaderboardIntent( mHelper.getApiClient(), leaderboardId );
			startActivityForResult(intent, REQUEST_LEADERBOARD);
		}
		else
		{
			Intent intent = Games.Achievements.getAchievementsIntent( mHelper.getApiClient() );
			startActivityForResult(intent, REQUEST_ACHIEVEMENT);
		}
	}
}
