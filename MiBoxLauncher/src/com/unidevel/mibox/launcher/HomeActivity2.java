
package com.unidevel.mibox.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import com.unidevel.mibox.data.BasicAppInfo;
import com.unidevel.mibox.data.Constants;
import com.unidevel.mibox.data.GetAppIconResponse;
import com.unidevel.mibox.data.ListAppResponse;
import com.unidevel.mibox.data.StartAppResponse;
import com.unidevel.mibox.launcher.client.MiBoxClient;
import com.unidevel.mibox.launcher.client.MiBoxRemoter;
import com.unidevel.mibox.util.BitmapUtil;

public class HomeActivity2 extends Activity implements ServiceListener
{
	GridView appView;
	AppAdapter appAdapter;
	MiBoxClient client;
	MiBoxRemoter remoter;
	Button devices;

	WifiManager.MulticastLock socketLock;
	JmDNS jmdns;
	ServiceList serviceList;

	class ServiceList
	{
		LinkedHashMap<String, ServiceInfo> services = new LinkedHashMap<String, ServiceInfo>();

		public void setServices( ServiceInfo[] services )
		{
			for ( ServiceInfo s : services )
			{
				this.services.put( s.getName(), s );
			}
		}

		public void addService( ServiceInfo s )
		{
			this.services.put( s.getName(), s );
		}

		public String[] getServiceNames()
		{
			ArrayList<String> names = new ArrayList<String>( this.services.size() );
			names.addAll( this.services.keySet() );
			return names.toArray( new String[ 0 ] );
		}

		public ServiceInfo getService( int pos )
		{
			Set<String> names = this.services.keySet();
			Iterator<String> it = names.iterator();
			for ( int i = 0; it.hasNext(); ++i )
			{
				String name = it.next();
				if ( i == pos )
				{
					return this.services.get( name );
				}
			}
			return null;
		}
	}

	class LoadIconTask extends AsyncTask<Void, Void, Boolean>
	{
		Context ctx;
		File cacheDir;
		byte[] buf;

		protected void onPreExecute()
		{
			super.onPreExecute();
			this.ctx = HomeActivity2.this;
			this.cacheDir = this.ctx.getDir( "cache", Context.MODE_PRIVATE ); //$NON-NLS-1$
			this.buf = new byte[ 8192 ];
		}

		byte[] load( File f ) throws FileNotFoundException, IOException
		{
			FileInputStream in = new FileInputStream( f );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len;
			while ( (len = in.read( this.buf )) > 0 )
			{
				out.write( this.buf, 0, len );
			}
			in.close();
			return out.toByteArray();
		}

		void save( File f, byte[] d ) throws IOException
		{
			FileOutputStream out = new FileOutputStream( f );
			out.write( d );
			out.close();
		}

