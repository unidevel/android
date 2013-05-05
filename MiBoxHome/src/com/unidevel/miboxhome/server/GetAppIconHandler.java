package com.unidevel.miboxhome.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import com.unidevel.miboxhome.data.GetAppIconRequest;
import com.unidevel.miboxhome.data.GetAppIconResponse;
import com.unidevel.miboxhome.util.BitmapUtil;

public class GetAppIconHandler extends MiBoxRequestHandler<GetAppIconRequest, GetAppIconResponse>
{

	@Override
	public GetAppIconResponse handleRequest( Context context, GetAppIconRequest request )
	{
		GetAppIconResponse response = new GetAppIconResponse();

		ResolveInfo info = findApp( context, request.packageName, request.className );
		if ( info == null )
		{
			response.failed = true;
		}
		else
		{
			PackageManager pm = context.getPackageManager();
			Drawable icon = info.activityInfo.loadIcon( pm );
			if ( icon != null )
			{
				response.data = BitmapUtil.toBytes( icon, 70 );
			}
		}
		return response;
	}

}
