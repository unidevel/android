
package com.unidevel.miboxhome;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.unidevel.miboxhome.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class HomeActivity extends Activity implements ServiceListener
{
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	// private SystemUiHider mSystemUiHider;
	GridView appView;
	AppAdapter appAdapter;

	class LoadAppTask extends AsyncTask<Void, Integer, List<AppInfo>>
	{
		public void run()
		{
			List<AppInfo> apps = doInBackground();
			onPostExecute( apps );
		}

		@Override
		protected List<AppInfo> doInBackground( Void... params )
		{
			List<AppInfo> apps;
			apps = findApps();
			return apps;
		}

		@Override
		protected void onPostExecute( List<AppInfo> result )
		{
			super.onPostExecute( result );
			GridView listView = (GridView)findViewById( R.id.gridview );
			appAdapter = new AppAdapter( HomeActivity.this, result );
			listView.setAdapter( appAdapter );
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
				Intent intent = new Intent();
				intent.setClassName( packageName, className );
				startActivity( intent );
			}
		} );

		// final View controlsView = findViewById(
		// R.id.fullscreen_content_controls );
		// final View contentView = findViewById( R.id.fullscreen_content );

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		// mSystemUiHider = SystemUiHider.getInstance( this, contentView,
		// HIDER_FLAGS );
		/*
		 * mSystemUiHider.setup(); mSystemUiHider.setOnVisibilityChangeListener(
		 * new SystemUiHider.OnVisibilityChangeListener() { // Cached values.
		 * int mControlsHeight; int mShortAnimTime;
		 * 
		 * @Override
		 * 
		 * @TargetApi (Build.VERSION_CODES.HONEYCOMB_MR2) public void
		 * onVisibilityChange( boolean visible ) { if ( Build.VERSION.SDK_INT >=
		 * Build.VERSION_CODES.HONEYCOMB_MR2 ) { // If the ViewPropertyAnimator
		 * API is available // (Honeycomb MR2 and later), use it to animate the
		 * // in-layout UI controls at the bottom of the // screen. if (
		 * mControlsHeight == 0 ) { mControlsHeight = controlsView.getHeight();
		 * } if ( mShortAnimTime == 0 ) { mShortAnimTime =
		 * getResources().getInteger( android.R.integer.config_shortAnimTime );
		 * } controlsView.animate().translationY( visible ? 0 : mControlsHeight
		 * ).setDuration( mShortAnimTime ); } else { // If the
		 * ViewPropertyAnimator APIs aren't // available, simply show or hide
		 * the in-layout UI // controls. controlsView.setVisibility( visible ?
		 * View.VISIBLE : View.GONE ); }
		 * 
		 * if ( visible && AUTO_HIDE ) { // Schedule a hide(). delayedHide(
		 * AUTO_HIDE_DELAY_MILLIS ); } } } );
		 */
		// Set up the user interaction to manually show or hide the system UI.
		/*
		 * contentView.setOnClickListener( new View.OnClickListener() {
		 * 
		 * @Override public void onClick( View view ) { if ( TOGGLE_ON_CLICK ) {
		 * mSystemUiHider.toggle(); } else { mSystemUiHider.show(); } } } );
		 */

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		// findViewById( R.id.dummy_button ).setOnTouchListener(
		// mDelayHideTouchListener );

		new LoadAppTask().execute();
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		// delayedHide( 100 );
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	/*
	 * View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
	 * {
	 * 
	 * @Override public boolean onTouch( View view, MotionEvent motionEvent ) {
	 * if ( AUTO_HIDE ) { delayedHide( AUTO_HIDE_DELAY_MILLIS ); } return false;
	 * } };
	 * 
	 * Handler mHideHandler = new Handler(); Runnable mHideRunnable = new
	 * Runnable() {
	 * 
	 * @Override public void run() { mSystemUiHider.hide(); } };
	 */
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	/*
	 * private void delayedHide( int delayMillis ) {
	 * mHideHandler.removeCallbacks( mHideRunnable ); mHideHandler.postDelayed(
	 * mHideRunnable, delayMillis ); new Thread() { public void run() { test();
	 * } }.start(); }
	 */
	public void test()
	{
		WifiManager wm = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		WifiManager.MulticastLock socketLock = wm.createMulticastLock( "mydebuginfo" );
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
			String type = "_rc._tcp.local.";
			jmdns.addServiceListener( type, this );
			return;
		}
		catch (IOException localIOException)
		{
			while ( true )
				localIOException.printStackTrace();
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
		String address = event.getInfo().getHostAddresses()[ 0 ];
		int port = event.getInfo().getPort();
		String name = event.getInfo().getName();
		// this.this$0.connectMiBox( str1, i );
		Log.i( "resolved", "address:" + address + ",port:" + port + ",name:" + name );
	}

	public List<AppInfo> findApps()
	{
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = this.getPackageManager();
		Intent intent = new Intent( Intent.ACTION_MAIN, null );
		intent.addCategory( Intent.CATEGORY_LAUNCHER );
		List<ResolveInfo> rList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> acts = pm.queryIntentActivities( intent, 0 );
		rList.addAll( acts );
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		String selectedPkg = pref.getString( "package", "" ); //$NON-NLS-1$ //$NON-NLS-2$
		String selectedName = pref.getString( "class", "" ); //$NON-NLS-1$ //$NON-NLS-2$

		for ( ResolveInfo r : rList )
		{
			if ( this.getPackageName().equals( r.activityInfo.packageName ) )
				continue;
			AppInfo app = new AppInfo();
			apps.add( app );
			app.packageName = r.activityInfo.packageName;
			app.name = r.activityInfo.name;
			app.icon = r.activityInfo.loadIcon( pm );
			if ( app.icon == null )
			{
				app.icon = getResources().getDrawable( R.drawable.ic_launcher );
			}
			CharSequence label = r.activityInfo.loadLabel( pm );
			if ( label == null )
			{
				app.label = ""; //$NON-NLS-1$
			}
			else
			{
				app.label = label.toString();
			}
			if ( selectedPkg.equals( app.packageName ) )
			{
				if ( (app.name == null && selectedName.length() == 0) || selectedName.equals( app.name ) )
				{
					app.selected = true;
				}
			}
		}
		return apps;
	}
}
