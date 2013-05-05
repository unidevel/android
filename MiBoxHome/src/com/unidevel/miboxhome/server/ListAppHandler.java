package com.unidevel.miboxhome.server;

import android.content.Context;
import com.unidevel.miboxhome.data.ListAppRequest;
import com.unidevel.miboxhome.data.ListAppResponse;

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
