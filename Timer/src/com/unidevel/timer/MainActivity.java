package com.unidevel.timer;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnSeekBarChangeListener
{
	boolean started;
	String alarmUri;
	String alarmWarnUri;
	String alarmEndUri;
	
	SeekBar sbAlarm ;
	SeekBar sbAlarmEnd;
	SeekBar sbAlarmWarn;
	
	Button btnStart;
	Button btnReset;
	
	long alarmTime;
	long alarmEndTime;
	long alarmWarnTime;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		this.started=false;
		this.sbAlarm = (SeekBar)this.findViewById( R.id.alarm );
		this.sbAlarmEnd = (SeekBar)this.findViewById( R.id.alarmEnd );
		this.sbAlarmWarn = (SeekBar)this.findViewById( R.id.alarmWarn );
		this.sbAlarm.setOnSeekBarChangeListener( this );
		this.sbAlarmEnd.setOnSeekBarChangeListener( this );
		this.sbAlarmWarn.setOnSeekBarChangeListener( this );
		this.btnStart = (Button)this.findViewById( R.id.start );
		this.btnReset = (Button)this.findViewById( R.id.reset );
    }
	
    private void updateUI()
    {
   		this.sbAlarm.setEnabled( !this.started );
   		this.sbAlarmEnd.setEnabled( !this.started );
   		this.sbAlarmWarn.setEnabled( !this.started );
   		if ( this.started )
   		{
   			this.btnStart.setBackgroundResource( android.R.drawable.ic_media_pause );
   		}
   		else
   		{
   			this.btnStart.setBackgroundResource( android.R.drawable.ic_media_play );
   		}
   		
    }
    
	public void onStartStop(View view){
		this.started = !this.started;
		updateUI();
	}
	
	public void onReset(View view){
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
				this.alarmTime = seekBar.getProgress()*500;
			}
			if ( seekBar == this.sbAlarmEnd )
			{
				this.alarmTime = seekBar.getProgress()*500;
			}
			if ( seekBar == this.sbAlarmWarn )
			{
				this.alarmTime = seekBar.getProgress()*500;
			}
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
