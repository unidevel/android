package com.unidevel.mibox.server.handler;

import android.content.*;
import android.util.*;
import com.unidevel.mibox.data.*;

public class KeyHandler extends MiBoxRequestHandler<KeyRequest,KeyResponse>
{

	public KeyResponse handleRequest(Context context, KeyRequest req)
	{
		KeyResponse res = new KeyResponse();
		String cmd="sendevent /dev/input/event"+req.index+" 1 "+req.key+" ";
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
			Log.e("KeyHandler.run", ex.getMessage(), ex);
		}
	}

	
}
