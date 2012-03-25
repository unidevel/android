package com.unidevel.SMSTrack;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SMSTrackReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if ( !ACTION.equals(intent.getAction())) return;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean isEnable = prefs.getBoolean("keyEnableSMS", true);
		if ( !isEnable ) return;
		String calendar_id = prefs.getString("keyCalendarEntry", "1");
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		if ( bundle != null ){
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			String address = "";
			String body = "";
			long timestamp = Calendar.getInstance().getTimeInMillis();
			for ( int i = 0; i< msgs.length; ++i ){
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				address = msgs[i].getOriginatingAddress();
				body += msgs[i].getMessageBody().toString();
				timestamp = msgs[i].getTimestampMillis();
			}
			
			CalendarWrapper wrapper = new CalendarWrapper(context);
			wrapper.addSMSEvent(calendar_id, address, body, timestamp);
		}
	}
}
