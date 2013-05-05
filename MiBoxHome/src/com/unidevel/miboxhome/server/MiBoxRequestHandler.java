package com.unidevel.miboxhome.server;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.unidevel.miboxhome.data.BasicAppInfo;
import com.unidevel.miboxhome.data.MiBoxRequest;
import com.unidevel.miboxhome.data.MiBoxResponse;

public abstract class MiBoxRequestHandler<ReqType extends MiBoxRequest, ResType extends MiBoxResponse>
{
	public abstract ResType handleRequest( Context context, ReqType request );

	public void findApps( Context context, List<BasicAppInfo> apps )
	{
		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent( Intent.ACTION_MAIN, null );
		intent.addCategory( Intent.CATEGORY_LAUNCHER );
		List<ResolveInfo> rList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> acts = pm.queryIntentActivities( intent, 0 );
		rList.addAll( acts );

		for ( ResolveInfo r : rList )
		{
			if ( context.getPackageName().equals( r.activityInfo.packageName ) )
				continue;
			BasicAppInfo app = new BasicAppInfo();
			apps.add( app );
			app.packageName = r.activityInfo.packageName;
			app.className = r.activityInfo.name;
			CharSequence label = r.activityInfo.loadLabel( pm );
			if ( label == null )
			{
				app.label = ""; //$NON-NLS-1$
			}
			else
			{
				app.label = label.toString();
			}
		}
	}

	public ResolveInfo findApp( Context context, String packageName, String className )
	{
		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent();
		intent.setClassName( packageName, className );
		List<ResolveInfo> acts = pm.queryIntentActivities( intent, 0 );
		if ( acts == null || acts.size() == 0 )
		{
			return null;
		}
		return acts.get( 0 );
	}
}
