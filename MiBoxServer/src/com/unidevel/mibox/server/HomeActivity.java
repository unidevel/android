
package com.unidevel.mibox.server;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class HomeActivity extends Activity
{
	AppAdapter appAdapter;
	GridView appView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.home );

		Intent i = new Intent( HomeService.SERVICE_ACTION );
		startService( i );

		this.appView = (GridView)this.findViewById( R.id.gridview );
		this.appView.setFocusable(true);
		this.appView.setFocusableInTouchMode(true);
		this.appView.setOnItemClickListener( new OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int pos, long id )
			{
				HomeActivity.this.appAdapter.setSelected( pos );
				savePref();
				AppInfo app = HomeActivity.this.appAdapter.getSelectedApp();
				try
				{
					Intent intent = new Intent();
					intent.setClassName( app.packageName, app.name );
					intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity( intent );
				}
				catch (Exception ex)
				{
					Log.e( "startApp", ex.getMessage(), ex ); //$NON-NLS-1$
				}
			}
		} );
		new LoadAppTask().execute();
	}

	protected void savePref()
	{
		if ( this.appAdapter == null )
			return;
		AppInfo app = this.appAdapter.getSelectedApp();
		if ( app == null )
			return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		pref.edit().putString( "package", app.packageName ).putString( "class", app.name ).commit(); //$NON-NLS-1$ //$NON-NLS-2$
	}

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

			apps = findActivity();
			return apps;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute( List<AppInfo> result )
		{
			super.onPostExecute( result );
			HomeActivity.this.appAdapter = new AppAdapter( HomeActivity.this, result );
			HomeActivity.this.appView.setAdapter( HomeActivity.this.appAdapter );
			int sel = HomeActivity.this.appAdapter.getSelected();
//			appView.getI
//			if ( sel >= 0 )
//			{
//				HomeActivity.this.appView.scrollTo(0, 0);
//				HomeActivity.this.appView.smoothScrollToPosition( sel );
//				HomeActivity.this.appView.setItemChecked(sel, true);
//			}
		}
	}

	public List<AppInfo> findActivity()
	{
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = this.getPackageManager();
		Intent intent = new Intent( Intent.ACTION_MAIN );
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
