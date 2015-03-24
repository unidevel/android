package com.unidevel.SMSTrack;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AppSelectActivity extends Activity implements OnClickListener {
	public static final String EXTRA_APPS = "apps";
	AppSelectListView appsView; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_app);
		Intent intent = getIntent();
		String[] apps = intent.getStringArrayExtra("APPS");
		appsView = (AppSelectListView)findViewById(R.id.listApps);
		appsView.setSelectedApps(apps);
		appsView.refreshApps();
		((Button)findViewById(R.id.btn_ok)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_cancel)).setOnClickListener(this);
	}
	
	public void onClick(View v) {
		if ( v.getId() == R.id.btn_ok ) {
			Intent intent = new Intent();
			List<String> apps = appsView.getSelectedApps();
			intent.putExtra(EXTRA_APPS, (String[])apps.toArray(new String[0]));
			this.setResult(RESULT_OK, intent);
		}
		else {
			this.setResult(RESULT_CANCELED, null);
		}
		this.finish();
	}
	
	
}
