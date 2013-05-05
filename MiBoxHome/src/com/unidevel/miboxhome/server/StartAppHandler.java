package com.unidevel.miboxhome.server;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.unidevel.miboxhome.data.StartAppRequest;
import com.unidevel.miboxhome.data.StartAppResponse;

public class StartAppHandler extends MiBoxRequestHandler<StartAppRequest, StartAppResponse>
{

	@Override
	public StartAppResponse handleRequest( Context context, StartAppRequest request )
	{
		StartAppResponse response = new StartAppResponse();
		ResolveInfo info = findApp( context, request.packageName, request.className );
		if ( info == null )
		{
			response.failed = true;
		}
		else
		{
			response.failed = false;
			Intent intent = new Intent();
			intent.setClassName( request.packageName, request.className );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			context.startActivity( intent );
		}
		return response;
	}

}