		@Override
		protected Boolean doInBackground( Void... params )
		{
			List<AppInfo> apps = HomeActivity2.this.appAdapter.getApps();
			for ( AppInfo app : apps )
			{
				String packageName = app.packageName;
				String className = app.name;
				File iconFile = new File( this.cacheDir, packageName + "_" + className ); //$NON-NLS-1$
				try
				{
					if ( iconFile.exists() )
					{
						try
						{
							byte[] data = load( iconFile );
							app.icon = BitmapUtil.toDrawable( data );
							this.publishProgress();
							continue;
						}
						catch (Exception e)
						{

						}
					}
					GetAppIconResponse response = HomeActivity2.this.client.getIcon( packageName, className );
					Drawable icon = BitmapUtil.toDrawable( response.data );
					if ( icon != null )
					{
						app.icon = icon;
						this.publishProgress();
					}
					try
					{
						save( iconFile, response.data );
					}
					catch (Exception e)
					{

					}
				}
				catch (Exception e)
				{
					Log.e( "loadIcon", e.getMessage(), e ); //$NON-NLS-1$
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate( Void... values )
		{
			super.onProgressUpdate( values );
			HomeActivity2.this.appAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute( Boolean result )
		{
			super.onPostExecute( result );
			HomeActivity2.this.appAdapter.notifyDataSetChanged();
		}
	}

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>>
	{
		List<AppInfo> apps;

		@Override
		protected void onPostExecute( List<AppInfo> result )
		{
			super.onPostExecute( result );
			HomeActivity2.this.appAdapter = new AppAdapter( HomeActivity2.this, this.apps );
			HomeActivity2.this.appView.setAdapter( HomeActivity2.this.appAdapter );

			new LoadIconTask().execute();
		}

		@Override
		protected List<AppInfo> doInBackground( Void... params )
		{
			try
			{
				ListAppResponse response = HomeActivity2.this.client.listApps();
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
				StartAppResponse response = HomeActivity2.this.client.startApp( packageName, className );
				return !response.failed;
			}
			catch (Exception e)
			{
				Log.e( "startApp", e.getMessage(), e ); //$NON-NLS-1$
			}
			return false;
		}

		@Override
		protected void onPostExecute( Boolean result )
		{
			super.onPostExecute( result );
			startMiBoxRemoter();
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
				HomeActivity2.this.client.installApp( path );
			}
			catch (Exception e)
			{
				Log.e( "installApk", e.getMessage(), e ); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			startMiBoxRemoter();
		}
	}

	class KeyTask extends AsyncTask<Integer, Void, Void>
	{
		protected Void doInBackground( Integer... keys )
		{
			// KeyRequest req=new KeyRequest();
			// req.index=0;
			// for(int key:keys){
			// req.key=key;
			// }
			// try
			// {
			// HomeActivity2.this.client.sendRecv( req );
			// }
			// catch (IOException e)
			// {}
			// catch (ClassNotFoundException e)
			// {}
			if ( remoter.isConnected() )
			{
				remoter.sendKeyCode( keys[ 0 ] );
			}
			return null;
		}
	}

	class RefreshDeviceTask extends AsyncTask<Void, Void, Void>
	{
		ServiceInfo[] services;
		ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			this.dialog = ProgressDialog.show( HomeActivity2.this, "", "Finding" ); //$NON-NLS-1$ //$NON-NLS-2$

			if ( HomeActivity2.this.socketLock == null )
			{
				WifiManager wm = (WifiManager)getSystemService( Context.WIFI_SERVICE );
				HomeActivity2.this.socketLock = wm.createMulticastLock( Constants.SERVICE_NAME );
				HomeActivity2.this.socketLock.acquire();
			}
		}

		protected Void doInBackground( Void... params )
		{
			try
			{
				if ( HomeActivity2.this.jmdns == null )
				{
					WifiManager wm = (WifiManager)getSystemService( Context.WIFI_SERVICE );
					@SuppressWarnings ("deprecation")
					String ip = Formatter.formatIpAddress( wm.getConnectionInfo().getIpAddress() );
					InetAddress localInetAddress = InetAddress.getByName( ip );
					HomeActivity2.this.jmdns = JmDNS.create( localInetAddress );
					HomeActivity2.this.jmdns.addServiceListener( Constants.JMDNS_TYPE, HomeActivity2.this );
				}
				this.services = HomeActivity2.this.jmdns.list( Constants.JMDNS_TYPE );
			}
			catch (IOException ex)
			{
				Log.e( "resolveService", ex.getMessage(), ex ); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			this.dialog.dismiss();
			HomeActivity2.this.serviceList.setServices( this.services );

			AlertDialog.Builder builder = new AlertDialog.Builder( HomeActivity2.this );
			builder.setTitle( "Select device" ); //$NON-NLS-1$
			builder.setItems( HomeActivity2.this.serviceList.getServiceNames(), new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick( DialogInterface dialog, int which )
				{
					connect( which );
					dialog.dismiss();
				}
			} );
			builder.create().show();
		}
	}

	class UninstallTask extends AsyncTask<String, Void, Void>
	{

		@Override
		protected Void doInBackground( String... params )
		{
			try
			{
				HomeActivity2.this.client.uninstallApp( params[ 0 ] );
			}
			catch (Exception e)
			{
				Log.e( "Uninstall", e.getMessage(), e ); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			startMiBoxRemoter();
		}
	}

	class ConnectToClientTask extends AsyncTask<Object, Void, Exception>
	{
		String host;
		int port;
		ProgressDialog dialog;

		@Override
		protected Exception doInBackground( Object... params )
		{
			HomeActivity2.this.client.disconnect();
			Exception exception = null;
			try
			{
				this.host = (String)params[ 0 ];
				this.port = (Integer)params[ 1 ];
				HomeActivity2.this.client.connect( this.host, Constants.SERVICE_PORT );
			}
			catch (Exception ex)
			{
				exception = ex;
			}
			try
			{
				HomeActivity2.this.remoter.connect( this.host, this.port );
			}
			catch (Exception ex)
			{
				exception = ex;
			}
			return exception;
		}

		@Override
		protected void onPreExecute()
		{
			this.dialog = ProgressDialog.show( HomeActivity2.this, "", "Connecting" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		protected void onPostExecute( Exception e )
		{
			super.onPostExecute( e );
			this.dialog.dismiss();
			if ( e != null )
			{
				Toast.makeText( HomeActivity2.this, "Connect to " + this.host + " failed!", Toast.LENGTH_LONG ).show(); //$NON-NLS-1$ //$NON-NLS-2$
				Log.e( "Client.connect", e.getMessage(), e ); //$NON-NLS-1$
			}
			else
			{
				new LoadAppTask().execute();
			}
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
				AppInfo info = HomeActivity2.this.appAdapter.getApp( pos );
				String packageName = info.packageName;
				String className = info.name;
				new StartAppTask().execute( packageName, className );
			}
		} );
		registerForContextMenu( this.appView );

		this.client = new MiBoxClient();
		this.remoter = new MiBoxRemoter();

		this.devices = (Button)findViewById( R.id.devices );
		this.devices.setOnClickListener( new OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				showDeviceList();
			}

		} );
		this.serviceList = new ServiceList();

		int[] buttonIds = new int[] {
				R.id.btnUp, R.id.btnDown, R.id.btnLeft, R.id.btnRight, R.id.btnBack,
				// R.id.btnHome,
				R.id.btnMenu
		};
		int[] keys =
				new int[] {
						MiBoxRemoter.KEY_CODE_UP, MiBoxRemoter.KEY_CODE_DOWN, MiBoxRemoter.KEY_CODE_LEFT,
						MiBoxRemoter.KEY_CODE_RIGHT, MiBoxRemoter.KEY_CODE_BACK,
						// MiBoxRemoter.KEY_CODE_HOME,
						MiBoxRemoter.KEY_CODE_MENU
				};
		OnClickListener buttonListener = new OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				int code = (Integer)v.getTag();
				new KeyTask().execute( code );
			}
		};
		for ( int i = 0; i < buttonIds.length; ++i )
		{
			int buttonId = buttonIds[ i ];
			int key = keys[ i ];
			View button = findViewById( buttonId );
			button.setTag( key );
			button.setOnClickListener( buttonListener );
		}

