package com.unidevel.tools.locker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.unidevel.util.DialogUtil;
import com.unidevel.util.RootUtil;

public class ActionActivity extends Activity {
	private static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
	private static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";

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
			reboot();
			break;
		case 3:
			shutdown();
			break;
		case 9:
			cancel(1);
			break;
		}
		finish();
		// System.exit(0);
	}

	private void shutdown() {
		Intent shutdown = new Intent(ACTION_REQUEST_SHUTDOWN); 
		shutdown.putExtra(EXTRA_KEY_CONFIRM, true); 
		shutdown.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(shutdown); 
	}

	private void reboot() {
		dialogs.confirm("确定重新启动吗？", new Runnable() {
			@Override
			public void run() {
				RootUtil.run("reboot");
			}
		});
	}

	public void msg(String s) {
		Toast.makeText(this, s, 3).show();
	}

	public void cancel(int id) {
		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(id);
	}

	public void lock() {
		DevicePolicyManager dpm = null;
		dpm = (DevicePolicyManager) this
				.getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName cn = new ComponentName(this, LockAdminReceiver.class);
		if (!dpm.isAdminActive(cn)) {
			dialogs.alert("首次使用锁屏功能需将本程序激活为设备管理器，点击确定开始设置");
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
