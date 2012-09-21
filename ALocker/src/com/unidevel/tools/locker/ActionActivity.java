package com.unidevel.tools.locker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.unidevel.util.DialogUtil;
import com.unidevel.util.RootUtil;

public class ActionActivity extends Activity {
	private DialogUtil dialogs;
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		dialogs = new DialogUtil(this);
		int actId = getIntent().getIntExtra("action", 0);
		switch (actId) {
		case 1:
			lock();
			break;
		case 2:
			shutdown();
			break;
		case 9:
			cancel(1);
			break;
		}
		finish();
	}

	private void shutdown() {
		String cmd = "sendevent /dev/input/event1 1 116 1\n"+
				"sendevent /dev/input/event1 0 0 0\n"+
				"sleep 1\n"+
				"sendevent /dev/input/event1 1 116 0\n"+
				"sendevent /dev/input/event1 0 0 0\n";
		RootUtil.run(cmd);
	}
	
	private void lock() {
		String cmd = "sendevent /dev/input/event1 1 116 1\n"+
				"sendevent /dev/input/event1 0 0 0\n"+
				"sendevent /dev/input/event1 1 116 0\n"+
				"sendevent /dev/input/event1 0 0 0\n";
		RootUtil.run(cmd);
	}

	public void cancel(int id) {
		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(id);
	}

	public void lock2() {
		DevicePolicyManager dpm = null;
		dpm = (DevicePolicyManager) this
				.getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName cn = new ComponentName(this, LockAdminReceiver.class);
		if (!dpm.isAdminActive(cn)) {
			dialogs.alert("首次允许需要将本程序设置激活为设备管理器.");
			ComponentName dpmSettings = new ComponentName(
					"com.android.settings",
					"com.android.settings.DeviceAdminSettings");
			Intent intent = new Intent();
			intent.setComponent(dpmSettings);
			intent.setAction("android.intent.action.MAIN");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return;
		}
		if (dpm != null) {
			dpm.lockNow();
			return;
		}
	}
}
