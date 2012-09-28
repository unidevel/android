package com.unidevel.tools.locker;

import android.app.*;
import android.content.*;
import android.os.*;
import com.unidevel.tools.locker.*;

public class LockActivity extends Activity
{
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		Intent i = new Intent(this, ActionActivity.class);
		i.putExtra("action", ActionActivity.ACTION_LOCK);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		this.finish();
	}
}
