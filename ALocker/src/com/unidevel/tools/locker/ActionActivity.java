package com.unidevel.tools.locker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.unidevel.util.DialogUtil;
import com.unidevel.util.RootUtil;
import android.media.*;

public class ActionActivity extends Activity {
	public static final int ACTION_LOCK = 1;

	public static final int ACTION_SHUTDOWN = 2;

	public static final int ACTION_VOLUME_DOWN = 3;

	public static final int ACTION_VOLUME_UP = 4;

	public static final int ACTION_CANCEL = 9;

	private DialogUtil dialogs;
	private boolean isRooted;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		dialogs = new DialogUtil(this);
		int actId = getIntent().getIntExtra("action", 0);
		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		this.isRooted = pref.getBoolean("root", false);
		switch (actId) {
		case ACTION_LOCK:
			if ( this.isRooted ) lockRoot();
			else lockNonRoot();
			break;
		case ACTION_SHUTDOWN:
			shutdown();
			break;
		case ACTION_VOLUME_DOWN:
			volDown();
			break;
		case ACTION_VOLUME_UP:
			volUp();
			break;
		case ACTION_CANCEL:
			cancel(1);
			break;
		}
		finish();
	}

	private void volDown()
	{
		adjustVolume(AudioManager.ADJUST_LOWER);
		//sendKey(114,0);
	}

	private void volUp()
	{
		adjustVolume(AudioManager.ADJUST_RAISE);
		//sendKey(115,0);
	}
	
	private void adjustVolume(int direction){
		AudioManager am=(AudioManager) getSystemService(AUDIO_SERVICE);
		am.adjustVolume(direction,AudioManager.FLAG_SHOW_UI|AudioManager.FLAG_PLAY_SOUND|AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_VIBRATE);
	}

	private void shutdown() {
		sendKey(116,1);
	}
	
	private void sendKey(int key,int sleep){
		String cmd = "sendevent /dev/input/event1 1 "+key+" 1\n"+
			"sendevent /dev/input/event1 0 0 0\n";
		if(sleep>0)cmd+="sleep 1\n";
		cmd+="sendevent /dev/input/event1 1 "+key+" 0\n"+
			"sendevent /dev/input/event1 0 0 0\n";
		RootUtil.run(cmd);
	}
	
	private void lockRoot() {
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

	public void lockNonRoot() {
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
