package com.unidevel.nosleep;

import android.app.Activity;
import android.app.Service;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Settings extends Activity {
	EditText txTimeout;
	IServiceState state;
	ServiceConnection conn;
	int startTime = -1;
	int stopTime = -1;
	int timeout = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		txTimeout = (EditText)findViewById(R.id.textTimeout);
		conn = new ServiceConnection(){
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				state = (IServiceState)service;
				timeout = state.getTimeout();
				txTimeout.setText(String.valueOf(state.getTimeout()));
				startTime = state.getStartTime();
				stopTime = state.getStopTime();
				
				CheckBox box;
				box = (CheckBox)findViewById(R.id.checkStart);
				box.setChecked(startTime>0);
				box.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton button, boolean checked) {
//						findViewById(R.id.btnSetStart).setEnabled(checked);
						findViewById(R.id.textStart).setEnabled(checked);
					}
				});
				updateStartStatus();
				
				box = (CheckBox)findViewById(R.id.checkStop);
				box.setChecked(stopTime>0);
				box.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton button, boolean checked) {
//						findViewById(R.id.btnSetStop).setEnabled(checked);
						findViewById(R.id.textStop).setEnabled(checked);
					}
				});
				updateStopStatus();
				
				box = (CheckBox)findViewById(R.id.checkTimeout);
				box.setChecked(timeout>0);
				box.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton button, boolean checked) {
						EditText text = (EditText)findViewById(R.id.textTimeout);
						text.setEnabled(checked);
						if ( !checked) text.setText("0");
					}
				});
				EditText text = (EditText)findViewById(R.id.textTimeout);
				text.setEnabled(box.isChecked());
				if ( !box.isChecked()) text.setText("0");
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				state = null;
				startTime = stopTime = -1;
			}
		};
		bindService(new Intent(NoSleepService.NAME), conn, Service.BIND_AUTO_CREATE);

		TextView text;
//		Button button;
//		button = (Button)findViewById(R.id.btnSetStart);
		text = (TextView)findViewById(R.id.textStart);
		text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int time = startTime;
				if ( time < 0 ) time = 8*60;
				TimePickerDialog dialog = new TimePickerDialog(Settings.this,
						new OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								startTime = hourOfDay*60+minute;
								updateStartStatus();
							}
				}, time/60, time%60, false);
				dialog.setTitle(getString(R.string.checkStart));
				dialog.show();
			}
		});

//		button = (Button)findViewById(R.id.btnSetStop);
		text = (TextView)findViewById(R.id.textStop);
		text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int time = stopTime;
				if ( time < 0 ) time = 22*60;
				TimePickerDialog dialog = new TimePickerDialog(Settings.this,
						new OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								stopTime = hourOfDay*60+minute;
								updateStopStatus();
							}
				}, time/60, time%60, false);
				dialog.setTitle(getString(R.string.checkStop));
				dialog.show();
			}
		});
		
		Button button = (Button)findViewById(R.id.btnApply);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				applySettings();
				Settings.this.finish();
			}
		});
		
		button = (Button)findViewById(R.id.btnCancel);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Settings.this.finish();
			}
		});
	}
	
	void updateTime(TextView view, int time){
		if ( time < 0 ) {
			view.setText("--:--");
			view.setEnabled(false);
		}
		else {
			int hour = time / 60;
			int min = time % 60;
			view.setText((hour<10?"0"+hour:String.valueOf(hour))+":"+(min<10?"0"+min:String.valueOf(min)));
			view.setEnabled(true);
		}
	}
	
	public void updateStopStatus(){
		CheckBox box;
		TextView text;
		box = (CheckBox)findViewById(R.id.checkStop);
//		findViewById(R.id.btnSetStop).setEnabled(box.isChecked());
		text = (TextView)findViewById(R.id.textStop);
		updateTime(text, box.isChecked()?stopTime:-1);
	}
	
	public void updateStartStatus(){
		CheckBox box;
		TextView text;
		box = (CheckBox)findViewById(R.id.checkStart);
//		findViewById(R.id.btnSetStart).setEnabled(box.isChecked());
		text = (TextView)findViewById(R.id.textStart);
		updateTime(text, box.isChecked()?startTime:-1);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if ( conn != null )	unbindService(conn);
		conn = null;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void applySettings(){
		String sTimeout = txTimeout.getText().toString();
		int timeout = -1;
		try {
			timeout = Integer.valueOf(sTimeout);
		}catch(Throwable ex){}
		if ( state != null && timeout >= 0 ) state.setTimeout(timeout);
		CheckBox box;
		box = (CheckBox)findViewById(R.id.checkStart);
		state.setStartTime(box.isChecked()?startTime:-1);
		box = (CheckBox)findViewById(R.id.checkStop);
		state.setStopTime(box.isChecked()?stopTime:-1);
		state.saveSettings();
	}
}
