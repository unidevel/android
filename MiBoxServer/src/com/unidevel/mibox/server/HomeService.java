package com.unidevel.mibox.server;

import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import com.unidevel.mibox.data.Constants;

public class HomeService extends Service
{
	public static final String SERVICE_ACTION = "com.unidevel.mibox.server.START_SERVER"; //$NON-NLS-1$

	MiBoxServer server;
	WifiManager.MulticastLock socketLock;

	@Override
	public IBinder onBind( Intent arg0 )
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		try
		{
			this.server = new MiBoxServer( this, Constants.SERVICE_PORT );
			this.server.start();
			Log.i( "Service.start", "service started" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (IOException e)
		{
			Log.e( "HomeService.onCreate", e.getMessage(), e ); //$NON-NLS-1$
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.server.stop();
		if ( this.socketLock != null )
			this.socketLock.release();
	}
}
