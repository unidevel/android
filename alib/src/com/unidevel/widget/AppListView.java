package com.unidevel.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.unidevel.R;

public class AppListView extends LinearLayout {

	ListView listView;
	AppListAdapter adapter;
	View itemView;
	LayoutInflater inflater;
	ProgressDialog progressDialog;

	public AppListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.inflater = LayoutInflater.from(context);
		this.inflater.inflate(R.layout.applistview, this, true);
		this.itemView = this.inflater.inflate(R.layout.appitem, null);
		this.listView = (ListView) this.findViewById(R.id.listView);
		String mode = attrs.getAttributeValue(R.styleable.list_mode);
		if ("1".equals(mode)) {

		} else if ("2".equals(mode)) {

		}
		this.adapter = new AppListAdapter(this.getContext(), this.itemView);
		this.listView.setAdapter(this.adapter);
		AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				adapter.listApps();
				return null;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if ( progressDialog != null )
				{
					progressDialog.cancel();
					progressDialog = null;
				}
				progressDialog = ProgressDialog.show(getContext(), null, getContext().getString(R.string.loading_activities));
			}
			
			@Override
			protected void onCancelled() {
				super.onCancelled();
				progressDialog.cancel();
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				progressDialog.dismiss();
			}
		};
		
		task.execute();
	}
}
