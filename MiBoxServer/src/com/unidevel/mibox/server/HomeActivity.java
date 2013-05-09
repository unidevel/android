
package com.unidevel.mibox.server;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import java.util.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class HomeActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.home );

		Intent i = new Intent(HomeService.SERVICE_ACTION);
		startService(i);
	}

	public List<AppInfo> findActivity( Uri uri, String type )
	{
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = this.getPackageManager();
		Intent intent = new Intent( Intent.ACTION_MAIN );
		
		if ( "file".equalsIgnoreCase( uri.getScheme() ) ) //$NON-NLS-1$
		{
			intent.setDataAndType( uri, type );
		}
		else
		{
			intent.setData( uri );
		}
		List<ResolveInfo> rList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> acts = pm.queryIntentActivities( intent, 0 );
		rList.addAll( acts );
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		String selectedPkg = pref.getString( "package", "" ); //$NON-NLS-1$ //$NON-NLS-2$
		String selectedName = pref.getString( "class", "" ); //$NON-NLS-1$ //$NON-NLS-2$
		if ( "file".equals( uri.getScheme() ) ) //$NON-NLS-1$
		{
			intent = new Intent();
			intent.setClassName( "com.android.chrome", //$NON-NLS-1$
					"com.google.android.apps.chrome.Main" ); //$NON-NLS-1$
			acts = pm.queryIntentActivities( intent, 0 );
			if ( acts != null && acts.size() > 0 )
				rList.addAll( acts );

			intent = new Intent();
			intent.setClassName( "com.chrome.beta", //$NON-NLS-1$
					"com.google.android.apps.chrome.Main" ); //$NON-NLS-1$
			acts = pm.queryIntentActivities( intent, 0 );
			if ( acts != null && acts.size() > 0 )
				rList.addAll( acts );
		}

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
