package com.unidevel.btbot;

import android.bluetooth.BluetoothAdapter;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

public class BTBotSettings extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceCategory cat = (PreferenceCategory)this.getPreferenceScreen().getPreference(1);
        int[] volumnNames = new int[]{R.string.type_call, R.string.type_ring, R.string.type_music};
        int[] volumeTypes = new int[]{AudioManager.STREAM_VOICE_CALL, AudioManager.STREAM_RING, AudioManager.STREAM_MUSIC};
    	final AudioManager manager = (AudioManager)this.getSystemService(AUDIO_SERVICE);
                
        for ( int i =0; i < volumnNames.length; ++i ){
        	int name = volumnNames[i];
        	final int type = volumeTypes[i];
        	final SeekBarPreference pref = new SeekBarPreference(this);
        	cat.addPreference(pref);
        	pref.setTitle(name);
        	int max = manager.getStreamMaxVolume(type);
        	int val = manager.getStreamVolume(type);
        	pref.setProgress(val);
        	pref.setMax(max);
        	pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object obj) {
					manager.setStreamVolume(type, pref.getProgress(), 0);
					return false;
				}
			});
        }
//        setPreferenceScreen(preferenceScreen)
//        setContentView(R.layout.main);
//        
//        ToggleButton toggleBT = (ToggleButton)findViewById(R.id.toggleBT);
//        toggleBT.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				toggleBluetooth(isChecked);
//			}
//		});
    }
    
	private void toggleBluetooth(boolean flag){
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if ( flag && !btAdapter.isEnabled() ){
			btAdapter.enable();
		}
		if ( !flag && btAdapter.isEnabled() ) {
			btAdapter.disable();
		}
	}	

}