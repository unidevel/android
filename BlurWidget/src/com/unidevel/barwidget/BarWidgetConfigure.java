package com.unidevel.barwidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import android.app.AlertDialog.Builder;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengUpdateListener;

//import com.gfan.sdk.statistics.Collector;
//import com.gfan.sdk.statistics.CommentActivity;

class BarWidgetConfigure extends PreferenceActivity implements OnClickListener, OnMenuItemClickListener, UmengUpdateListener {
	static final String PREFIX_TITLE = "title";
	static final String PREFIX_TITLE_COLOR = "color";
	static final String PREFIX_SIDE = "side";
	static final String PREFIX_APPS = "apps";
	static final String PREFIX_WIDGET_ID = "widgetid";
	static final String PREFIX_TITLE_IMG = "title_img";
	static final String PREFIX_APP_PKG = "app_pkg";
	static final String PREFIX_APP_CLS = "app_class";
	static final String PREFIX_APP_NAME = "app_name";
	static final String PREFIX_APP_ICON = "app_icon";
	public static final String PREFS_NAME = "blurwidget";
	int mAppWidgetId;
	int nPrograms;
//	SelectAppPreference[] appPrefs;
	SelectAppPreference2 appPrefs;
	AppListView appListView;
	EditTextPreference titlePrefs;
	CheckBoxPreference sidePrefs;
	ColorPreference colorPrefs;
	@Override
	protected void onResume() {
//		Collector.onResume(this);
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
//		Collector.onPause(this);
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		MobclickAgent.onError(this);
		MobclickAgent.setUpdateOnlyWifi(false);
		MobclickAgent.setUpdateListener(this);
		MobclickAgent.update(this);
		
        setResult(RESULT_CANCELED);
		setContentView(R.layout.widget_configure);
		
		appListView = (AppListView)findViewById(R.id.listApps);
		
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            nPrograms = extras.getInt("programs", 0);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
		
        
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		
		PreferenceCategory titleCat = new PreferenceCategory(this);
		titleCat.setTitle(this.getString(R.string.category_title));
		root.addPreference(titleCat);
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		titlePrefs = new EditTextPreference(this);
		titlePrefs.setKey(getTitleKey(mAppWidgetId));
		String title = prefs.getString(getTitleKey(mAppWidgetId), "");
		titlePrefs.setTitle(getString(R.string.summary_title));
		titlePrefs.setSummary(title.length()==0?" ":title);
		titlePrefs.setDialogTitle(getString(R.string.prefdlg_title));
		titlePrefs.setText(title);
		titlePrefs.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				titlePrefs.setSummary(newValue.toString());
				return true;
			}
		});
		titleCat.addPreference(titlePrefs);
		
		colorPrefs = new ColorPreference(this);
		colorPrefs.setTitle(R.string.title_select_color);
		int color = prefs.getInt(getTitleColorKey(mAppWidgetId), 0xFFFFFFFF);
		colorPrefs.setColor(color);
		titleCat.addPreference(colorPrefs);
		
		if ( findViewById(R.id.ad_view) == null ) throw new RuntimeException();
		
		sidePrefs = new CheckBoxPreference(this);
		sidePrefs.setKey(getSideKey(mAppWidgetId));
		boolean checked = prefs.getBoolean(getSideKey(mAppWidgetId), false);
		sidePrefs.setChecked(checked);
		sidePrefs.setTitle(getString(R.string.title_side));
		if ( checked ){
			sidePrefs.setSummary(getString(R.string.summary_side_true));
		}
		else {
			sidePrefs.setSummary(getString(R.string.summary_side_false));
		}
		sidePrefs.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean checked = (Boolean)newValue;
				if ( checked ){
					sidePrefs.setSummary(getString(R.string.summary_side_true));
				}
				else {
					sidePrefs.setSummary(getString(R.string.summary_side_false));
				}
				return true;
			}
		});
		titleCat.addPreference(sidePrefs);

		PreferenceCategory appCat = new PreferenceCategory(this);
		appCat.setTitle(this.getString(R.string.category_apps));
		root.addPreference(appCat);
