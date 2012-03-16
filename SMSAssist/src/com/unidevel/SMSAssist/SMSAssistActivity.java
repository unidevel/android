package com.unidevel.SMSAssist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class SMSAssistActivity extends Activity implements TextWatcher, OnClickListener, OnCheckedChangeListener, OnItemClickListener, OnLongClickListener {
	SMSAdapter adapter ;
	CheckBox selectAll;
	ProgressDialog progressDialog;
	enum FilterType {STARTS_WITH, ENDS_WITH, CONTAINS, FULLTEXT};
	FilterType filterType;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		EditText filter = (EditText)findViewById(R.id.filter);
		filter.addTextChangedListener(this);
		
		filterType = FilterType.STARTS_WITH;
		filter.setHint(R.string.menu_start_with);
		filter.setInputType(InputType.TYPE_CLASS_PHONE);
		ImageButton filterButton = (ImageButton)findViewById(R.id.filterButton);
		filterButton.setOnClickListener(this);
//		selectAll = (CheckBox)findViewById(R.id.selectAll);
//		selectAll.setOnClickListener(this);
//		selectAll.setOnCheckedChangeListener(this);
		
		ListView list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
		registerForContextMenu(filterButton);
		reloadSMS();
	
		AdView adView = new AdView(this, AdSize.BANNER, "a14f4b35981e2b7");
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.addView(adView);
		AdRequest req  = new AdRequest();
		adView.loadAd(req);
	}

	public void reloadSMS(){
		final Handler handler = new Handler();
		final ListView list = (ListView)findViewById(R.id.list);
		final List<SMS> oldItems = adapter!=null?adapter.getAll():null;
		Thread thread = new Thread(){
			@Override
			public void run() {
				Uri smsUri = Uri.parse("content://sms");
				final List<SMS> items = new ArrayList<SMS>();
				Cursor cursor = SMSAssistActivity.this.getContentResolver().query(
						smsUri, new String[] { "_id", "thread_id", "address", "person", "date", "type", "body" }, null, null, null);
				if ( cursor != null ) {
					Log.i("count", ""+cursor.getCount());
					int size = oldItems == null? 0: oldItems.size();
					for( boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext() ){
						SMS sms = new SMS();
						sms.id = cursor.getInt(0);
						sms.threadId = cursor.getInt(1);
						sms.address = cursor.getString(2);
						sms.person = cursor.getInt(3);
						sms.date = cursor.getLong(4);
						sms.type = cursor.getInt(5);
						sms.body = cursor.getString(6);
						if ( sms.address == null ) sms.address = "";
						if ( sms.body == null ) sms.body = "";
						for (int i = 0; i < size; ++i){
							SMS oldSMS = oldItems.get(i);
							if (oldSMS.id==sms.id){
								sms.selected=oldSMS.selected;
								oldItems.remove(i);
								break;
							}
						}
						
						items.add(sms);
					}
					cursor.close();
				}
				handler.post(new Runnable(){
					public void run() {
						adapter = new SMSAdapter(items, SMSAssistActivity.this);
						list.setAdapter(adapter);
						if ( progressDialog != null ){
							progressDialog.dismiss();
							progressDialog = null;
						}
						Toast.makeText(SMSAssistActivity.this, getString(R.string.msg_reload), 3);
					}
				});
			}
		};
		thread.start();
	}
	
	public void afterTextChanged(Editable edit) {
//		if ( adapter != null ) adapter.applyFilter(edit.toString());
		refreshFilter(edit.toString(), filterType);
	}

	private void refreshFilter(String filter, FilterType type){
		if ( adapter == null ) return;
		switch(type){
		case STARTS_WITH:
			adapter.selectNumberStarts(filter);
			break;
		case CONTAINS:
			adapter.selectNumberContains(filter);
			break;
		case FULLTEXT:
			adapter.selectByFullTextSearch(filter);
			break;
		case ENDS_WITH:
			adapter.selectNumberEnds(filter);
			break;
		}
	}
	
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	public void onClick(View view) {
		if (view.getId() == R.id.filterButton){
			if( !adapter.isAllSelected() ) {
				adapter.showSelected();
				Toast.makeText(this, getString(R.string.msg_showselected), 2).show();
			}
			else {
				EditText filter = (EditText)findViewById(R.id.filter);
				refreshFilter(filter.getText().toString(), filterType);	
			}
		}
	}

	public void onCheckedChanged(CompoundButton button, boolean checked) {
		if (adapter != null) adapter.selectAll(checked);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if ( item.getItemId() == R.id.delete ){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					deleteSMS();
				}
			});
			builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setTitle(R.string.title_delete);
			builder.setMessage(R.string.message_delete);
			builder.create().show();
		}
		else if ( item.getItemId() == R.id.select_all ) {
			if ( adapter != null )adapter.selectAll(true);
		}
		else if ( item.getItemId() == R.id.invert ) {
			if ( adapter != null )adapter.invertSelection();
		}
