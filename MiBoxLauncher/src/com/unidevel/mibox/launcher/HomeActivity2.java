package com.unidevel.mibox.launcher;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.provider.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.view.GestureDetector.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.ads.*;
import com.unidevel.mibox.data.*;
import com.unidevel.mibox.launcher.client.*;
import com.unidevel.mibox.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.jmdns.*;

import android.text.format.Formatter;
import android.view.View.OnClickListener;

public class HomeActivity2 extends Activity implements ServiceListener {
	GridView appView;
	AppAdapter appAdapter;
	MiBoxClient client;
	MiBoxRemoter remoter;
	Button devices;

	WifiManager.MulticastLock socketLock;
	JmDNS jmdns;
	ServiceList serviceList;

	View homeView;
	View menuView;

	MenuAdapter menuAdapter;

	class ServiceList {
		LinkedHashMap<String, ServiceInfo> services = new LinkedHashMap<String, ServiceInfo>();

		public void setServices(ServiceInfo[] services) {
			for (ServiceInfo s : services) {
				this.services.put(s.getName(), s);
			}
		}

		public void addService(ServiceInfo s) {
			this.services.put(s.getName(), s);
		}

		public String[] getServiceNames() {
			ArrayList<String> names = new ArrayList<String>(
					this.services.size());
			names.addAll(this.services.keySet());
			return names.toArray(new String[0]);
		}

		public ServiceInfo getService(int pos) {
			Set<String> names = this.services.keySet();
			Iterator<String> it = names.iterator();
			for (int i = 0; it.hasNext(); ++i) {
				String name = it.next();
				if (i == pos) {
					return this.services.get(name);
				}
			}
			return null;
		}
	}

	class LoadIconTask extends AsyncTask<Void, Void, Boolean> {
		Context ctx;
		File cacheDir;
		byte[] buf;

		protected void onPreExecute() {
			super.onPreExecute();
			this.ctx = HomeActivity2.this;
			this.cacheDir = this.ctx.getDir("cache", Context.MODE_PRIVATE); //$NON-NLS-1$
			this.buf = new byte[8192];
		}

		byte[] load(File f) throws FileNotFoundException, IOException {
			FileInputStream in = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len;
			while ((len = in.read(this.buf)) > 0) {
				out.write(this.buf, 0, len);
			}
			in.close();
			return out.toByteArray();
		}

		void save(File f, byte[] d) throws IOException {
			FileOutputStream out = new FileOutputStream(f);
			out.write(d);
			out.close();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			List<AppInfo> apps = HomeActivity2.this.appAdapter.getApps();
			if ( apps == null )
			{
				return false;
			}
			for (AppInfo app : apps) {
				String packageName = app.packageName;
				String className = app.name;
				File iconFile = new File(this.cacheDir, packageName
						+ "_" + className); //$NON-NLS-1$
				try {
					if (iconFile.exists()) {
						try {
							byte[] data = load(iconFile);
							app.icon = BitmapUtil.toDrawable(data);
							this.publishProgress();
							continue;
						} catch (Exception e) {

						}
					}
					GetAppIconResponse response = HomeActivity2.this.client
							.getIcon(packageName, className);
					Drawable icon = BitmapUtil.toDrawable(response.data);
					if (icon != null) {
						app.icon = icon;
						this.publishProgress();
					}
					try {
						save(iconFile, response.data);
					} catch (Exception e) {

					}
				} catch (Exception e) {
					Log.e("loadIcon", e.getMessage(), e); //$NON-NLS-1$
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			HomeActivity2.this.appAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			HomeActivity2.this.appAdapter.notifyDataSetChanged();
		}
	}

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>> {
		List<AppInfo> apps;

		@Override
		protected void onPostExecute(List<AppInfo> result) {
			super.onPostExecute(result);
			HomeActivity2.this.appAdapter = new AppAdapter(HomeActivity2.this,
					this.apps);
			HomeActivity2.this.appView
					.setAdapter(HomeActivity2.this.appAdapter);

			new LoadIconTask().execute();
		}

		@Override
		protected List<AppInfo> doInBackground(Void... params) {
			try {
				ListAppResponse response = HomeActivity2.this.client.listApps();
				ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
				for (BasicAppInfo info : response.apps) {
					AppInfo app = new AppInfo();
					app.packageName = info.packageName;
					app.name = info.className;
					app.label = info.label;
					apps.add(app);
				}
				this.apps = apps;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	class StartAppTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String packageName = params[0];
			String className = params[1];
			try {
				StartAppResponse response = HomeActivity2.this.client.startApp(
						packageName, className);
				return !response.failed;
			} catch (Exception e) {
				Log.e("startApp", e.getMessage(), e); //$NON-NLS-1$
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	class InstallApkTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			String path = params[0];
			try {
				HomeActivity2.this.client.installApp(path);
			} catch (Exception e) {
				Log.e("installApk", e.getMessage(), e); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	class KeyTask extends AsyncTask<Integer, Void, Void> {
		protected Void doInBackground(Integer... keys) {
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
			if (remoter.isConnected()) {
				remoter.sendKeyCode(keys[0]);
			}
			return null;
		}
	}

	class RefreshDeviceTask extends AsyncTask<Void, Void, Void> {
		ServiceInfo[] services;
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(HomeActivity2.this, "",
					getString(R.string.prog_finding_device));

			if (HomeActivity2.this.socketLock == null) {
				WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				HomeActivity2.this.socketLock = wm
						.createMulticastLock(Constants.SERVICE_NAME);
				HomeActivity2.this.socketLock.acquire();
			}
		}

		protected Void doInBackground(Void... params) {
			try {
				if (HomeActivity2.this.jmdns == null) {
					WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					@SuppressWarnings("deprecation")
					String ip = Formatter.formatIpAddress(wm
							.getConnectionInfo().getIpAddress());
					InetAddress localInetAddress = InetAddress.getByName(ip);
					HomeActivity2.this.jmdns = JmDNS.create(localInetAddress);
					HomeActivity2.this.jmdns.addServiceListener(
							Constants.JMDNS_TYPE, HomeActivity2.this);
				}
				this.services = HomeActivity2.this.jmdns
						.list(Constants.JMDNS_TYPE);
			} catch (IOException ex) {
				Log.e("resolveService", ex.getMessage(), ex); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			this.dialog.dismiss();
			HomeActivity2.this.serviceList.setServices(this.services);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					HomeActivity2.this);
			if (this.services == null || this.services.length == 0) {
				builder.setMessage(R.string.msg_no_device);
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								devices.setText(getString(R.string.label_no_device));
							}
						});
			} else {
				builder.setTitle(R.string.title_select_device);
				builder.setItems(
						HomeActivity2.this.serviceList.getServiceNames(),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								connect(which);
								dialog.dismiss();
							}
						});
			}
			builder.create().show();
		}
	}

