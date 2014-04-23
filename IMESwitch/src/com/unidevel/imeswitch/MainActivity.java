package com.unidevel.imeswitch;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.inputmethod.*;
import android.content.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
		Intent intent=this.getIntent();
		boolean show=intent.getBooleanExtra("show",false);
		if (show){
			this.showIMEDialog();			
		}
		else {
			this.showNotify();
		}
		this.finish();
    }
	
	public void showIMEDialog(){
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showInputMethodPicker();
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
}
