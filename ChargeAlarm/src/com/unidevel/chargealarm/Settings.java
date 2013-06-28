package com.unidevel.chargealarm;

import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.util.*;

public class Settings extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{

	public boolean onPreferenceChange(Preference p, Object val)
	{
		if (p == rp)
		{
			Log.i("alarm", "" + val);
			//p.setSummary("url:" + val);
			p.setSummary(getRingtoneTitle(""+val));
		}
		return true;
	}
	
	RingtonePreference rp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//	setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.preferences);
		rp = (RingtonePreference) this.findPreference("alarm_url");
		rp.setOnPreferenceChangeListener(this);
		//	RingtonePreference rp;
		
		Intent i=new Intent(this,ChargeStatService.class);
		startService(i);
		//	PreferenceCategory cat = (PreferenceCategory)this.getPreferenceScreen().getPreference(1);
		//	int[] volumnNames = new int[]{R.string.type_call, R.string.type_ring, R.string.type_music};
		//	int[] volumeTypes = new int[]{AudioManager.STREAM_VOICE_CALL, AudioManager.STREAM_RING, AudioManager.STREAM_MUSIC};
	}
	
	public String getRingtoneTitle(String uri){
		Uri ringtoneUri = Uri.parse(uri);
		Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
		String name;
		if(ringtone!=null){
			name = ringtone.getTitle(this);
		}
		else{
			name="";
		}
		int pos1=name.indexOf('.');
		if(pos1>0)
			name=name.substring(0,pos1);
		pos1=name.indexOf('_');
		if(pos1>0)
			name=name.substring(0,pos1);
		return name;
	}
}
