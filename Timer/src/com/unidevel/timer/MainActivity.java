package com.unidevel.timer;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.media.*;
import android.net.*;

public class MainActivity extends Activity
{
	boolean started;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		this.started=false;
    }
	
	public void onStartStop(View view){
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
		if (resultCode == Activity.RESULT_OK && requestCode == 5)
		{
			Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			if (uri != null)
			{
				//this.chosenRingtone = uri.toString();
			}
			else
			{
				//this.chosenRingtone = null;
			}
		}            
	}
}
