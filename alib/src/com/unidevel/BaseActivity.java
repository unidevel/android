package com.unidevel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.unidevel.util.DialogUtil;
import com.unidevel.util.FileUtil;
import com.unidevel.util.ZipUtil;

public class BaseActivity extends Activity {
	private static final String LOG_TAG = BaseActivity.class.getSimpleName();
	protected SharedPreferences pref;
	private int logLevel = Log.INFO;
	private DialogUtil dialogUtil = new DialogUtil(this);

	protected void put(String key, String value) {
		this.pref.edit().putString(key, value).commit();
	}

	protected void put(String key, int value) {
		this.pref.edit().putInt(key, value).commit();
	}

	protected void put(String key, boolean value) {
		this.pref.edit().putBoolean(key, value).commit();
	}

	protected String getString(String key) {
		return this.pref.getString(key, null);
	}

	protected int getInt(String key) {
		return this.pref.getInt(key, 0);
	}

	protected boolean getBoolean(String key) {
		return this.pref.getBoolean(key, false);
	}

	protected int getVersionCode() {
		int versionCode = 0;
		try {
			versionCode = this.getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return versionCode;
	}

	protected String getVersionName() {
		String versionName = null;
		try {
			versionName = this.getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return versionName;
	}

	protected void extractAsset(String assetZipFile, File destDir,
			boolean forceUpdate) throws IOException {
		File dir = this.getFilesDir();
		String key = "asset_" + assetZipFile;
		if (shouldUpdate(key, forceUpdate)) {
			try {
				InputStream in = this.getAssets().open(assetZipFile);
				ZipUtil.extract(in, dir);
				int versionCode = this.getVersionCode();
				put(key, versionCode);
			} catch (IOException e) {
				Log.e(LOG_TAG + ".extract", e.getMessage(), e);
				throw e;
			}
		}
	}

	protected void copyAssets(String dir, File destDir, boolean forceUpdate)
			throws IOException {
		String key = "asset_" + dir;
		int versionCode = this.getVersionCode();
		if (shouldUpdate(key, forceUpdate)) {
			String[] allAssets = this.getAssets().list(dir);
			for (String asset : allAssets) {
				String assetFile = dir + "/" + asset;
				copyAsset(assetFile, destDir);
			}
			put(key, versionCode);
		}
	}

	private boolean shouldUpdate(String key, boolean forceUpdate) {
		if (forceUpdate)
			return true;
		int versionCode = this.getVersionCode();
		int savedCode = getInt(key);
		return versionCode != savedCode;
	}

	protected void copyAsset(String assetFile, File destDir) throws IOException {
		InputStream in = null;
		try {
			in = this.getAssets().open(assetFile);
			File destFile = new File(destDir, assetFile);
			File dir = destFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileUtil.copy(in, destFile);
		} catch (IOException ex) {
			Log.e(LOG_TAG + ".copyAsset", ex.getMessage(), ex);
			throw ex;
		} finally {
			try {
				in.close();
			} catch (Throwable ex) {
			}
		}
	}

	protected File appFile(String path) {
		File file = new File(getFilesDir(), path);
		return file;
	}

	private void setCurrentActivity() {
		Context appContext = this.getApplicationContext();
		if (appContext instanceof BaseApplication) {
			((BaseApplication) appContext).setCurrentActivity(this);
		}
	}

	private void clearCurrentActivity() {
		Context appContext = this.getApplicationContext();
		if (appContext instanceof BaseApplication) {
			BaseApplication app = ((BaseApplication) appContext);
			Activity currentActivity = app.getCurrentActivity();
			if (this.equals(currentActivity)) {
				app.setCurrentActivity(null);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		setCurrentActivity();
	}

	@Override
	protected void onPause() {
		clearCurrentActivity();
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		clearCurrentActivity();
		super.onDestroy();
	}

	protected void setLogLevel(int level) {
		this.logLevel = level;
	}

	protected String getLogTag() {
		return getClassAndMethodName(3);
	}

	private String getClassAndMethodName(int level) {
		Exception ex = new Exception();
		StackTraceElement items[] = ex.getStackTrace();
		StackTraceElement item = items[level];
		return item.getClassName() + "." + item.getMethodName();
	}

	protected void d(String msg) {
		if (this.logLevel >= Log.DEBUG) {
			Log.d(getLogTag(), msg);
		}
	}

	protected void d(String msg, Throwable ex) {
		if (this.logLevel >= Log.DEBUG) {
			Log.d(getLogTag(), msg, ex);
		}
	}

	protected void i(String msg) {
		if (this.logLevel >= Log.INFO) {
			Log.i(getLogTag(), msg);
		}
	}

	protected void i(String msg, Throwable ex) {
		if (this.logLevel >= Log.INFO) {
			Log.i(getLogTag(), msg, ex);
		}
	}

	protected void w(String msg) {
		if (this.logLevel >= Log.WARN) {
			Log.w(getLogTag(), msg);
		}
	}

	protected void w(String msg, Throwable ex) {
		if (this.logLevel >= Log.WARN) {
			Log.w(getLogTag(), msg, ex);
		}
	}

	protected void e(String msg) {
		if (this.logLevel >= Log.ERROR) {
			Log.e(getLogTag(), msg);
		}
	}

	protected void e(String msg, Throwable ex) {
		if (this.logLevel >= Log.ERROR) {
			Log.e(getLogTag(), msg, ex);
		}
	}

	public int d2p(float dipValue) {
		final float scale = this.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public float p2d(int pxValue) {
		final float scale = this.getResources().getDisplayMetrics().density;
		return (float) pxValue / scale + 0.5f;
	}

	public void t(String message) {
		t(message, true);
	}

	public void t(String message, boolean longDuration) {
		Toast.makeText(this, message,
				longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}

	public void alert(String title, String message, Throwable ex) {
		this.dialogUtil.alert(title, message, ex);
	}

	public void alert(String title, String message) {
		this.dialogUtil.alert(title, message);
	}

	public void alert(String message) {
		this.dialogUtil.alert(message);
	}
}
