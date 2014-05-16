
package com.unidevel.timer;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSeekBarChangeListener
{
	Handler handler;
	boolean started;
	String alarmUri;
	String alarmWarnUri;
	String alarmEndUri;

	SeekBar sbAlarm;
	SeekBar sbAlarmEnd;
	SeekBar sbAlarmWarn;
	TextView messageView;
	View cardView;
	ImageButton btnStart;
	ImageButton btnReset;
	ImageButton btnAdd;
	int alarmTime;
	int alarmEndTime;
	int alarmWarnTime;

	boolean alarmPlayed;
	boolean alarmEndPlayed;
	boolean alarmWarnPlayed;
	boolean blink;

	long time;
	long lastTime;

	Timer timer;
	int uint=30;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		this.started = false;
		this.sbAlarm = (SeekBar)this.findViewById( R.id.alarmBar );
		this.sbAlarmEnd = (SeekBar)this.findViewById( R.id.alarmEndBar );
		this.sbAlarmWarn = (SeekBar)this.findViewById( R.id.alarmWarnBar );
		this.sbAlarm.setOnSeekBarChangeListener( this );
		this.sbAlarmEnd.setOnSeekBarChangeListener( this );
		this.sbAlarmWarn.setOnSeekBarChangeListener( this );
		this.btnStart = (ImageButton)this.findViewById( R.id.start );
		this.btnReset = (ImageButton)this.findViewById( R.id.reset );
		this.handler = new Handler();
		this.messageView = (TextView)this.findViewById( R.id.time );
		this.cardView = this.findViewById( R.id.card );
		this.time = 0;
		this.alarmEndPlayed = false;
		this.alarmPlayed = false;
		this.alarmWarnPlayed = false;

		this.btnAdd = (ImageButton)this.findViewById( R.id.newTimer );
		this.addNewTimer();
		/* IntentFilter mediaButtonIntentFilter = new IntentFilter( Intent.ACTION_MEDIA_BUTTON );
		receiver = new MediaButtonBroadcastReceiver();
		this.registerReceiver( receiver, mediaButtonIntentFilter ); */
	}

	protected void addNewTimer()
	{
		this.btnAdd.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				Intent intent = new Intent(MainActivity.this, SubActivity.class);
				startActivity( intent );
			}
		} );
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//this.unregisterReceiver( receiver );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		this.alarmUri = pref.getString( "alarm", null );
		this.alarmEndUri = pref.getString( "alarm_end", null );
		this.alarmWarnUri = pref.getString( "alarm_warn", null );
		updateTimeDisplay();
	}

	private void updateUI()
	{
		// this.sbAlarm.setEnabled( !this.started );
		// this.sbAlarmEnd.setEnabled( !this.started );
		// this.sbAlarmWarn.setEnabled( !this.started );
		this.btnReset.setEnabled( !this.started );
		if ( this.started )
		{
			this.btnStart.setImageResource( android.R.drawable.ic_media_pause );
		}
		else
		{
			this.btnStart.setImageResource( android.R.drawable.ic_media_play );
		}
		this.cardView.setBackgroundColor( Color.BLACK );
	}

	private void updateTime()
	{
		handler.post( new Runnable()
		{
			@Override
			public void run()
			{
				int color = Color.BLACK;
				int seconds = (int)(time / 1000.0);

				if ( seconds >= alarmEndTime )
				{
					if ( !alarmEndPlayed )
					{
						play( alarmEndUri );
						alarmEndPlayed = true;
					}
					blink = !blink;
					color = Color.RED;
				}
				else if ( seconds >= alarmWarnTime )
				{
					if ( !alarmWarnPlayed )
					{
						play( alarmWarnUri );
						alarmWarnPlayed = true;
					}
					blink = !blink;
					// color = Color.YELLOW;
					int delta = seconds - alarmWarnTime;
					int totalDelta = alarmEndTime - alarmWarnTime;
					if ( totalDelta == 0 || delta == 0 )
					{
						color = Color.YELLOW;
					}
					else
					{
						color = 0xFFFF0000;
						int g = 255 - (int)((float)255 * (float)delta / (float)totalDelta);
						color |= g << 8;
					}
				}
				else if ( seconds >= alarmTime )
				{
					if ( !alarmPlayed )
					{
						play( alarmUri );
						alarmPlayed = true;
					}
					blink = true;
					color = Color.GREEN;
					int delta = seconds - alarmTime;
					int totalDelta = alarmWarnTime - alarmTime;
					if ( totalDelta == 0 || delta == 0 )
					{
						color = Color.GREEN;
					}
					else
					{
						color = 0xFF00FF00;
						int g = (int)((float)255 * (float)delta / (float)totalDelta);
						color |= g << 16;
					}
				}
				else
				{
					blink = true;
				}
				updateTimeDisplay();
				cardView.setBackgroundColor( blink
						? color
						: Color.BLACK );
			}
		} );
	}

	public void onStartStop( View view )
	{
		this.started = !this.started;
		
		if ( this.started )
		{
			TimerTask task = new TimerTask()
			{
				@Override
				public void run()
				{
					time += System.currentTimeMillis() - lastTime;
					lastTime = System.currentTimeMillis();
					updateTime();
				}
			};
			this.lastTime = System.currentTimeMillis();
			this.timer = new Timer();
			this.timer.schedule( task, 0, 500 );
		}
		else
		{
			this.timer.cancel();
			this.timer = null;
		}
		updateUI();
	}

	public void onReset( View view )
	{
		this.time = 0;
		this.alarmEndPlayed = false;
		this.alarmPlayed = false;
		this.alarmWarnPlayed = false;
		updateUI();
		updateTimeDisplay();
	}

	public void onPlayAlarm(View view)
	{
		if ( view == null )
			return;
		if ( view.getId() == R.id.alarmPlay )
		{
			play(alarmUri);
		}
		else if ( view.getId() == R.id.alarmEndPlay )
		{
			play(alarmEndUri);
		}
		else if ( view.getId() == R.id.alarmWarnPlay )
		{
			play(alarmWarnUri);
		}
	}
	
	public void onChooseAlarm( View view )
	{
		Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER );
		intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION );
		intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone" );
		intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri)null );
		this.startActivityForResult( intent, view.getId() );
	}

	protected void onActivityResult( final int requestCode, final int resultCode, final Intent intent )
	{
		if ( resultCode == Activity.RESULT_OK )
		{
			Uri uri = intent.getParcelableExtra( RingtoneManager.EXTRA_RINGTONE_PICKED_URI );

			if ( uri != null )
			{
				// this.chosenRingtone = uri.toString();
				String key = null;
				switch (requestCode)
				{
					case R.id.alarm:
						this.alarmUri = uri.toString();
						key = "alarm";
						break;
					case R.id.alarmEnd:
						this.alarmEndUri = uri.toString();
						key = "alarm_end";
						break;
					case R.id.alarmWarn:
						this.alarmWarnUri = uri.toString();
						key = "alarm_warn";
						break;
				}
				if ( key != null )
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
					pref.edit().putString( key, uri.toString() ).commit();
				}
			}
		}
	}

	private void adjustSeekBar( SeekBar seekBar, int max, int delta )
	{
		seekBar.setMax( max );
		// if ( seekBar.getProgress() > max )
		{
			int newValue = max - delta;
			if ( newValue > 0 )
			{
				seekBar.setProgress( newValue );
			}
		}
	}

	@Override
	public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
	{
		if ( true )
		{
			if ( seekBar == this.sbAlarm )
			{
				this.alarmTime = seekBar.getProgress() * uint;
			}
			if ( seekBar == this.sbAlarmEnd )
			{
				this.alarmEndTime = seekBar.getProgress() * uint;
				adjustSeekBar( this.sbAlarmWarn, seekBar.getProgress(), 1 );
				adjustSeekBar( this.sbAlarm, seekBar.getProgress(), 2 );
			}
			if ( seekBar == this.sbAlarmWarn )
			{
				this.alarmWarnTime = seekBar.getProgress() * uint;
				adjustSeekBar( this.sbAlarm, seekBar.getProgress(), 1 );
			}
		}
		updateTimeDisplay();
	}

	private String formatTime( int seconds )
	{
		if ( seconds < 0 )
		{
			seconds = -seconds;
		}
		int m = seconds / 60;
		int s = seconds % 60;
		return "" + (m < 10
				? "0" + m
				: m) + ":" + (s < 10
				? "0" + s
				: s);
	}

	private void updateTimeDisplay()
	{
		int seconds = (int)(time / 1000.0);
		int left = alarmEndTime - seconds;
		String message =
				"Total: " + formatTime( alarmEndTime ) + "\nFirst: " + formatTime( alarmTime ) + "\nLast : "
						+ formatTime( alarmWarnTime ) + "\n";
		message += "------------\nTime : " + formatTime( seconds ) + "\nLeft : " + formatTime( left ) + "\n";
		messageView.setText( message );
	}

	public void play( String uri )
	{
		try
		{
			Uri n;
			if ( uri == null )
			{
				n = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
			}
			else
			{
				n = Uri.parse( uri );
			}
			Ringtone r = RingtoneManager.getRingtone( getApplicationContext(), n );
			r.play();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onStartTrackingTouch( SeekBar seekBar )
	{
	}

	@Override
	public void onStopTrackingTouch( SeekBar seekBar )
	{
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		Log.i( "onKeyDown", "keyCode=" + keyCode );
		if ( keyCode == KeyEvent.KEYCODE_MEDIA_NEXT || keyCode ==KeyEvent.KEYCODE_MEDIA_PREVIOUS 
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
			if ( !this.started )
			{
				this.onReset( this.btnReset );
				return true;
			}
			return false;
		}
		else if ( keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE  || keyCode == KeyEvent.KEYCODE_HEADSETHOOK )
		{
			this.onStartStop( this.btnStart );
			return true;
		}
		return super.onKeyDown( keyCode, event );
	}
	
	private void setMax(int max){
		int p=max/uint;
		sbAlarm.setMax(p);
		sbAlarmWarn.setMax(p);
		sbAlarmEnd.setMax(p);
	}
	
	
	private void setAlarm(int alarm, int warn, int end){
		int p1=alarm/uint;
		int p2=warn/uint;
		int p3=end/uint;
		sbAlarm.setProgress(p1);
		sbAlarmWarn.setProgress(p2);
		sbAlarmEnd.setProgress(p3);
	}
	public void onSet1(View v){
		setMax(720);
		setAlarm(40,50,60);
	}
	public void onSet2(View v){
		setMax(720);
		setAlarm(75,100,120);
	}
	public void onSet3(View v){
		setMax(720);
		setAlarm(135,155,180);
	}
	public void onSet4(View v){
		setMax(720);
		setAlarm(180,210,240);
	}
	public void onSet5(View v){
		setMax(720);
		setAlarm(240,270,300);
	}
	public void onSet6(View v){
		setMax(720);
		setAlarm(240,330,360);
	}
	public void onSet20(View v){
		setMax(1200);
		setAlarm(1140,1170,1200);
	}
}
