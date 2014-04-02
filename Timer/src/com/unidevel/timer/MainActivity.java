package com.unidevel.timer;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.*;
import android.media.*;

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
	ImageButton btnStart;
	ImageButton btnReset;
	
	long alarmTime;
	long alarmEndTime;
	long alarmWarnTime;
	
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
    }
	
    private void updateUI()
    {
   		this.sbAlarm.setEnabled( !this.started );
   		this.sbAlarmEnd.setEnabled( !this.started );
   		this.sbAlarmWarn.setEnabled( !this.started );
   		if ( this.started )
   		{
   			this.btnStart.setImageResource( android.R.drawable.ic_media_pause );
   		}
   		else
   		{
   			this.btnStart.setImageResource( android.R.drawable.ic_media_play );
   		}
   	}
    
    private void updateTime()
    {
    	handler.post( new Runnable(){
    		@Override
    		public void run()
    		{
    			 int seconds =(int) (time/1000.0);
				 String message=""+seconds;
				 messageView.setText(message);
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
			this.timer.schedule( task, 0, 100 );
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
				switch(requestCode)
				{
					case R.id.alarm:
						this.alarmUri = uri.toString();
						break;
					case R.id.alarmEnd:
						this.alarmEndUri = uri.toString();
						break;
					case R.id.alarmWarn:
						this.alarmWarnUri = uri.toString();
						break;
				}
			}
		}            
	}

	@Override
	public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
	{
		if ( fromUser )
		{
			if ( seekBar == this.sbAlarm )
			{
				this.alarmTime = seekBar.getProgress();
			}
			if ( seekBar == this.sbAlarmEnd )
			{
				this.alarmTime = seekBar.getProgress();
			}
			if ( seekBar == this.sbAlarmWarn )
			{
				this.alarmTime = seekBar.getProgress();
			}
		}
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
