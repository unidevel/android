package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.PreferenceManager;

import com.unidevel.tools.locker.*;

public class LockActivity extends Activity
{
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("unlock",true)&&MainActivity.checkUnlocker(this)){
			MainActivity.startUnlocker(this);
		}
		ActionActivity.lock(this);
		finish();
	}
}
