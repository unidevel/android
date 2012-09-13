package com.unidevel.app;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

public class TicketBotSettings extends PreferenceActivity implements TicketBotConstants, OnPreferenceChangeListener {
	EditTextPreference prefName;
	EditTextPreference prefId;
	EditTextPreference prefPhone;
	ListPreference prefDate;
	ListPreference prefTickets;
	TicketBotUser user;
	
	boolean modified;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			user = TicketBotUser.load(this);
		}
		catch(Throwable ex){
			user = new TicketBotUser();
		}
		modified = false;

		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);
		PreferenceCategory userCat = new PreferenceCategory(this);
		userCat.setTitle(this.getString(R.string.cat_userinfo));
		root.addPreference(userCat);

		prefName = new EditTextPreference(this);
		prefName.setKey(KEY_NAME);
		prefName.setTitle(getString(R.string.label_name));
		prefName.setOnPreferenceChangeListener(this);
		prefName.setDialogTitle(getString(R.string.label_name));
		userCat.addPreference(prefName);

		prefId = new EditTextPreference(this);
		prefId.setKey(KEY_ID);
		prefId.setTitle(getString(R.string.label_id));
		prefId.setOnPreferenceChangeListener(this);
		prefId.setDialogTitle(getString(R.string.label_id));
		userCat.addPreference(prefId);
		
		prefPhone = new EditTextPreference(this);
		prefPhone.setKey(KEY_PHONE);
		prefPhone.setTitle(getString(R.string.label_phone));
		prefPhone.setOnPreferenceChangeListener(this);
		prefPhone.setDialogTitle(getString(R.string.label_phone));
		userCat.addPreference(prefPhone);

		PreferenceCategory ticketCat = new PreferenceCategory(this);
		ticketCat.setTitle(this.getString(R.string.cat_ticketinfo));
		root.addPreference(ticketCat);
		
		prefDate = new ListPreference(this);
		prefDate.setKey(KEY_DATE);
		prefDate.setTitle(getString(R.string.label_date));
		prefDate.setDialogTitle(getString(R.string.label_date));
		prefDate.setSummary(toDisplayDate(user.wantDate));
		prefDate.setEntries(new String[]{getString(R.string.desc_date_tomorrow), getString(R.string.desc_date_this_saturday), getString(R.string.desc_date_this_sunday)});
		prefDate.setEntryValues(new String[]{String.valueOf(DATE_TOMORROW), String.valueOf(DATE_THIS_SATURDAY), String.valueOf(DATE_THIS_SUNDAY)});
		prefDate.setOnPreferenceChangeListener(this);
		ticketCat.addPreference(prefDate);

		prefTickets = new ListPreference(this);
		prefTickets.setKey(KEY_TICKETS);
		prefTickets.setTitle(getString(R.string.label_tickets));
		prefTickets.setDialogTitle(getString(R.string.label_tickets));
		prefTickets.setSummary(toDisplayTickets(user.wantTickets));
		prefTickets.setEntries(new String[]{getString(R.string.desc_max_tickets), "1","2","3","4"});
		prefTickets.setEntryValues(new String[]{String.valueOf(MAX_TICKETS),"1","2","3","4"});
		prefTickets.setOnPreferenceChangeListener(this);
		ticketCat.addPreference(prefTickets);
		
		updateSummary();
		setPreferenceScreen(root);
	}

	public String toDisplayDate(int date){
		switch(date){
			case DATE_THIS_SATURDAY: return getString(R.string.desc_date_this_saturday);
			case DATE_THIS_SUNDAY: return getString(R.string.desc_date_this_sunday);
		}
		return getString(R.string.desc_date_tomorrow);
	}

	public String toDisplayTickets(int tickets){
		if ( tickets == 0 ) {
			return getString(R.string.desc_max_tickets);
		}
		else return String.valueOf(tickets);
	}
	

	public boolean onPreferenceChange(Preference pref, Object newValue) {
		modified = true;
		if ( pref == prefId ) {
			user.id = String.valueOf(newValue);
		}
		else if ( pref == prefName) {
			user.name = String.valueOf(newValue);
		}
		else if ( pref == prefPhone ) {
			user.phone = String.valueOf(newValue);
		}
		else if ( pref == prefDate ) {
			try {
				user.wantDate = Integer.valueOf(String.valueOf(newValue));
			}
			catch(Throwable ex){
				return false;
			}
		}
		else if ( pref == prefTickets ) {
			try {
				user.wantTickets = Integer.valueOf(String.valueOf(newValue));
			}
			catch(Throwable ex){
				return false;
			}
		}
		updateSummary();
		return true;
	}
	
	private void updateSummary(){
		prefName.setSummary(" "+user.name);
		prefName.setText(user.name);
		prefId.setSummary(" "+user.id);
		prefId.setText(user.id);
		prefPhone.setSummary(" "+user.phone);
		prefPhone.setText(user.phone);
		if ( user.wantDate < 0 ) user.wantDate = 0;
		prefDate.setSummary(" "+toDisplayDate(user.wantDate));
		prefDate.setValueIndex(user.wantDate);
		if ( user.wantTickets < 0 ) user.wantTickets = 0;
		prefTickets.setSummary(" "+toDisplayTickets(user.wantTickets));
		prefTickets.setValueIndex(user.wantTickets);
	}
	
	@Override
	protected void onStop() {
		savePref();
		super.onStop();
	}
	
	private void savePref(){
		try {
			if (user != null && modified){
				user.save(this);
				modified = false;
			}
		} catch (Throwable e) {
			Toast.makeText(this, getString(R.string.error_save_pref), 2).show();
		}
	}
}