	class UninstallTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				HomeActivity2.this.client.uninstallApp(params[0]);
			} catch (Exception e) {
				Log.e("Uninstall", e.getMessage(), e); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	class ConnectToClientTask extends AsyncTask<Object, Void, Exception> {
		String host;
		int port;
		ProgressDialog dialog;

		@Override
		protected Exception doInBackground(Object... params) {
			HomeActivity2.this.client.disconnect();
			Exception exception = null;
			try {
				this.host = (String) params[0];
				this.port = (Integer) params[1];
				HomeActivity2.this.client.connect(this.host,
						Constants.SERVICE_PORT);
			} catch (Exception ex) {
				exception = ex;
			}
			try {
				HomeActivity2.this.remoter.connect(this.host, this.port);
			} catch (Exception ex) {
				exception = ex;
			}
			return exception;
		}

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(HomeActivity2.this,
					"", getString(R.string.prog_connecting)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		protected void onPostExecute(Exception e) {
			super.onPostExecute(e);
			this.dialog.dismiss();
			if (e != null) {
				Toast.makeText(HomeActivity2.this,
						getString(R.string.msg_not_connect, this.host),
						Toast.LENGTH_LONG).show(); //$NON-NLS-1$ //$NON-NLS-2$
				Log.e("Client.connect", e.getMessage(), e); //$NON-NLS-1$
			} else {
				new LoadAppTask().execute();
			}
		}
	}

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Toast.makeText(SelectFilterActivity.this, "Left Swipe",
					// Toast.LENGTH_SHORT).show();
					showSideMenu(true);
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Toast.makeText(SelectFilterActivity.this, "Right Swipe",
					// Toast.LENGTH_SHORT).show();
					showSideMenu(false);
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

	}

	GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	private void startMiBoxActivity(String className) {
		String pkgName = "com.duokan.phone.remotecontroller";
		Intent intent = new Intent();
		if (className == null) {
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setPackage(pkgName);
		} else {
			if (className.indexOf('.') == 0) {
				className = pkgName + className;
			}
			intent.setClassName(pkgName, className);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivityForResult(intent, 0);
		} catch (Throwable ex) {
			Log.e("startMiBoxActivity", ex.getMessage(), ex);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		this.homeView = findViewById(R.id.home_layout);
		this.menuView = findViewById(R.id.menu_layout);

		GridView menuGrid = (GridView) findViewById(R.id.menuGrid);
		this.menuAdapter = new MenuAdapter(this);
		menuGrid.setAdapter(this.menuAdapter);
		menuGrid.setFocusable(true);
		menuGrid.setFocusableInTouchMode(true);
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long id) {
				int menuId = menuAdapter.getId(pos);
				switch (menuId) {
				case MenuAdapter.ID_REMOTER:
					startMiBoxRemoter();
					break;
				case MenuAdapter.ID_REFRESH:
					if(client.isConnected())
					{
						new LoadAppTask().execute();
					}
					else{
						showDeviceList();
					}
					break;
				case MenuAdapter.ID_HOME: {
						if(client.isConnected()){
							String packageName = "com.unidevel.mibox.server"; //$NON-NLS-1$ 
							String className = "com.unidevel.mibox.server.HomeActivity"; //$NON-NLS-1$ 
							new StartAppTask().execute(packageName, className);
						}else{
							showDeviceList();
						}
					}
					break;
				case MenuAdapter.ID_ABOUT:{
					Intent intent=new Intent(HomeActivity2.this, AboutActivity.class);
					startActivity(intent);
				}

					break;
				// case MenuAdapter.ID_SEARCH:
				// startMiBoxActivity(".SearchActivity");
				// break;
				// case MenuAdapter.ID_FC:
				// startMiBoxActivity(".HomeActivity");
				// break;
				// case MenuAdapter.ID_PAD:
				// startMiBoxActivity(".ChannelActivity");
				// break;
				}
				showSideMenu(false);
			}
		});

		// this.tv = (TextView)findViewById(R.id.trace);
		this.appView = (GridView) this.findViewById(R.id.gridview);
		this.appView.setKeepScreenOn(true);
		this.appView.setFocusable(true);
		this.appView.setFocusableInTouchMode(true);
		this.appView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long id) {
				AppInfo info = HomeActivity2.this.appAdapter.getApp(pos);
				String packageName = info.packageName;
				String className = info.name;
				new StartAppTask().execute(packageName, className);
			}
		});
		registerForContextMenu(this.appView);

		this.client = new MiBoxClient();
		this.remoter = new MiBoxRemoter();

		this.devices = (Button) findViewById(R.id.devices);
		this.devices.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDeviceList();
			}

		});
		this.serviceList = new ServiceList();

		int[] buttonIds = new int[] { R.id.btnUp, R.id.btnDown, R.id.btnLeft,
				R.id.btnRight, R.id.btnBack, R.id.btnHome, R.id.btnMenu,
				R.id.btnEnter };
		int[] keys = new int[] { MiBoxRemoter.KEY_CODE_UP,
				MiBoxRemoter.KEY_CODE_DOWN, MiBoxRemoter.KEY_CODE_LEFT,
				MiBoxRemoter.KEY_CODE_RIGHT, MiBoxRemoter.KEY_CODE_BACK,
				MiBoxRemoter.KEY_CODE_HOME, MiBoxRemoter.KEY_CODE_MENU,
				MiBoxRemoter.KEY_CODE_OK };

		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		OnClickListener buttonListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!client.isConnected()){
					showDeviceList();
				}
				else{
					int code = (Integer) v.getTag();
					new KeyTask().execute(code);
				}
			}
		};
		for (int i = 0; i < buttonIds.length; ++i) {
			int buttonId = buttonIds[i];
			int key = keys[i];
			View button = findViewById(buttonId);
			button.setTag(key);
			button.setOnClickListener(buttonListener);
			button.setOnTouchListener(gestureListener);
		}

		View btnUpload = findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!client.isConnected()){
					showDeviceList();
				}
				else{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("file/*"); //$NON-NLS-1$
					startActivityForResult(intent, GET_PATH);
				}
			}
		});
		/*
		View btnRefresh = findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new LoadAppTask().execute();
			}
		});

		View btnRemoter = findViewById(R.id.btnRemomter);
		btnRemoter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMiBoxRemoter();
			}
		});

		View btnClose = findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HomeActivity2.this.finish();
			}
		});
		*/
		View btnMenu = findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMenu = !showMenu;
				showSideMenu(showMenu);
			}
		});

		/*
		 * View btnHome = findViewById( R.id.btnHome );
		 * btnHome.setOnClickListener( new OnClickListener() {
		 * 
		 * @Override public void onClick( View v ) { String packageName =
		 * "com.unidevel.mibox.server"; //$NON-NLS-1$ String className =
		 * "com.unidevel.mibox.server.HomeActivity"; //$NON-NLS-1$ new
		 * StartAppTask().execute( packageName, className ); } } );
		 */

		AdView adView = new AdView(this, AdSize.BANNER, "a1518d142014149"); //$NON-NLS-1$
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);

		this.appView.setOnTouchListener(gestureListener);

		showDeviceList();
	}

	class SlideAnimation extends Animation {
		View viewLeft;
		View viewRight;
		int endOffset;
		boolean expanded;

		public SlideAnimation(View viewLeft, View viewRight, int endOffset,
				boolean expanded) {
			super();
			this.viewLeft = viewLeft;
			this.viewRight = viewRight;
			this.endOffset = endOffset;
			this.expanded = expanded;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			int newOffset;
			if (expanded) {
				newOffset = 0;
				newOffset = (int) (endOffset * (1 - interpolatedTime));
			} else {
				newOffset = (int) (endOffset * (interpolatedTime));
			}
			viewLeft.scrollTo(newOffset, 0);
			viewRight.scrollTo(newOffset - endOffset, 0);
		}
	}

	boolean showMenu = false;

	public void showSideMenu(boolean show) {
		showMenu = show;
		View view = findViewById(R.id.menu_view);
		SlideAnimation anim = new SlideAnimation(this.homeView, this.menuView,
				view.getWidth(), !show);
		anim.setDuration(75);
		this.menuView.setVisibility(View.VISIBLE);
		this.homeView.startAnimation(anim);
	}

	protected void showDeviceList() {
		if(isWifiConnected())
		{
			new RefreshDeviceTask().execute();
		}
		else
		{
			showConnectWifiDialog();
		}
	}
	
	protected void showConnectWifiDialog(){
		AlertDialog.Builder b=new AlertDialog.Builder(this);
		b.setMessage(R.string.msg_open_wifi);
		b.setPositiveButton(R.string.label_setting, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int p2)
				{
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(intent);
					dialog.dismiss();
				}
			});
		b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int p2)
				{
					dialog.dismiss();
				}
			});
		b.create().show();
	}

	void startMiBoxRemoter() {
		String pkg = "com.duokan.phone.remotecontroller"; //$NON-NLS-1$
		try {
			Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
			startActivity(intent);
		} catch (Throwable ex) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + pkg)); //$NON-NLS-1$
				startActivity(intent);
			} catch (Throwable ex2) {
				Log.e("OpenMarket", ex2.getMessage(), ex2); //$NON-NLS-1$
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// MenuInflater inflater = new MenuInflater( this );
		// inflater.inflate( R.menu.main_menu, menu );
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == this.appView.getId()) {
			MenuInflater inflater = new MenuInflater(this);
			inflater.inflate(R.menu.app_menu, menu);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			AppInfo app = this.appAdapter.getApp(info.position);
			menu.setHeaderTitle(app.label);
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (R.id.install == item.getItemId()) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*"); //$NON-NLS-1$
			startActivityForResult(intent, this.GET_PATH);
		} else if (R.id.file == item.getItemId()) {

		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.uninstall) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			AppInfo app = this.appAdapter.getApp(info.position);
			new UninstallTask().execute(app.packageName);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	int GET_PATH = 1234;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == this.GET_PATH) {
			if (RESULT_OK == resultCode && data != null) {
				String path = data.getData().getPath();
				InstallApkTask task = new InstallApkTask();
				task.execute(path);
			}
		}
	}
	
	public boolean isWifiConnected(){
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if(wm.getWifiState()!=WifiManager.WIFI_STATE_ENABLED){
			return false;
		}
		WifiInfo info=wm.getConnectionInfo();
		if(info==null||info.getNetworkId()<0||info.getIpAddress()==0){
			return false;
		}
		Log.i("wifi","ip: "+info.getIpAddress()+", netid:"+info.getNetworkId()+", SSID:"+info.getSSID());
		return true;
	}

	@SuppressWarnings("deprecation")
	public ServiceInfo[] resolveService() {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		this.socketLock = wm.createMulticastLock(Constants.SERVICE_NAME);
		this.socketLock.acquire();
		try {
			String ip = Formatter.formatIpAddress(wm.getConnectionInfo()
					.getIpAddress());
			InetAddress localInetAddress = InetAddress.getByName(ip);
			this.jmdns = JmDNS.create(localInetAddress);
			this.jmdns.addServiceListener(Constants.JMDNS_TYPE, this);
			ServiceInfo[] services = this.jmdns.list(Constants.JMDNS_TYPE);

			return services;
		} catch (IOException ex) {
			Log.e("resolveService", ex.getMessage(), ex); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public void serviceAdded(ServiceEvent event) {
		Log.i("serviceAdded:", "added:" + event.getName() + ", port:" + event.getInfo().getPort() + ",address:" + event.getInfo().getHostAddresses()[0]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	@Override
	public void serviceRemoved(ServiceEvent event) {
		Log.i("serviceRemoved:", "removed:" + event.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void serviceResolved(ServiceEvent event) {
		Log.i("serviceResolved:", "resolved:" + event.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.socketLock != null) {
			this.socketLock.release();
			this.socketLock = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (showMenu) {
				showSideMenu(false);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void connect(int pos) {
		ServiceInfo service = this.serviceList.getService(pos);
		String serviceName;
		if (service != null) {
			new ConnectToClientTask().execute(service.getHostAddresses()[0],
					service.getPort());
			serviceName = service.getName();
		} else {
			serviceName = getString(R.string.label_no_device);
		}
		this.devices.setText(serviceName);
	}
}
