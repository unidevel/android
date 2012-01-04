package com.unidevel.desklink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengUpdateListener;

public class DeskLinkSettings extends PreferenceActivity implements
		OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		addPreferencesFromResource(R.xml.preferences);
		findViewById(R.id.btnOk).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);

		UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
		
		try {
			final PackageInfo appInfo = this.getPackageManager()
					.getPackageInfo(this.getPackageName(), 0);
			Preference prefVersion = findPreference("keyVersion");
			prefVersion.setSummary(appInfo.versionName);

			Preference prefAuthor = findPreference("keyAuthor");
			prefAuthor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Zhou%20Li"));
					startActivity(intent);
					return true;
				}
			});

			
			Preference prefRate = findPreference("keyRate");
			prefRate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
//					Intent intent = new Intent(Intent.ACTION_VIEW,
//							Uri.parse("market://details?id="
//									+ appInfo.packageName));
//					startActivity(intent);
					UMFeedbackService.openUmengFeedbackSDK(DeskLinkSettings.this);
					return true;
				}
			});

			Preference prefUpdate = findPreference("keyUpdateSelf");
			prefUpdate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					MobclickAgent.update(DeskLinkSettings.this);
					MobclickAgent.updateAutoPopup = false;
					final ProgressDialog dialog = ProgressDialog.show(DeskLinkSettings.this, null, getString(R.string.updatingSelf));
					MobclickAgent.setUpdateListener(new UmengUpdateListener() {
						@Override
						public void onUpdateReturned(int arg) {
							dialog.dismiss();
							switch (arg) {
							case 0: // has update
								MobclickAgent.showUpdateDialog(DeskLinkSettings.this);
								break;
							case 1: // has no update
								Toast.makeText(DeskLinkSettings.this, getString(R.string.noUpdate), 3).show();
								break;
							case 2: // none wifi
								Toast.makeText(DeskLinkSettings.this, getString(R.string.noUpdate), 3).show();
								break;
							case 3: // time out
								Toast.makeText(DeskLinkSettings.this, getString(R.string.timeout), 3).show();
								break;
							}
						}
					});
					return true;
				}
			});
		} catch (NameNotFoundException e) {
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btnOk) {
			setResult(RESULT_OK);
			this.finish();
		} else if (view.getId() == R.id.btnCancel) {
			setResult(RESULT_CANCELED);
			this.finish();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
