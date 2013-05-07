package com.unidevel.mibox.server.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.unidevel.mibox.data.InstallAppRequest;
import com.unidevel.mibox.data.InstallAppResponse;

public class InstallAppHandler extends MiBoxRequestHandler<InstallAppRequest, InstallAppResponse>
{

	@Override
	public InstallAppResponse handleRequest( Context context, InstallAppRequest request )
	{
		InstallAppResponse result = new InstallAppResponse();
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( android.content.Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.parse( "file://" + request.remotePath ), "application/vnd.android.package-archive" ); //$NON-NLS-1$ //$NON-NLS-2$
		context.startActivity( intent );

		return result;
	}

}
