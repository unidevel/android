package com.unidevel.mibox.launcher;
import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.net.wifi.*;
import android.os.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.unidevel.mibox.data.*;
import com.unidevel.mibox.launcher.client.*;
import com.unidevel.mibox.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.jmdns.*;

import android.text.format.Formatter;
import android.view.View.OnClickListener;

public class HomeActivity2 extends Activity implements ServiceListener, OnItemSelectedListener
{
	GridView appView;
	AppAdapter appAdapter;
	MiBoxClient client;
	Spinner devices;

	WifiManager.MulticastLock socketLock;
	JmDNS jmdns;
	ServiceList sl;
	
	class ServiceList
	{
		LinkedHashMap<String, ServiceInfo> services = new LinkedHashMap<String, ServiceInfo>();
		
		public void setServices(ServiceInfo[] services){
			for(ServiceInfo s:services){
				this.services.put(s.getName(),s);
			}
			invalidate();
		}
		
		public void addService(ServiceInfo s){
			this.services.put(s.getName(),s);
			invalidate();
		}

		private void invalidate()
		{
			ArrayList<String> names=new ArrayList<String>(services.size());
			names.addAll(services.keySet());
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity2.this, android.R.layout.simple_dropdown_item_1line, names);//.toArray(new String[0]));
			devices.setAdapter(adapter);
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
			ctx=HomeActivity2.this;
			cacheDir=ctx.getDir("cache",Context.MODE_PRIVATE);
			buf=new byte[8192];
		}
		
		byte[] load(File f) throws FileNotFoundException, IOException{
			FileInputStream in=new FileInputStream(f);
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			int len;
			while((len=in.read(buf))>0){
				out.write(buf,0,len);
			}
			in.close();
			return out.toByteArray();
		}
		
		void save(File f, byte[] d) throws IOException{
			FileOutputStream out = new FileOutputStream(f);
			out.write(d);
			out.close();
		}
		
		@Override
		protected Boolean doInBackground( Void... params )
		{
			List<AppInfo> apps = appAdapter.getApps();
			for ( AppInfo app : apps )
			{
				String packageName = app.packageName;
				String className = app.name;
				File iconFile=new File(cacheDir, packageName+"_"+className);
				try
				{
					if(iconFile.exists()){
						try{
							byte[] data=load(iconFile);
							app.icon =  BitmapUtil.toDrawable( data );
							this.publishProgress();
							continue;
						}
						catch(Exception e){
							
						}
					}
					GetAppIconResponse response = client.getIcon( packageName, className );
					Drawable icon = BitmapUtil.toDrawable( response.data );
					if ( icon != null )
					{
						app.icon = icon;
						this.publishProgress();
					}
					try{
						save(iconFile, response.data);
					}
					catch(Exception e){
						
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

	class RefreshDeviceTask extends AsyncTask<Void, Void, Void>
	{
		ServiceInfo[] services;
		@Override
		protected Void doInBackground( Void... params )
		{
			services = jmdns.list( Constants.JMDNS_TYPE );
			return null;
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute( result );
			for ( ServiceInfo service : services )
			{
				
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
				AppInfo info = appAdapter.getApp( pos );
				String packageName = info.packageName;
				String className = info.name;
				new StartAppTask().execute( packageName, className );
			}
		} );

		this.devices = (Spinner)findViewById( R.id.devices );
		this.devices.setOnItemSelectedListener( this );
		View button = findViewById( R.id.refresh_devices );
		button.setOnClickListener( new OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				new RefreshDeviceTask().execute();
			}

		} );
		new ResolveServiceTask().execute();
		this.sl=new ServiceList();
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
			String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
			InetAddress localInetAddress = InetAddress.getByName( ip );
			jmdns = JmDNS.create(localInetAddress);// , InetAddress.getByName(
									// localInetAddress.getHostName()
									// ).toString() );
			jmdns.addServiceListener( Constants.JMDNS_TYPE, this );
			ServiceInfo[] services = jmdns.list( Constants.JMDNS_TYPE );
			
			//System.err.println( services.length );
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
		Log.i( "serviceAdded:", "added:" + event.getName() +", port:"+event.getInfo().getPort()+",address:"+event.getInfo().getHostAddresses()[ 0 ]);
		if ( Constants.SERVICE_NAME.equals( event.getName() ) )
		{
			this.socketLock.release();
			this.socketLock = null;
			String address = event.getInfo().getHostAddresses()[ 0 ];
			Log.i("added","address:"+address);
			int port = event.getInfo().getPort();
			this.client = new MiBoxClient( address, 3456 );
			new LoadAppTask().execute();
		}
		
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

	@Override
	public void onItemSelected( AdapterView<?> adapterView, View view, int position, long id )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected( AdapterView<?> adapterView )
	{
		// TODO Auto-generated method stub

	}
}
