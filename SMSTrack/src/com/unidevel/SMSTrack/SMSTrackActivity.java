package com.unidevel.SMSTrack;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.*;
import com.unidevel.SMSTrack.CalendarWrapper.QueryCalendarCallback;

public class SMSTrackActivity extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);
        this.addPreferencesFromResource(R.xml.prefs);
        try {
	        final ListPreference pref = (ListPreference)getPreferenceScreen().findPreference("keyCalendarEntry");
	        CalendarWrapper wrapper = new CalendarWrapper(this);
	        final List<String> ids = new ArrayList<String>();
	        final List<String> names = new ArrayList<String>();
	        wrapper.queryCalender(new QueryCalendarCallback() {
				public void onCalendar(int id, String name) {
					if(name==null)return;
					ids.add(String.valueOf(id));
					names.add(name);
				}
			});
	        pref.setEntries(names.toArray(new CharSequence[0]));
	        pref.setEntryValues(ids.toArray(new CharSequence[0]));
	        Log.e("names",names.toString());
	        Log.e("ids",names.toString());
	        String value = pref.getValue();
	        int position = ids.indexOf(value);
	        if ( position >= 0  ){
	        	pref.setSummary(names.get(position));
	        }
	        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object value) {
					int pos = ids.indexOf(value);
					pref.setSummary(names.get(pos));
					return true;
				}
			});
	        
			AdView adView = new AdView(this);
			adView.setAdUnitId("ca-app-pub-2348443469199344/2400394112");
			adView.setAdSize(AdSize.BANNER);
			LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
			layout.addView(adView);
			AdRequest req = new AdRequest.Builder().build();
			adView.loadAd(req);
        }
        catch(Throwable ex){
        	Toast.makeText(this, "Please install Google Calendar before run this program", Toast.LENGTH_LONG).show();
        	Log.e("SMSTrack", "onCreate", ex);
        	this.finish();
        }
    }
}
