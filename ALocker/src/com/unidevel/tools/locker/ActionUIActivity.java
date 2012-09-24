package com.unidevel.tools.locker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionUIActivity extends Activity implements OnClickListener{
	boolean isRooted = false;
	Context ctx = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools);

		SharedPreferences pref;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		this.isRooted = pref.getBoolean("root", false);

		findViewById(R.id.labelLock).setOnClickListener(this);
		if (isRooted){
			findViewById(R.id.labelShutdown).setVisibility(View.VISIBLE);
			findViewById(R.id.labelShutdown).setOnClickListener(this);
		}
		else {
			findViewById(R.id.labelShutdown).setVisibility(View.GONE);
		}
		findViewById(R.id.labelVolDown).setOnClickListener(this);
		findViewById(R.id.labelVolUp).setOnClickListener(this);
		findViewById(R.id.labelCancel).setOnClickListener(this);
		String pkgName = pref.getString("slot1.pkg", "");
		String labelName = pref.getString("slot1.name", "");
		if ( pkgName.length()>0 ){
			findViewById(R.id.slot1).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.textSlot1)).setText(labelName);
			Bitmap icon=((BitmapDrawable)MainActivity.getAppIcon(ctx,pkgName)).getBitmap();
			((ImageView)findViewById(R.id.imageSlot1)).setImageBitmap(icon);
			findViewById(R.id.slot1).setOnClickListener(this);
			findViewById(R.id.slot1).setTag(pkgName);
		}
		else {
			findViewById(R.id.slot1).setVisibility(View.GONE);
		}
	}
	@Override
	public void onClick(View v) {
		Intent i = new Intent(ctx, ActionActivity.class);
		switch(v.getId())
		{
		case R.id.labelLock:
			i.putExtra("action", ActionActivity.ACTION_LOCK);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			break;
		case R.id.labelShutdown:
			i.putExtra("action", ActionActivity.ACTION_SHUTDOWN);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			break;
		case R.id.labelVolDown:
			i.putExtra("action", ActionActivity.ACTION_VOLUME_DOWN);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			break;
		case R.id.labelVolUp:
			i.putExtra("action", ActionActivity.ACTION_VOLUME_UP);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			break;
		case R.id.slot1:
			i = ctx.getPackageManager().getLaunchIntentForPackage((String)v.getTag());
			break;
		case R.id.labelCancel:
			this.finish();
			return;
		}
		startActivity(i);
	}
}
