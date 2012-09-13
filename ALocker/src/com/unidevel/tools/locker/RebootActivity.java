package com.unidevel.tools.locker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;


public class RebootActivity extends Activity implements OnClickListener {
	public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
	
	public static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
//	   2030:    public static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP";
//	   2037:    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
//	   2045:    public static final String EXTRA_UID = "android.intent.extra.UID";
//	   2053:    public static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED";
//	   2061:    public static final String EXTRA_REPLACING = "android.intent.extra.REPLACING";
//	   2071:    public static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT";
//	   2080:    public static final String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE";
//	   2104:    public static final String METADATA_DOCK_HOME = "android.dock_home";
//	   2112:    public static final String EXTRA_BUG_REPORT = "android.intent.extra.BUG_REPORT";
//	Button btnYes, btnNo;
	View imgReboot, imgLock, imgShutdown, imgCancel, labelReboot, labelLock, labelShutdown, labelCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools);
		imgReboot = findViewById(R.id.imgReboot);
		imgShutdown = findViewById(R.id.imgShutdown);
		imgLock = findViewById(R.id.imgLock);
		imgCancel = findViewById(R.id.imgCancel);
		labelReboot = findViewById(R.id.labelReboot);
		labelLock = findViewById(R.id.labelLock);
		labelShutdown = findViewById(R.id.labelShutdown);
		labelCancel = findViewById(R.id.labelCancel);
		imgReboot.setOnClickListener(this);
		imgShutdown.setOnClickListener(this);
		imgLock.setOnClickListener(this);
		labelReboot.setOnClickListener(this);
		labelLock.setOnClickListener(this);
		labelShutdown.setOnClickListener(this);
		imgCancel.setOnClickListener(this);
		labelCancel.setOnClickListener(this);

		
//		setContentView(R.layout.rebootconfirm);
//		btnYes = (Button)findViewById(R.id.btnYes);
//		btnYes.setOnClickListener(this);
//		btnNo = (Button)findViewById(R.id.btnNo);
//		btnNo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if ( v == imgReboot || v == labelReboot ) {
			Intent intent = new Intent(Intent.ACTION_REBOOT);
			intent.putExtra("nowait", 1);  
			intent.putExtra("interval", 1);  
			intent.putExtra("window", 0);  
			sendBroadcast(intent);			
		}
		else if ( v == imgLock || v == labelLock ) {
			this.enforceCallingOrSelfPermission(android.Manifest.permission.DEVICE_POWER, null);
			PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
			pm.goToSleep(SystemClock.uptimeMillis());
		}
		else if ( v == imgShutdown || v == labelShutdown ) {
			Intent intent = new Intent(ACTION_REQUEST_SHUTDOWN);  
			intent.putExtra(EXTRA_KEY_CONFIRM, false);  
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			startActivity(intent);  
		}
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
