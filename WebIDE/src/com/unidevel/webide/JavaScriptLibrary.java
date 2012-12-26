package com.unidevel.webide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

public class JavaScriptLibrary {
	final Activity ctx;
	private List<String> callbacks;

	public JavaScriptLibrary(Activity context) {
		this.ctx = context;
		this.callbacks = new ArrayList<String>();
	}

	public SharedPreferences pref() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this.ctx);
		return pref;
	}

	public String getPref(String name) {
		return pref().getString(name, null);
	}

	public void putPref(String name, String value) {
		pref().edit().putString(name, value).commit();
	}

	public String[] listDir(String dir) {
		File d = new File(dir);
		return d.list();
	}

	public boolean isFile(String path) {
		File f = new File(path);
		return f.isFile();
	}

	public String getFile(String name) {
		File d = ctx.getFilesDir();
		File f = new File(d, name);
		return f.getPath();
	}

	public String dataDir() {
		return Environment.getDataDirectory().getPath();
	}

	public String rootDir() {
		return Environment.getRootDirectory().getPath();
	}

	public String read(String file) {
		StringBuffer buf = new StringBuffer();
		BufferedReader fr = null;
		try {
			fr = new BufferedReader(new FileReader(file));
			char cbuf[] = new char[8192];
			for (int l = fr.read(cbuf); l > 0; l = fr.read(cbuf)) {
				buf.append(cbuf, 0, l);
			}

		} catch (Exception e) {
			return null;
		} finally {
			try {
				fr.close();
			} catch (Exception e) {
			}
		}
		return buf.toString();
	}

	public boolean write(String file, String value) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(value);
			fw.flush();
			return true;
		} catch (IOException e) {

		} finally {
			try {
				fw.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	public void view(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		this.ctx.startActivity(intent);
	}

	public void call(String number) {
		Uri uri = Uri.parse("tel:" + number);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		this.ctx.startActivity(it);
	}

	public void market(String appId) {
		Uri uri = Uri.parse("market://details?id=" + appId);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		this.ctx.startActivity(it);
	}

	public void map(String x, String y) {
		Uri uri = Uri.parse("geo:" + x + "," + y);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		this.ctx.startActivity(it);
	}

	public void select(String type, String callback) {
		Intent it = new Intent();
		it.setType(type + "/*");
		it.setAction(Intent.ACTION_GET_CONTENT);
		int id = addCallback(type + ":" + callback);
		this.ctx.startActivityForResult(it, id);
	}

	public void selectImage(String callback) {
		select("image", callback);
	}

	private int addCallback(String callback) {
		synchronized (callbacks) {
			int size = callbacks.size();
			for (int i = 0; i < size; ++i) {
				if (callbacks.get(i) == null) {
					callbacks.set(i, callback);
					return i;
				}
			}
			callbacks.add(callback);
			return size;
		}
	}

	public String getCallback(int position) {
		synchronized (callbacks) {
			if (position >= 0 && position < callbacks.size()) {
				return callbacks.get(position);
			}
		}
		return null;
	}

	public void removeCallback(String callback) {
		synchronized (callbacks) {
			int pos = callbacks.indexOf(callback);
			if (pos >= 0) {
				callbacks.set(pos, null);
				for (int i = callbacks.size() - 1; i >= pos; --i) {
					if (callbacks.get(i) == null)
						callbacks.remove(i);
				}
			}
		}
	}
}
