package com.unidevel.SMSTrack;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.View;

public class SelectAppPreference2  extends DialogPreference {
	String[] selectedApps;
	AppSelectListView appSelectListView;
	int maxApps = 0;
	public SelectAppPreference2(Context context) {
		super(context, null);
		setDialogLayoutResource(R.layout.select_app_dialog);
		selectedApps = new String[0];
	}
	
	public void setSelectedApps(String... selectedApps) {
		this.selectedApps = selectedApps;
	}
	
	public void setMaxApps(int apps){
		maxApps = apps;
	}
	
	public String[] getSelectedApps() {
		return selectedApps;
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		appSelectListView = (AppSelectListView)view.findViewById(R.id.listApps);
	}
	
	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);
		appSelectListView.setMaxApps(maxApps);
		appSelectListView.setSelectedApps(selectedApps);
		appSelectListView.refreshApps();
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if ( positiveResult ) {
			selectedApps = appSelectListView.getSelectedApps().toArray(new String[0]);
			notifyChanged();
			callChangeListener(selectedApps);
		}
	}
}
