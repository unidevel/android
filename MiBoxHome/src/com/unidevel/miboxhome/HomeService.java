package com.unidevel.miboxhome;

import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.unidevel.miboxhome.data.Constants;
import com.unidevel.miboxhome.server.MiBoxServer;

public class HomeService extends Service
{
	MiBoxServer server;
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
	}
}
