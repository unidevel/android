package com.unidevel.mibox.server.handler;

import android.content.Context;
import com.unidevel.mibox.data.ListAppRequest;
import com.unidevel.mibox.data.ListAppResponse;

public class ListAppHandler extends MiBoxRequestHandler<ListAppRequest, ListAppResponse>
{

	@Override
	public ListAppResponse handleRequest( Context context, ListAppRequest request )
	{
		ListAppResponse response = new ListAppResponse();
		findApps( context, response.apps );
		return response;
	}
}
