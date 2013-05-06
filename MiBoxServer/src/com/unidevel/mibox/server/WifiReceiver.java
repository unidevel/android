package com.unidevel.mibox.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import com.unidevel.mibox.data.Constants;

public class WifiReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive( Context context, Intent intent )
	{
		ConnectivityManager conMan = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo netInfo = conMan.getActiveNetworkInfo();
		if (netInfo!=null&& netInfo.getType() == ConnectivityManager.TYPE_WIFI )
		{
			if ( netInfo.getState() == State.CONNECTED )
			{
				Intent serviceIntent = new Intent( Constants.SERVICE_NAME );
				context.startService( serviceIntent );
			}
			else
			{
				Intent serviceIntent = new Intent( Constants.SERVICE_NAME );
				context.stopService( serviceIntent );
			}
		}
	}

}