//		appPrefs = new SelectAppPreference[getPrograms()];
//		for ( int i = 0; i < getPrograms(); ++ i  ){ 
//			SelectAppPreference appPref = new SelectAppPreference(this);
//			String pkg = prefs.getString(getAppPackageKey(mAppWidgetId, i), null);
//			String cls = prefs.getString(getAppClassKey(mAppWidgetId, i), null);
//			if ( pkg != null ) {
//				appPref.setApp(pkg, cls);
//			}
//			appCat.addPreference(appPref);
//			appPrefs[i] = appPref;
//		}
		
		appPrefs = new SelectAppPreference2(this);
		String titleSelectApp = String.format(getString(R.string.title_select_app), getPrograms());
		appPrefs.setTitle(titleSelectApp);
		appPrefs.setMaxApps(getPrograms());
		appPrefs.setDialogTitle(titleSelectApp);
		appPrefs.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				appListView.setApps(appPrefs.getSelectedApps());
				return true;
			}
		});
		appCat.addPreference(appPrefs);
		setPreferenceScreen(root);
		
		for ( int i = 0; i < getPrograms(); ++ i  ){ 
			String pkg = prefs.getString(getAppPackageKey(mAppWidgetId, i), null);
			String cls = prefs.getString(getAppClassKey(mAppWidgetId, i), null);
			String icon = prefs.getString(getAppIconKey(mAppWidgetId, i), null);
			appListView.addApp(pkg, cls, icon, false);
		}
		appListView.refresh();
		appPrefs.setSelectedApps(appListView.getApps().toArray(new String[0]));
		
		appListView.getAdapter().registerDataSetObserver(new DataSetObserver(){
			@Override
			public void onChanged() {
				super.onChanged();
				appPrefs.setSelectedApps(appListView.getApps().toArray(new String[0]));
			}
		});
		registerForContextMenu(appListView);
		
		((Button)findViewById(R.id.btn_ok)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_cancel)).setOnClickListener(this);
	}
		
	public int getPrograms(){
		return nPrograms;
	}
	
	public int getAppWidgetResId(){
		return 0;
	}
	
	public static String getAppNameKey(int widgetId, int index){
		return PREFIX_APP_NAME + "_" + widgetId + "_" + index;
	}

	public static String getAppPackageKey(int widgetId, int index){
		return PREFIX_APP_PKG + "_" + widgetId + "_" + index;
	}
	
	public static String getAppClassKey(int widgetId, int index){
		return PREFIX_APP_CLS + "_" + widgetId + "_" + index;
	}
	
	public static String getAppIconKey(int widgetId, int index){
		return PREFIX_APP_ICON + "_" + widgetId + "_" + index;
	}
	
	public static String getTitleKey(int widgetId){
		return PREFIX_TITLE + "_" + widgetId;
	}
	
	public static String getTitleImageKey(int widgetId){
		return PREFIX_TITLE_IMG + "_" + widgetId;
	}
	
	public static String getSideKey(int widgetId){
		return PREFIX_SIDE + "_" + widgetId;
	}
	
	public static String getAppsKey(int widgetId){
		return PREFIX_APPS + "_" + widgetId;
	}
	
	public static String getWidgetResIdKey(int widgetId){
		return PREFIX_WIDGET_ID + "_" + widgetId;
	}
	
	public static String getTitleColorKey(int widgetId){
		return PREFIX_TITLE_COLOR + "_" + widgetId;
	}

	public void savePref(){
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putString(getTitleKey(mAppWidgetId), titlePrefs.getText());
		edit.putBoolean(getSideKey(mAppWidgetId), sidePrefs.isChecked());
		edit.putInt(getAppsKey(mAppWidgetId), getPrograms());
		edit.putInt(getWidgetResIdKey(mAppWidgetId), getAppWidgetResId());
		edit.putInt(getTitleColorKey(mAppWidgetId), colorPrefs.getColor());
//		List<String> apps = appListView.getApps();
		for ( int i = 0 ; i < appListView.getCount() ; i ++) {
			edit.putString(getAppPackageKey(mAppWidgetId, i), appListView.getAppPackage(i));
			edit.putString(getAppClassKey(mAppWidgetId, i), appListView.getAppClass(i));
			edit.putString(getAppIconKey(mAppWidgetId, i), appListView.getAppIcon(i));
		}
		edit.commit();
	}
	
	public void onClick(View v) {
		if ( v.getId() == R.id.btn_ok ) {
			final Context context = this;
			savePref();
			
            // Push widget update to surface with newly set prefix
			boolean checked = sidePrefs.isChecked();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BarWidget.updateWidget(context, this.getClass(), appWidgetManager, mAppWidgetId, getAppWidgetResId(), checked, getPrograms());

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            
//            Collector.setAppClickCount("ConfigureWidget");
        }
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.config_menu, menu);
		
		for ( int i = 0 ; i < menu.size(); ++ i ) {
			MenuItem item = menu.getItem(i);
			if ( item.getSubMenu()!=null && item.getSubMenu().size() > 0 ) {
				for ( int j = 0; j < item.getSubMenu().size(); ++ j ) {
					MenuItem child = item.getSubMenu().getItem(j);
					child.setOnMenuItemClickListener(this);
				}
			}
			else item.setOnMenuItemClickListener(this);
		}
		return super.onCreateOptionsMenu(menu);
	}

	private boolean exportSettings(File file){
		Properties props = new Properties();
		int widgetId = 0;
		props.setProperty(getTitleKey(widgetId), titlePrefs.getText());
		props.setProperty(getTitleColorKey(widgetId), String.valueOf(colorPrefs.getColor()));
		props.setProperty(getSideKey(widgetId), String.valueOf(sidePrefs.isChecked()));
		props.setProperty(getAppsKey(widgetId), String.valueOf(getPrograms()));
		props.setProperty(getWidgetResIdKey(widgetId), String.valueOf(getAppWidgetResId()));
		List<String> apps = appListView.getApps();
		for ( int i = 0 ; i < appListView.getCount() ; i ++) {
			props.setProperty(getAppPackageKey(widgetId, i), appListView.getAppPackage(i));
			props.setProperty(getAppClassKey(widgetId, i), appListView.getAppClass(i));
			props.setProperty(getAppIconKey(widgetId, i), appListView.getAppIcon(i));
		}
//		for ( int i = 0 ; i < apps.size() && i < getPrograms()*2; i += 2 ) {
//			props.setProperty(getAppPackageKey(widgetId, i>>1), apps.get(i));
//			props.setProperty(getAppClassKey(widgetId, i>>1), apps.get(i+1));
//			if ( appPrefs[i].getAppClassName() != null ) {
//				props.setProperty(getAppPackageKey(widgetId, i), appPrefs[i].getAppPackage());
//				props.setProperty(getAppNameKey(widgetId, i), appPrefs[i].getAppName());
//				props.setProperty(getAppClassKey(widgetId, i), appPrefs[i].getAppClassName());
//			}
//		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			props.save(out, new java.util.Date().toString());
		}
		catch(Throwable ex){
			Toast.makeText(this, getString(R.string.error_export), 3).show();
//			Collector.onError(this);
			return false;
		}
		finally{
			try { out.close(); } catch(Throwable ex){}
		}
		Toast.makeText(this, getString(R.string.success_export), 3).show();
		return true;
	}
	
	private boolean exportIconSettings(File file){
		Properties props = new Properties();
		int widgetId = 0;
		props.setProperty(getAppsKey(widgetId), String.valueOf(getPrograms()));
		for ( int i = 0 ; i < appListView.getCount() ; i ++) {
			props.setProperty(getAppIconKey(widgetId, i), appListView.getAppIcon(i));
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			props.save(out, new java.util.Date().toString());
		}
		catch(Throwable ex){
			Toast.makeText(this, getString(R.string.error_export), 3).show();
			return false;
		}
		finally{
			try { out.close(); } catch(Throwable ex){}
		}
		Toast.makeText(this, getString(R.string.success_export), 3).show();
		return true;
	}
	
	
	private boolean toBool(Object o, boolean defValue){
		try {
			return Boolean.valueOf(String.valueOf(o));
		}
		catch(Throwable ex){}
		return defValue;
	}
	
	private int toInt(Object o, int defValue){
		try {
			return Integer.valueOf(String.valueOf(o));
		}
		catch(Throwable ex){}
		return defValue;		
	}
	
	private boolean importIconSettings(File file){
		Properties props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			props.load(in);
			int widgetId = 0;
			for ( int i = 0 ; i < getPrograms() && i < appListView.getCount(); ++ i ) {
				String icon = props.getProperty(getAppIconKey(widgetId, i));
				appListView.setAppIcon(i, icon, false);
			}
			appListView.refresh();
		}
		catch(Throwable ex){
			Toast.makeText(this, getString(R.string.error_import), 3).show();
//			Collector.onError(this);
			return false;			
		}
		finally{
			try { in.close(); } catch(Throwable ex){}
		}
		Toast.makeText(this, getString(R.string.success_import), 3).show();
		return true;
	}
	
	private boolean importSettings(File file){
		Properties props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			props.load(in);
			int widgetId = 0;
			titlePrefs.setText(props.getProperty(getTitleKey(widgetId), ""));
			titlePrefs.setSummary(props.getProperty(getTitleKey(widgetId), ""));
			sidePrefs.setChecked(toBool(props.get(getSideKey(widgetId)), false));
			colorPrefs.setColor(toInt(props.get(getTitleColorKey(widgetId)), 0));
			colorPrefs.setDialogTitle(R.string.title_select_color);
			appListView.clearApps();
			for ( int i = 0 ; i < getPrograms(); ++ i ) {
				String pkg = props.getProperty(getAppPackageKey(widgetId, i));
				String cls = props.getProperty(getAppClassKey(widgetId, i));
				String icon = props.getProperty(getAppIconKey(widgetId, i));
				appListView.addApp(pkg, cls, icon, false);
			}
			appListView.refresh();
		}
		catch(Throwable ex){
			Toast.makeText(this, getString(R.string.error_import), 3).show();
//			Collector.onError(this);
			return false;			
		}
		finally{
			try { in.close(); } catch(Throwable ex){}
		}
		Toast.makeText(this, getString(R.string.success_import), 3).show();
		return true;
	}
	
	static final int REQUEST_CHOOSE_IMAGE = 11000;
	
	public boolean onMenuItemClick(MenuItem item) {
		if ( item.getItemId() == R.id.menu_export_all || item.getItemId() == R.id.menu_export_icons) {
			String name = titlePrefs.getText();
			if ( name == null || name.trim().length() == 0 ) {
				Date date = new Date();
				name = String.format("%tF", date);
			}
			Builder builder = new Builder(this);
			builder.setTitle(getString(R.string.title_export));
			final EditText text = new EditText(this);
			builder.setView(text);
			text.setText(name);
			final int itemId = item.getItemId();
			builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					File file = new File(getExportDir(BarWidgetConfigure.this), text.getText().toString());
					if ( itemId == R.id.menu_export_all ) {
						if ( exportSettings(file) ) {
							dialog.dismiss();
						}
					}
					else {
						if ( exportIconSettings(file) ) {
							dialog.dismiss();
						}
					}
					//					Collector.setAppClickCount("ExportSettings");
					MobclickAgent.onEvent(BarWidgetConfigure.this, "onExportConfig");					
				}
			});
			builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();	
				}
			});
			builder.create().show();
		}
		else if ( item.getItemId() == R.id.menu_import_all || item.getItemId() == R.id.menu_import_icons) {
			Builder builder = new Builder(this);
			builder.setTitle(getString(R.string.title_export));
			final File dir = getExportDir(this);
			final String[] files = dir.list();
			final int itemId = item.getItemId();
			builder.setItems(files, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					File file = new File(dir, files[which]);
					if ( itemId == R.id.menu_import_all ) {
						if ( importSettings(file) ) {
							dialog.dismiss();
						}
					}
					else {
						if ( importIconSettings(file)) {
							dialog.dismiss();
						}
					}
//					Collector.setAppClickCount("ImportSettings");
					MobclickAgent.onEvent(BarWidgetConfigure.this, "onImportConfig");
				}
			});
			builder.create().show();
		}
		else if ( item.getItemId() == R.id.menu_comment ) {
//			Intent intent = new Intent(this, CommentActivity.class);
//			startActivity(intent);
			MobclickAgent.openFeedbackActivity(BarWidgetConfigure.this);
		}
		else if ( item.getItemId() == R.id.menu_upgrade ) {
			MobclickAgent.update(BarWidgetConfigure.this);
		}
		else if ( item.getItemId() == R.id.menu_clear ) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			final int position = info.position;
			AppInfo app = (AppInfo)appListView.getItemAtPosition(position);
			Builder builder = new Builder(this);
			String msg = String.format(getString(R.string.msg_remove_app), app.name);
			builder.setTitle(R.string.title_remove_app).setMessage(msg);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					appListView.removeApp(position);
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		else if ( item.getItemId() == R.id.menu_set_icon ) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
//			intent.putExtra("crop", "true");
//			intent.putExtra("aspectX", 1);
//			intent.putExtra("aspectY", 1);
//			intent.putExtra("outputX", 72);
//			intent.putExtra("outputY", 72);
//			intent.putExtra("return-data", true);
			startActivityForResult(Intent.createChooser(intent, ""), REQUEST_CHOOSE_IMAGE+info.position);
			
		} 
		return true;
	}
	
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    if ( cursor != null ) {
	    	int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    	cursor.moveToFirst();
	    	return cursor.getString(column_index);
	    }
	    else {
	    	return uri.getPath();
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode >= REQUEST_CHOOSE_IMAGE  && requestCode <= REQUEST_CHOOSE_IMAGE+getPrograms()) {
			if ( data != null ) {
				int appIndex = requestCode-REQUEST_CHOOSE_IMAGE;
				if ( appIndex < 0 ) return;
				Uri uri = data.getData();
				String iconFile = getPath(uri);
				appListView.setAppIcon(appIndex, iconFile);
//				Log.i("URI", String.valueOf(uri));
//				for (String key: data.getExtras().keySet() ){
//					Log.i("extra", key+":"+data.getExtras().get(key));
//				}
//				Bitmap bitmap = (Bitmap)data.getExtras().get("data");
//				File dir = getExportDir(this);
//				File file;
//				file = new File(dir, "icon"+mAppWidgetId+"x"+appIndex+".png");
//				FileOutputStream out = null;
//				try {
//					out = new FileOutputStream(file);
//					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//				}
//				catch(Throwable ex){
//					Toast.makeText(this, getString(R.string.error_save_icon), 2);
//				}
//				finally {
//					try { out.close(); } catch(Throwable ex){}
//				}
			}
		}
	}

	public static File getExportDir(Context context){
		File dir = null;
		if ( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ){
			dir = new File(Environment.getExternalStorageDirectory(), "Android/Data/cellwidget");
		}
		else {
			dir = context.getFilesDir();
		}
		if ( dir != null && !dir.exists() ) dir.mkdirs();
		return dir;
	}

	public void onUpdateReturned(int returnCode) {
		int resIds[] = new int[]{R.string.msg_have_update, R.string.msg_no_update, R.string.msg_not_wifi, R.string.msg_update_timeout};
		if ( returnCode < 0 || returnCode >= 3 ) return;
		Toast.makeText(this, getString(resIds[returnCode]), 3).show();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if( appListView == v ) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.app_menu, menu); 
			MenuItem item;
			item = menu.findItem(R.id.menu_clear);
			item.setOnMenuItemClickListener(this);
			item = menu.findItem(R.id.menu_set_icon);
			item.setOnMenuItemClickListener(this);
		}
	}
}