//		else if ( item.getItemId() == R.id.clear ) {
//			if ( adapter != null )adapter.selectAll(false);
//		}		
		else if ( item.getItemId() == R.id.exit ) {
			this.finish();
		}
//		else if ( item.getItemId() == R.id.about ){
//			
//		}
		else if ( item.getItemId() == R.id.reload) {
			reloadSMS();
		}
		else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void deleteSMS(){
		if (adapter == null ) return;
		List<SMS> items = adapter.getSelected();
		ContentResolver resolver = getContentResolver();
		try {
			for (SMS sms: items){
				resolver.delete(Uri.parse("content://sms/"+sms.id),null, null);
			}
			adapter.deleteSelected();
		}catch(Throwable ex){
			Log.e("SMSAssist", ex.getMessage(), ex);
			Toast.makeText(this, "Error:"+ex.getMessage(), 3).show();
		}
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		SMS sms = (SMS)adapter.getItem(index);
//		TextView text = new TextView(this);
//		text.setTextAppearance(this, android.R.attr.textAppearanceMedium);
//		text.setText(sms.body);
		builder.setMessage(sms.body);
//		builder.setView(text);
		builder.setTitle(sms.address);
		builder.create().show();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if ( v.getId() == R.id.list && adapter != null ) {
			menu.setHeaderTitle(R.string.menu_select);
			getMenuInflater().inflate(R.menu.context_menu, menu);
		}
		else if ( v.getId() == R.id.filterButton ) {
			menu.setHeaderTitle(R.string.menu_filter_type);
			getMenuInflater().inflate(R.menu.filter_menu, menu);			
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		EditText filter = (EditText)findViewById(R.id.filter);
		if ( item.getItemId() == R.id.filter_start_with){
			filterType = FilterType.STARTS_WITH;
			filter.setHint(R.string.menu_start_with);
			filter.setInputType(InputType.TYPE_CLASS_PHONE);
			refreshFilter(filter.getText().toString(), filterType);
		}
		else if ( item.getItemId() == R.id.filter_end_with){
			filterType = FilterType.ENDS_WITH;
			filter.setHint(R.string.menu_end_with);
			filter.setInputType(InputType.TYPE_CLASS_PHONE);
			refreshFilter(filter.getText().toString(), filterType);
		}
		else if ( item.getItemId() == R.id.filter_contains){
			filterType = FilterType.CONTAINS;
			filter.setHint(R.string.menu_contains);
			filter.setInputType(InputType.TYPE_CLASS_PHONE);
			refreshFilter(filter.getText().toString(), filterType);
		}
		else if ( item.getItemId() == R.id.filter_fulltext){
			filterType = FilterType.FULLTEXT;
			filter.setHint(R.string.menu_fulltext);
			filter.setInputType(InputType.TYPE_CLASS_TEXT);
			refreshFilter(filter.getText().toString(), filterType);			
		}		
		if ( adapter == null ) return false;
		if( item.getItemId() == R.id.select_same_thread ){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			SMS sms = (SMS)adapter.getItem(menuInfo.position);
			adapter.selectByThread(sms.threadId);
		}
		else if ( item.getItemId() == R.id.select_same_address ){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			SMS sms = (SMS)adapter.getItem(menuInfo.position);
			adapter.selectByAddress(sms.address);
		}
		else if ( item.getItemId() == R.id.select_same_date ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			SMS sms = (SMS)adapter.getItem(menuInfo.position);
			adapter.selectByDate(sms.date);			
		}
		else if ( item.getItemId() == R.id.select_before_this ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			SMS sms = (SMS)adapter.getItem(menuInfo.position);
			adapter.selectBefore(sms.date);			
		}
		else if ( item.getItemId() == R.id.select_after_this ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			SMS sms = (SMS)adapter.getItem(menuInfo.position);
			adapter.selectAfter(sms.date);			
		}
		else if ( item.getItemId() == R.id.select_except_this ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			SMS sms = (SMS)adapter.getItem(menuInfo.position);
			adapter.selectExcept(sms.id);
		}
		else if ( item.getItemId() == R.id.select_not_from_contact ) {
			if ( adapter != null )adapter.selectNotFromContact();
		}		
		else return super.onContextItemSelected(item);
		return true;
	}

	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		return false;
	}
}