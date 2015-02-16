package com.unidevel.andevtools.log;

import android.util.Log;

import com.unidevel.andevtools.model.ILog;

public class AndroidLog implements ILog {
	public static String TAG = "AnDevTools";
	@Override
	public void trace(String fmt, Object... args) {
		String msg = String.format(fmt, args);		
		Log.v(TAG, msg);
	}

	@Override
	public void warn(String fmt, Object... args) {
		String msg = String.format(fmt, args);		
		Log.w(TAG, msg);		
	}

	@Override
	public void error(String fmt, Object... args) {
		String msg = String.format(fmt, args);		
		Log.e(TAG, msg);
	}

	@Override
	public void info(String fmt, Object... args) {
		String msg = String.format(fmt, args);		
		Log.i(TAG, msg);
	}

	@Override
	public void error(Throwable e) {
		Log.e(TAG, e.getMessage(), e);
	}

}
