package com.unidevel.mibox.server.handler;

import android.content.*;
import android.net.*;
import com.unidevel.mibox.data.*;

public class UninstallAppHandler extends MiBoxRequestHandler<UninstallAppRequest,UninstallAppResponse>
{

	public UninstallAppResponse handleRequest(Context context, UninstallAppRequest request)
	{
		String pkg = request.packageName;
		Uri pkgUri = Uri.parse("package:"+pkg);
		Intent i = new Intent(Intent.ACTION_DELETE,pkgUri);
		context.startActivity(i);
		return new UninstallAppResponse();
	}

}
