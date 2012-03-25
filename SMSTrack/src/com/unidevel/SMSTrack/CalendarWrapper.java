package com.unidevel.SMSTrack;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;

public class CalendarWrapper {
//    <string name="calendar_events_uri">content://calendar/events</string>
//        <string name="calendar_uri">content://calendar/calendars</string>
//        <string name="calendar_uri_froyo">content://com.android.calendar/calendars</string>
//        <string name="calendar_events_uri_froyo">content://com.android.calendar/events</string>
	private Uri CALENDAR_URI;
	private Uri CALENDAR_EVENTS_URI;
	Context context;
	public static interface QueryCalendarCallback {
		public void onCalendar(int id, String name);
	}
	
	public CalendarWrapper(Context context){
		 if (Integer.valueOf(Build.VERSION.SDK).intValue() < 8){
			 CALENDAR_URI = Uri.parse("content://calendar/events");
			 CALENDAR_EVENTS_URI = Uri.parse("content://calendar/calendars");			 
		 }
		 else {
			 CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
			 CALENDAR_EVENTS_URI = Uri.parse("content://com.android.calendar/events");
		 }
		 this.context = context;
	}
	
	public void queryCalender(QueryCalendarCallback callback){
		String[] cols = new String[]{"_id","name"};
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(CALENDAR_URI, cols, null, null, null);
		if ( cursor.moveToFirst() ) {
			int idx_id = cursor.getColumnIndex("_id");
			int idx_name = cursor.getColumnIndex("name");
			do {
				int id = cursor.getInt(idx_id);
				String name = cursor.getString(idx_name);
				callback.onCalendar(id, name);
			}
			while(cursor.moveToNext());
		}
		cursor.close();
	}
	
	public void addSMSEvent(String calendar_id, String address, String body, long timestamp){
		try {
			
		    ContentResolver resolver = context.getContentResolver();
		    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
		    Cursor cursor = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		    String from = "";
		    if ( cursor.moveToFirst() ){
		    	int idx_name = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		    	from = cursor.getString(idx_name);
		    }
		    else {
		    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		    	if ( prefs.getBoolean("keyFromContact", false) ) {
		    		return;
		    	}
		    }
		    if ( from == null ) from = "";
		    cursor.close();
			ContentValues value = new ContentValues();
			String title_format = context.getString(R.string.title_format);
			if ( from != null && from.length() > 0 ) {
				address = from +"<"+address+">";
			}
			value.put("calendar_id", calendar_id);
			value.put("title", String.format(title_format, address));
			StringBuffer buf = new StringBuffer();
			buf.append(String.format(context.getString(R.string.from_format), address));
			buf.append("\n");
			buf.append(String.format(context.getString(R.string.body_format), body));
			buf.append("\n");
			value.put("description", buf.toString());
		    value.put("dtstart", timestamp);
		    value.put("dtend", timestamp);
		    value.put("hasExtendedProperties", Boolean.FALSE);
		    resolver.insert(CALENDAR_EVENTS_URI, value);
		}
		catch(Throwable ex){
			Toast.makeText(context, ex.getMessage(), 3).show();
			Log.e("SMSTrack", "addSMSEvent", ex);
		}
	}
}
