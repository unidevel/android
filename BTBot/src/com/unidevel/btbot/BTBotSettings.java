package com.unidevel.btbot;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class BTBotSettings extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
        
        /*
		Preference pref = findPreference("keyMoreApp");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:x2derr"));
				try { 
					startActivity(intent); 
				}
				catch(Throwable ex){
					Log.e("BTBot", ex.getMessage(),ex);
				}
				return true;
			}
		});
		*/
		AdView adView = new AdView(this, AdSize.BANNER, "a14f44967d5395d");
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.addView(adView);
		AdRequest req  = new AdRequest();
		adView.loadAd(req);
    }
}