package com.unidevel.test;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import com.unidevel.*;
import com.unidevel.widget.*;
import android.widget.*;

public class TestAppList extends Activity
{
	ListView listView;
	AppListAdapter adapter;
	View itemView;
	LayoutInflater inflater;
	ProgressDialog progressDialog;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applistview);
		init(this,null);
		listApp();
    }
	
	protected void init(Context context, AttributeSet attrs) {
		this.inflater = LayoutInflater.from(context);
		
		this.itemView = this.inflater.inflate(R.layout.appitem, null);
		this.listView = (ListView) this.findViewById(R.id.appList);
		if ( attrs != null )
		{
			String mode = attrs.getAttributeValue(R.styleable.list_mode);
			if ("1".equals(mode)) {

			} else if ("2".equals(mode)) {

			}
		}
		this.adapter = new AppListAdapter(this.getContext(), this.itemView);
		//this.listView.setAdapter(this.adapter);
	}

	public void listApp(){
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
				adapter.notifyDataSetInvalidated();
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				progressDialog.dismiss();
				adapter.notifyDataSetInvalidated();
				listView.setAdapter(adapter);
			}
		};

		task.execute();
	}
	
	Context getContext(){
		return this;
	}
}
