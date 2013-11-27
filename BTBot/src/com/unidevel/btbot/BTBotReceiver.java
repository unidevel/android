package com.unidevel.btbot;

import android.bluetooth.*;
import android.content.*;
import android.media.*;
import android.preference.*;
import android.telephony.*;
import android.util.*;

public class BTBotReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean autoIncoming = pref.getBoolean("auto_incoming", true);
		boolean autoOutgoing = pref.getBoolean("auto_outgoing", true);
		
		Log.i("BT.State", intent.getAction());
		if ( Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction()) && autoOutgoing ) {
			toggleBluetooth(true);
		}
		else {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			Log.i("BT.State", state == null ? "": state);
			if ( TelephonyManager.EXTRA_STATE_RINGING.equals(state) && autoIncoming){
				toggleBluetooth(true);
			}
			else if ( TelephonyManager.EXTRA_STATE_IDLE.equals(state) && (autoIncoming || autoOutgoing) ) {
				toggleBluetooth(false);
			}
//			else if ( TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state) ) { //PICK UP
//			}
		}
	}
	
	private void toggleBluetooth(boolean flag){
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null ) {
			Log.i("BT.Device", "Bluetooth device can not be found!");
			return;
		}
		if ( flag && !btAdapter.isEnabled() ){
			btAdapter.enable();
		}
		if ( !flag && btAdapter.isEnabled() ) {
			btAdapter.disable();
		}
	}	
	
	private void saveVolume(String key, Context ctx){
		final AudioManager manager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		int vol=manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putInt(key, vol).commit();
	}
}
