package com.unidevel.tools.locker;

import android.content.*;
import android.preference.*;

public class BootReceiver extends BroadcastReceiver
{

	public void onReceive(Context ctx, Intent i)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		if(pref.getBoolean("boot",true)){
			MainActivity.showNotify(ctx);
		}
	}
	
}
