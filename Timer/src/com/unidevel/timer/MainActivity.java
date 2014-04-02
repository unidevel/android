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
	
	SeekBar sbAlarm ;
	SeekBar sbAlarmEnd;
	SeekBar sbAlarmWarn;
	TextView messageView;
	View cardView;
	ImageButton btnStart;
	ImageButton btnReset;
	
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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		this.started=false;
		this.sbAlarm = (SeekBar)this.findViewById( R.id.alarmBar );
		this.sbAlarmEnd = (SeekBar)this.findViewById( R.id.alarmEndBar );
		this.sbAlarmWarn = (SeekBar)this.findViewById( R.id.alarmWarnBar );
		this.sbAlarm.setOnSeekBarChangeListener( this );
		this.sbAlarmEnd.setOnSeekBarChangeListener( this );
		this.sbAlarmWarn.setOnSeekBarChangeListener( this );
		this.btnStart = (ImageButton)this.findViewById( R.id.start );
		this.btnReset = (ImageButton)this.findViewById( R.id.reset );
		this.handler = new Handler();
		this.messageView = (TextView)this.findViewById(R.id.time);
		this.cardView = this.findViewById( R.id.card );
		this.time = 0;
		this.alarmEndPlayed = false;
		this.alarmPlayed = false;
		this.alarmWarnPlayed = false;
    }
	
    @Override
    protected void onResume()
    {
    	super.onResume();
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
    	this.alarmUri = pref.getString( "alarm", null );
    	this.alarmEndUri = pref.getString( "alarm_end", null );
    	this.alarmWarnUri = pref.getString( "alarm_warn", null );
    }
    
    private void updateUI()
    {
   		//this.sbAlarm.setEnabled( !this.started );
   		//this.sbAlarmEnd.setEnabled( !this.started );
   		//this.sbAlarmWarn.setEnabled( !this.started );
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
    	handler.post( new Runnable(){
    		@Override
    		public void run()
    		{
    			 int seconds =(int) (time/1000.0);
    			 int left = alarmEndTime - seconds;
    			 int color = Color.BLACK;
    			 
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
					 color = Color.YELLOW;
    			 }
    			 else if ( seconds >= alarmTime )
    			 {
    				 if ( !alarmPlayed )
    				 {
    					 play( alarmUri );
    					 alarmPlayed = true;
    				 }
					 color = Color.GREEN;
					 blink = true;
    			 }
    			 else
    			 {
    				 blink = true;
    			 }
    			 String message="Total: " + alarmEndTime+" seconds\nWarning: "+alarmTime+" seconds\nLast warning: "+alarmWarnTime+" seconds\nTime: "+seconds+" seconds\nLeft: "+left+" seconds\n";
    			 messageView.setText(message);
				 cardView.setBackgroundColor( blink? color: Color.BLACK );
    		}
    	});
    }
    
	public void onStartStop(View view){
		this.started = !this.started;
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
		if ( this.started )
		{
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
	
	public void onReset(View view){
		this.time = 0;
		this.alarmEndPlayed = false;
		this.alarmPlayed = false;
		this.alarmWarnPlayed = false;
		updateUI();
		updateTimeDisplay();
	}
	
	public void onChooseAlarm(View view){
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		this.startActivityForResult(intent, view.getId());
	}
	
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			if (uri != null)
			{
				//this.chosenRingtone = uri.toString();
				String key = null; 
				switch(requestCode)
				{
					case R.id.alarm:
						this.alarmUri = uri.toString();
						key =  "alarm";
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
	
	private void adjustSeekBar(SeekBar seekBar, int max, int delta)
	{
		seekBar.setMax( max );
		//if ( seekBar.getProgress() > max )
		{
			int newValue = max - delta;
			if (  newValue > 0 )
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
				this.alarmTime = seekBar.getProgress()*30;
			}
			if ( seekBar == this.sbAlarmEnd )
			{
				this.alarmEndTime = seekBar.getProgress()*30;
				adjustSeekBar(this.sbAlarmWarn, seekBar.getProgress(), 1);
				adjustSeekBar(this.sbAlarm, seekBar.getProgress(), 2);
			}
			if ( seekBar == this.sbAlarmWarn )
			{
				this.alarmWarnTime = seekBar.getProgress()*30;
				adjustSeekBar(this.sbAlarm, seekBar.getProgress(), 1);
			}
		}
		updateTimeDisplay();
	}
	
	private void updateTimeDisplay()
	{
		 String message="Total: " + alarmEndTime+" seconds\nWarning: "+alarmTime+" seconds\nLast warning: "+alarmWarnTime+" seconds\n";
		 messageView.setText(message);
	}

	public void play(String uri){
		try {
			Uri n;
			if(uri==null){
				n = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
			else{
				n=Uri.parse(uri);
			}
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), n);
			r.play();
		} catch (Exception e) {
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
}
