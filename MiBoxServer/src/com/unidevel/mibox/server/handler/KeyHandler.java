package com.unidevel.mibox.server.handler;

import android.content.Context;
import android.util.Log;
import com.unidevel.mibox.data.KeyRequest;
import com.unidevel.mibox.data.KeyResponse;

public class KeyHandler extends MiBoxRequestHandler<KeyRequest,KeyResponse>
{

	public KeyResponse handleRequest(Context context, KeyRequest req)
	{
		KeyResponse res = new KeyResponse();
		String cmd = "sendevent /dev/input/event" + req.index + " 1 " + req.key + " "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		run(cmd+1);
		run(cmd+0);
		return res;
	}
	
	public static void run(String cmd) {
		java.lang.Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();
		} catch (Exception ex) {
			Log.e( "KeyHandler.run", ex.getMessage(), ex ); //$NON-NLS-1$
		}
	}

	
}
