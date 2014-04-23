package com.unidevel.imeswitch;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.inputmethod.*;
import android.content.*;
import android.provider.*;
import java.util.*;
import android.support.v4.app.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Intent intent=this.getIntent();
		boolean show=intent.getBooleanExtra("show",false);
		if (show){
			this.showIMEDialog();			
		}
		else
		{
			this.showNotify2();
			Toast.makeText(this, getString(R.string.message), Toast.LENGTH_LONG).show();
		}
		//this.finish();
    }
	
	public void showIMEDialog(){
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showInputMethodPicker();
	}

	public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
		this.showNotify2();
    }
	
	public String getIMEName(){
		String id = Settings.Secure.getString(
			getContentResolver(), 
			Settings.Secure.DEFAULT_INPUT_METHOD
		);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();

		final int N = mInputMethodProperties.size();

		for (int i = 0; i < N; i++) {

			InputMethodInfo imi = mInputMethodProperties.get(i);

			if (imi.getId().equals(id)) {

				//imi contains the information about the keyboard you are using
				PackageManager pm=(PackageManager)this.getPackageManager();
				try
				{
					PackageInfo ai=pm.getPackageInfo(imi.getPackageName(), PackageManager.GET_META_DATA);
					return pm.getApplicationLabel(ai.applicationInfo).toString();
					//ai.activities[0].
				}
				catch (PackageManager.NameNotFoundException e)
				{}
				break;
			}
		}
		return "Unknown IME";
	}
	
	public void showNotify(){
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
		Notification n = new Notification();  
		n.icon = R.drawable.ic_launcher;  
		n.tickerText = this.getString( R.string.ticket );
		n.flags = Notification.FLAG_NO_CLEAR;
		//n.defaults = Notification.DEFAULT_SOUND;  
		//n.defaults = Notification.DEFAULT_;  
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra( "show", true);
		PendingIntent pd = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
		n.setLatestEventInfo(this, n.tickerText, "", pd);
		nm.notify(0, n);
	}
	
	public void showNotify2(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra( "show", true);
		PendingIntent pd = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
		
		
		NotificationManager mNotificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Sets an ID for the notification, so it can be updated
		int notifyID = 1;
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
			.setContentTitle(this.getIMEName())
			.setContentText(this.getString(R.string.ticket))
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(pd);
		Notification n=mNotifyBuilder.build();
		n.flags = Notification.FLAG_NO_CLEAR;
		//numMessages = 0;
		// Start of a loop that processes data and then notifies the user
		//...
		//mNotifyBuilder.setContentText(currentText)
		//	.setNumber(++numMessages);
		// Because the ID remains unchanged, the existing notification is
		// updated.
		mNotificationManager.notify(
            notifyID,n);
		this.finish();
	}
}
