package com.unidevel.mibox.server.handler;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import com.unidevel.mibox.data.GetAppIconRequest;
import com.unidevel.mibox.data.GetFileRequest;
import com.unidevel.mibox.data.InstallAppRequest;
import com.unidevel.mibox.data.KeyRequest;
import com.unidevel.mibox.data.ListAppRequest;
import com.unidevel.mibox.data.MiBoxRequest;
import com.unidevel.mibox.data.MiBoxResponse;
import com.unidevel.mibox.data.SendFileRequest;
import com.unidevel.mibox.data.StartAppRequest;
import com.unidevel.mibox.data.UninstallAppRequest;

@SuppressWarnings ("rawtypes")
public class MiBoxRequestHandlerManager
{
	Map<Class, MiBoxRequestHandler> handlers;
	Context context;

	public MiBoxRequestHandlerManager( Context context )
	{
		this.handlers = new HashMap<Class, MiBoxRequestHandler>();
		this.context = context;
		registerHandlers();
	}

	private void registerHandlers()
	{
		this.handlers.put( ListAppRequest.class, new ListAppHandler() );
		this.handlers.put( GetAppIconRequest.class, new GetAppIconHandler() );
		this.handlers.put( StartAppRequest.class, new StartAppHandler() );
		this.handlers.put( SendFileRequest.class, new SendFileHandler() );
		this.handlers.put( InstallAppRequest.class, new InstallAppHandler() );
		this.handlers.put( GetFileRequest.class, new GetFileHandler() );
		this.handlers.put( UninstallAppRequest.class, new UninstallAppHandler() );
		this.handlers.put( KeyRequest.class, new KeyHandler() );

	}

	@SuppressWarnings ("unchecked")
	public MiBoxResponse handleRequest( MiBoxRequest request )
	{
		MiBoxRequestHandler<MiBoxRequest, MiBoxResponse> handler = this.handlers.get( request.getClass() );
		if ( handler != null )
		{
			return handler.handleRequest( this.context, request );
		}
		return null;
	}
}