		View btnHome = findViewById( R.id.btnHome );
		btnHome.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				String packageName = "com.unidevel.mibox.server"; //$NON-NLS-1$
				String className = "com.unidevel.mibox.server.HomeActivity"; //$NON-NLS-1$
				new StartAppTask().execute( packageName, className );
			}
		} );

		showDeviceList();
	}

	protected void showDeviceList()
	{
		new RefreshDeviceTask().execute();
	}

	void startMiBoxRemoter()
	{
		String pkg = "com.duokan.phone.remotecontroller"; //$NON-NLS-1$
		try
		{
			Intent intent = getPackageManager().getLaunchIntentForPackage( pkg );
			startActivity( intent );
		}
		catch (Throwable ex)
		{
			try
			{
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setData( Uri.parse( "market://details?id=" + pkg ) ); //$NON-NLS-1$
				startActivity( intent );
			}
			catch (Throwable ex2)
			{
				Log.e( "OpenMarket", ex2.getMessage(), ex2 ); //$NON-NLS-1$
			}
		}
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
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		if ( v.getId() == this.appView.getId() )
		{
			MenuInflater inflater = new MenuInflater( this );
			inflater.inflate( R.menu.app_menu, menu );
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
			AppInfo app = this.appAdapter.getApp( info.position );
			menu.setHeaderTitle( app.name );
		}
	}

	@Override
	public boolean onMenuItemSelected( int featureId, MenuItem item )
	{
		if ( R.id.install == item.getItemId() )
		{
			Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
			intent.setType( "file/*" ); //$NON-NLS-1$
			startActivityForResult( intent, this.GET_PATH );
		}
		else if ( R.id.file == item.getItemId() )
		{

		}
		return super.onMenuItemSelected( featureId, item );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		if ( item.getItemId() == R.id.uninstall )
		{
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			AppInfo app = this.appAdapter.getApp( info.position );
			new UninstallTask().execute( app.packageName );
			return true;
		}
		return super.onContextItemSelected( item );
	}

	int GET_PATH = 1234;

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		if ( requestCode == this.GET_PATH )
		{
			if ( RESULT_OK == resultCode && data != null )
			{
				String path = data.getData().getPath();
				InstallApkTask task = new InstallApkTask();
				task.execute( path );
			}
		}
	}

	@SuppressWarnings ("deprecation")
	public ServiceInfo[] resolveService()
	{
		WifiManager wm = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		this.socketLock = wm.createMulticastLock( Constants.SERVICE_NAME );
		this.socketLock.acquire();
		try
		{
			String ip = Formatter.formatIpAddress( wm.getConnectionInfo().getIpAddress() );
			InetAddress localInetAddress = InetAddress.getByName( ip );
			this.jmdns = JmDNS.create( localInetAddress );
			this.jmdns.addServiceListener( Constants.JMDNS_TYPE, this );
			ServiceInfo[] services = this.jmdns.list( Constants.JMDNS_TYPE );

			return services;
		}
		catch (IOException ex)
		{
			Log.e( "resolveService", ex.getMessage(), ex ); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public void serviceAdded( ServiceEvent event )
	{
		Log.i( "serviceAdded:", "added:" + event.getName() + ", port:" + event.getInfo().getPort() + ",address:" + event.getInfo().getHostAddresses()[ 0 ] ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	@Override
	public void serviceRemoved( ServiceEvent event )
	{
		Log.i( "serviceRemoved:", "removed:" + event.getName() ); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void serviceResolved( ServiceEvent event )
	{
		Log.i( "serviceResolved:", "resolved:" + event.getName() ); //$NON-NLS-1$ //$NON-NLS-2$
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

	protected void connect( int pos )
	{
		ServiceInfo service = this.serviceList.getService( pos );
		String serviceName;
		if ( service != null )
		{
			new ConnectToClientTask().execute( service.getHostAddresses()[ 0 ], service.getPort() );
			serviceName = service.getName();
		}
		else
		{
			serviceName = ""; //$NON-NLS-1$
		}
		this.devices.setText( serviceName );
	}
}
