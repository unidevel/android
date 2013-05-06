package com.unidevel.mibox.launcher;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.net.wifi.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.unidevel.mibox.data.*;
import com.unidevel.mibox.launcher.client.*;
import com.unidevel.mibox.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.jmdns.*;

public class HomeActivity2 extends Activity implements ServiceListener
{
	GridView appView;
	AppAdapter appAdapter;
	MiBoxClient client;
	WifiManager.MulticastLock socketLock;

	class LoadIconTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground( Void... params )
		{
			List<AppInfo> apps = appAdapter.getApps();
			for ( AppInfo app : apps )
			{
				String packageName = app.packageName;
				String className = app.name;
				try
				{
					GetAppIconResponse response = client.getIcon( packageName, className );
					Drawable icon = BitmapUtil.toDrawable( response.data );
					if ( icon != null )
					{
						app.icon = icon;
						this.publishProgress();
					}
				}
				catch (Exception e)
				{
					Log.e( "loadIcon", e.getMessage(), e );
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate( Void... values )
		{
			super.onProgressUpdate( values );
			appAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute( Boolean result )
		{
			super.onPostExecute( result );
			appAdapter.notifyDataSetChanged();
		}
	}

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>>
	{
		List<AppInfo> apps;
		@Override
		protected void onPostExecute( List<AppInfo> result )
		{
			super.onPostExecute( result );
			appAdapter = new AppAdapter( HomeActivity2.this, apps );
			appView.setAdapter( appAdapter );
			
			new LoadIconTask().execute();
		}

		@Override
		protected List<AppInfo> doInBackground( Void... params )
		{
			try
			{
				client.connect();
				ListAppResponse response = client.listApps();
				ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
				for ( BasicAppInfo info : response.apps )
				{
					AppInfo app = new AppInfo();
					app.packageName = info.packageName;
					app.name = info.className;
					app.label = info.label;
					apps.add( app );
				}
				this.apps = apps;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}

	class StartAppTask extends AsyncTask<String, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground( String... params )
		{
			String packageName = params[ 0 ];
			String className = params[ 1 ];
			try
			{
				StartAppResponse response = client.startApp( packageName, className );
				return !response.failed;
			}
			catch (Exception e)
			{
				Log.e( "startApp", e.getMessage(), e );
			}
			return false;
		}

	}

	class ResolveServiceTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground( Void... params )
		{
			resolveService();
			return null;
		}
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.home );

		this.appView = (GridView)this.findViewById( R.id.gridview );
		this.appView.setKeepScreenOn( true );
		this.appView.setFocusable( true );
		this.appView.setFocusableInTouchMode( true );
		this.appView.setOnItemClickListener( new OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int pos, long id )
			{
				AppInfo info = appAdapter.getApp( pos );
				String packageName = info.packageName;
				String className = info.name;
				new StartAppTask().execute( packageName, className );
			}
		} );

		//Intent intent = new Intent( MiBoxServer.SERVICE_ACTION );
		//startService( intent );

		new ResolveServiceTask().execute();
	}

	public void resolveService()
	{
		WifiManager wm = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		socketLock = wm.createMulticastLock( Constants.SERVICE_NAME );
		socketLock.acquire();
		try
		{
			int i = ((WifiManager)getSystemService( "wifi" )).getConnectionInfo().getIpAddress();
			byte[] arrayOfByte = new byte[ 4 ];
			arrayOfByte[ 0 ] = (byte)(i & 0xFF);
			arrayOfByte[ 1 ] = (byte)(0xFF & i >> 8);
			arrayOfByte[ 2 ] = (byte)(0xFF & i >> 16);
			arrayOfByte[ 3 ] = (byte)(0xFF & i >> 24);
			InetAddress localInetAddress = InetAddress.getByAddress( arrayOfByte );
			JmDNS jmdns =
					JmDNS.create( localInetAddress, InetAddress.getByName( localInetAddress.getHostName() ).toString() );

			jmdns.addServiceListener( Constants.JMDNS_TYPE, this );
			return;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			this.socketLock.release();
			this.socketLock = null;
		}
	}

	@Override
	public void serviceAdded( ServiceEvent event )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serviceRemoved( ServiceEvent event )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serviceResolved( ServiceEvent event )
	{
		if ( Constants.SERVICE_NAME.equals( event.getName() ) )
		{
			this.socketLock.release();
			this.socketLock = null;
			String address = event.getInfo().getHostAddresses()[ 0 ];
			int port = event.getInfo().getPort();
			this.client = new MiBoxClient( address, port );
			new LoadAppTask().execute();

		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if ( this.socketLock != null )
		{
			this.socketLock.release();
			this.socketLock = null;
		}
	}
}
