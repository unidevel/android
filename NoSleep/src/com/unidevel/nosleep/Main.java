package com.unidevel.nosleep;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {
	IServiceState state;
	String labelStart = "Start";
	String labelStop = "Stop";
	BroadcastReceiver startReceiver, stopReceiver;
	ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("onServiceConnected", String.valueOf(name));
			state = (IServiceState)service;
	        Button button = (Button)findViewById(R.id.btnStart);
	        button.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View view) {
					Button button = (Button)view;
					Intent intent = new Intent("NoSleepService");
					if ( state.isStarted() ) {
						Main.this.unbindService(conn);
						Main.this.stopService(intent);
						button.setText(labelStart);
						Main.this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
					}
					else {
						Main.this.startService(new Intent(NoSleepService.NAME));
						button.setText(labelStop);
					}
				}
	        });
	        
	        if ( state.isStarted() ) {
	        	button.setText(labelStop);
	        }
	        else {
	        	button.setText(labelStart);
	        }
	        
	        button = (Button)findViewById(R.id.btnSettings);
	        button.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View v) {
	        		Intent intent = new Intent(Main.this, Settings.class);
	        		Main.this.startActivity(intent);
	        	}
	        });
	        
//	        button = (Button)findViewById(R.id.btnQuit);
//	        if ( button != null )
//	        button.setOnClickListener(new OnClickListener(){
//	        	@Override
//	        	public void onClick(View v) {
//	        		Main.this.finish();
//	        	}
//	        });

	        button = (Button)findViewById(R.id.btnAbout);
	        if ( button != null )
	        button.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View v) {
	        		Intent intent = new Intent(Main.this, About.class);
	        		startActivity(intent);
	        	}
	        });
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("onServiceDisconnected", String.valueOf(name));
		}
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = new Intent(NoSleepService.NAME);
        this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
        labelStart = getString(R.string.btnStart);
        labelStop = getString(R.string.btnStop);
        startReceiver = new BroadcastReceiver(){
        	@Override
        	public void onReceive(Context context, Intent intent) {
    	        Button button = (Button)findViewById(R.id.btnStart);
				button.setText(labelStop);
        	}
        };
        registerReceiver(startReceiver, new IntentFilter(NoSleepService.NOSLEEP_START));
        stopReceiver = new BroadcastReceiver(){
        	@Override
        	public void onReceive(Context context, Intent intent) {
    	        Button button = (Button)findViewById(R.id.btnStart);
				button.setText(labelStart);
        	}
        };
        registerReceiver(stopReceiver, new IntentFilter(NoSleepService.NOSLEEP_STOP));
        
//        TextView text = (TextView)findViewById(R.id.labelLink);
//        text.setText(Html.fromHtml(getString(R.string.version)));
//        text.setClickable(true);
//        text.setLinksClickable(true);
//        text.setMovementMethod(LinkMovementMethod.getInstance());
//		AdManager.setTestDevices(new String[] { AdManager.TEST_EMULATOR, // Android
//																			// emulator
//				"E83D20734F72FB3108F104ABC0FFC738", // My T-Mobile G1 Test Phone
//		});
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	this.unbindService(conn);
    	unregisterReceiver(startReceiver);
    	unregisterReceiver(stopReceiver);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if ( keyCode == KeyEvent.KEYCODE_BACK ) {
    		Main.this.finish();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
}