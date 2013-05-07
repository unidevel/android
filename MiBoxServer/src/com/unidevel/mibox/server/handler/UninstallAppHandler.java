package com.unidevel.mibox.server.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.unidevel.mibox.data.UninstallAppRequest;
import com.unidevel.mibox.data.UninstallAppResponse;

public class UninstallAppHandler extends MiBoxRequestHandler<UninstallAppRequest,UninstallAppResponse>
{

	public UninstallAppResponse handleRequest(Context context, UninstallAppRequest request)
	{
		String pkg = request.packageName;
		Uri pkgUri = Uri.parse( "package:" + pkg ); //$NON-NLS-1$
		Intent i = new Intent(Intent.ACTION_DELETE,pkgUri);
		context.startActivity(i);
		return new UninstallAppResponse();
	}

}
