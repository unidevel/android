package com.unidevel.mibox.launcher;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.unidevel.mibox.data.BasicAppInfo;
import com.unidevel.mibox.data.Constants;
import com.unidevel.mibox.data.GetAppIconResponse;
import com.unidevel.mibox.data.ListAppResponse;
import com.unidevel.mibox.data.StartAppResponse;
import com.unidevel.mibox.launcher.client.MiBoxClient;
import com.unidevel.mibox.util.BitmapUtil;

public class HomeActivity2 extends Activity implements ServiceListener
{
	GridView appView;
	AppAdapter appAdapter;
	MiBoxClient client;

	WifiManager.MulticastLock socketLock;
	JmDNS jmdns;
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

	class InstallApkTask extends AsyncTask<String, Void, Void>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground( String... params )
		{
			String path = params[ 0 ];
			try
			{
				client.installApp( path );
			}
			catch (Exception e)
			{
				Log.e( "installApk", e.getMessage(), e );
			}
			return null;
		}

	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.home );
		// this.tv = (TextView)findViewById(R.id.trace);
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

		new ResolveServiceTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		super.onCreateOptionsMenu( menu );
		MenuInflater inflater = new MenuInflater( this );
		inflater.inflate( R.menu.main_menu, menu );
		return true;
	}

	@Override
	public boolean onMenuItemSelected( int featureId, MenuItem item )
	{
		if ( R.id.install == item.getItemId() )
		{
			Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
			intent.setType( "file/*" );
			startActivityForResult( intent, GET_PATH );
		}
		else if ( R.id.file == item.getItemId() )
		{

		}
		return super.onMenuItemSelected( featureId, item );
	}

	int GET_PATH = 1234;

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		if ( requestCode == GET_PATH )
		{
			if ( RESULT_OK == resultCode && data != null )
			{
				String path = data.getData().getPath();
				InstallApkTask task = new InstallApkTask();
				task.execute( path );
			}
		}
	}

	public void resolveService()
	{
		WifiManager wm = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		socketLock = wm.createMulticastLock( Constants.SERVICE_NAME );
		socketLock.acquire();
		try
		{
			/*
			int i = ((WifiManager)getSystemService( "wifi" )).getConnectionInfo().getIpAddress();
			byte[] arrayOfByte = new byte[ 4 ];
			arrayOfByte[ 0 ] = (byte)(i & 0xFF);
			arrayOfByte[ 1 ] = (byte)(0xFF & i >> 8);
			arrayOfByte[ 2 ] = (byte)(0xFF & i >> 16);
			arrayOfByte[ 3 ] = (byte)(0xFF & i >> 24);
			Log.i("resolveService","ip:"+(255+(int)arrayOfByte[0])+"."+(255+(int)arrayOfByte[1])+"."+arrayOfByte[2]+"."+arrayOfByte[3]);
			InetAddress localInetAddress = InetAddress.getByAddress( arrayOfByte );
			JmDNS jmdns =
					JmDNS.create( localInetAddress, InetAddress.getByName( localInetAddress.getHostName() ).toString() );
			*/
			String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
			InetAddress localInetAddress = InetAddress.getByName( ip );
			jmdns = JmDNS.create();// , InetAddress.getByName(
									// localInetAddress.getHostName()
									// ).toString() );
			jmdns.addServiceListener( Constants.JMDNS_TYPE, this );
			// ServiceInfo[] services = jmdns.list( Constants.JMDNS_TYPE );
			// System.err.println( services.length );
			return;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			Log.e("resolveService",ex.getMessage(),ex);
			this.socketLock.release();
			this.socketLock = null;
		}
	}

	@Override
	public void serviceAdded( ServiceEvent event )
	{
		// TODO Auto-generated method stub
		Log.i( "serviceAdded:", "added:" + event.getName() );
		
	}

	@Override
	public void serviceRemoved( ServiceEvent event )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serviceResolved( ServiceEvent event )
	{
		Log.i( "serviceResolved:", "resolved:" + event.getName() );
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
