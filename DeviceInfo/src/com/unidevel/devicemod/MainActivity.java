package com.unidevel.devicemod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends Activity implements View.OnClickListener
{
	final MainActivity me=this;

	private static final int LABEL_ID = 1;

	private static final int NEWVALUE_ID = 2;

	private static final int OLDVALUE_ID = 3;

	private static final int EDIT_ID = 4;

	private static final int APPLY_ID = 5;

	private static final int SAVE_ID = 6;

	private static final int REFRESH_ID = 7;

	private static final int EXIT_ID = 8;

	private DeviceListAdapter adapter;

	private ListView deviceInfoList;

	public void onClick(View view)
	{
		AbstractDeviceInfoItem item;
		switch (view.getId())
		{
			case SAVE_ID:
				saveDeviceInfo();
				return;
			case EDIT_ID:
				item = (AbstractDeviceInfoItem) view.getTag();
				showEditDialog(item, item.getValue());
				break;
			case APPLY_ID:
				item = (AbstractDeviceInfoItem) view.getTag();
				showEditDialog(item, item.getSavedValue());
				break;
			case REFRESH_ID:
				this.adapter.notifyDataSetInvalidated();
				break;
		}
	}

	private void saveDeviceInfo()
	{
		File file =getSaveFile();
		try
		{
			adapter.save(file);
			adapter.load(file);
			showMessage(me.getString(R.string.save_success, file.getPath()));
		}
		catch (Exception ex)
		{
			showError(me.getString(R.string.save_failed,ex.getLocalizedMessage()));
		}
	}

	private void loadDeviceInfo()
	{
		try
		{
			adapter.load(getSaveFile());
		}
		catch (IOException e)
		{}
	}

	private void showError(String msg)
	{
		Builder builder = new Builder(this);
		builder.setTitle(R.string.error).
			setMessage(msg).setPositiveButton(android.R.string.ok, new Dialog.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}

	private void showMessage(String msg)
	{
		Toast.makeText(me, msg, Toast.LENGTH_LONG).show();
	}

	private File getSaveFile()
	{
		File file=new File(me.getExternalFilesDir(null), "info.txt");
		return file;
	}
	
	private void showEditDialog(final AbstractDeviceInfoItem item, String value)
	{
		try
		{
			installBinary();
		}
		catch (IOException e)
		{
			showError(me.getString(R.string.install_binary_failed));
			return;
		}
		AlertDialog.Builder builder=new AlertDialog.Builder(me);
		final EditText text=new EditText(me);
		text.setText(value);
		builder.setCancelable(true).setTitle(item.getLabel())
			.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int button)
				{
					String newValue=text.getText().toString();
					dialog.dismiss();
					try
					{
						item.setValue(newValue);
						showMessage(me.getString(R.string.set_success));
					}
					catch (Exception e)
					{
						showError(me.getString(R.string.set_failed, e.getLocalizedMessage()));
						return;
					}
					PendingIntent intent = PendingIntent.getActivity(me, 0, new Intent(getIntent()), getIntent().getFlags());
					AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, intent);
					System.exit(0);
				}
			})
			.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int button)
				{
					text.setText("");
					dialog.dismiss();
				}
			})
			.setView(text)
			;
		builder.show();
	}

	private void installBinary() throws IOException
	{
		File file=new File(me.getFilesDir(), "sqlite3");
		DeviceUtil.SQLITE_PATH = file.getPath();
		if (file.exists())return;
		InputStream in=me.getAssets().open("sqlite3");
		OutputStream out=new FileOutputStream(file);
		byte[] buf=new byte[4096];
		int len;
		while ((len = in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
		in.close();out.close();
		String cmd="chmod 0755 " + file.getPath();
		RootUtil.run(cmd);

	}

	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);

		LinearLayout root = new LinearLayout(me);
		root.setOrientation(LinearLayout.VERTICAL);
		this.setContentView(root);

		LinearLayout adLayout = new LinearLayout(me);
		adLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		adLayout.setOrientation(LinearLayout.HORIZONTAL);
		root.addView(adLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		AdView adView = new AdView(this, AdSize.BANNER, " a1505186784b936"); 
		adLayout.addView(adView); 
		AdRequest req = new AdRequest(); 
		adView.loadAd(req);

		this.deviceInfoList = new ListView(me);
		this.adapter = new DeviceListAdapter();
		loadDeviceInfo();
		root.addView(deviceInfoList, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		deviceInfoList.setAdapter(this.adapter);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, SAVE_ID, Menu.NONE, r(R.string.save));
		menu.add(Menu.NONE, REFRESH_ID, Menu.NONE, r(R.string.refresh));
		menu.add(Menu.NONE, EXIT_ID, Menu.NONE, r(R.string.exit));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == SAVE_ID)
		{
			saveDeviceInfo();
		}
		else if (item.getItemId() == REFRESH_ID)
		{
			this.adapter.notifyDataSetChanged();
		}
		else if (item.getItemId() == EXIT_ID)
		{
			this.finish();
			System.exit(0);
		}
		return true;
	}

	class DeviceListAdapter extends BaseAdapter
	{
		AbstractDeviceInfoItem[] items=new AbstractDeviceInfoItem[]
		{new AndroidIdItem(me), new GoogleServiceIdItem(me),
			new SubscriberIdItem(me),new DeviceIdItem(me),
			new SerialNoItem(me),new MacAddressItem(me)};
		
		public int getCount()
		{
			return items.length;
		}

		public Object getItem(int index)
		{
			return items[index];
		}

		public long getItemId(int index)
		{
			return index;
		}

		public View getView(int index, View view, ViewGroup parent)
		{
			if (view == null)
			{
				view = createView(parent);
			}
			AbstractDeviceInfoItem item=(AbstractDeviceInfoItem)getItem(index);
			((TextView)view.findViewById(LABEL_ID)).setText(item.getLabel());
			((TextView)view.findViewById(NEWVALUE_ID)).setText(r(R.string.current, s(item.getValue())));
			((TextView)view.findViewById(OLDVALUE_ID)).setText(r(R.string.saved, s(item.getSavedValue())));
			View editButton=view.findViewById(EDIT_ID);
			editButton.setTag(item);
			editButton.setOnClickListener(me);
			View applyButton=view.findViewById(APPLY_ID);
			applyButton.setTag(item);
			applyButton.setOnClickListener(me);
			if ( item.supportEdit() )
			{
				editButton.setVisibility(View.VISIBLE);
				applyButton.setVisibility(View.VISIBLE);
				applyButton.setEnabled(!isEmpty(item.getSavedValue()));				
			}
			else
			{
				editButton.setVisibility(View.GONE);
				applyButton.setVisibility(View.GONE);				
			}
			return view;
		}
		
		protected boolean isEmpty(String s)
		{
			return s==null || s.length() == 0;
		}

		public int getViewTypeCount()
		{
			return 1;
		}

		public int getItemViewType(int position)
		{
			return 0;
		}

		public DeviceListAdapter()
		{

		}

		public void save(File file) throws IOException
		{
			Properties prop = new Properties();
			FileOutputStream out=null;
			try
			{
				out = new FileOutputStream(file);
				for (AbstractDeviceInfoItem item:items)
				{
					prop.put(item.getKey(), s(item.getValue()));
				}
				prop.store(out, "");
			}
			finally
			{
				try
				{
					out.close();
				}
				catch (Exception e)
				{}
			}
		}

		public void load(File file) throws FileNotFoundException, IOException
		{
			Properties prop=new Properties();
			FileInputStream in=null;
			try
			{
				in = new FileInputStream(file);
				prop.load(in);
				for (AbstractDeviceInfoItem item:items)
				{
					item.setSavedValue(prop.getProperty(item.getKey()));
				}
				this.notifyDataSetInvalidated();
			}
			finally
			{
				try
				{
					in.close();
				}
				catch (Exception e)
				{}
			}
		}
	}
	
	private String r(int resourceId){
		return this.getString(resourceId);
	}
	
	private String r(int resourceId, Object... args){
		return this.getString(resourceId,args);
	}

	private String s(Object o)
	{
		if (o == null)return "";
		return o.toString();
	}

	private View createView(ViewGroup root)
	{
		LinearLayout layout = new LinearLayout(me);
		layout.setOrientation(LinearLayout.VERTICAL);
		//root.addView(layout, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		View span = new View(me);
		span.setBackgroundColor(0xFF000090);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
		layout.addView(span, params);

		TextView label=new TextView(me);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(label, params);
		label.setTextSize(24.0f);
		label.setText("Label");
		label.setId(LABEL_ID);
		{
			LinearLayout subLayout = new LinearLayout(me);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);
			subLayout.setPadding(20, 0, 10, 0);
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(subLayout, params);

			label = new TextView(me);
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
			subLayout.addView(label, params);
			label.setTextSize(18.0f);
			label.setId(NEWVALUE_ID);
			label.setText(r(R.string.current));

			Button button = new Button(me);
			button.setText(r(R.string.edit));
			button.setId(EDIT_ID);
			params = new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT);
			subLayout.addView(button, params);
		}

		{
			LinearLayout subLayout = new LinearLayout(me);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);
			subLayout.setPadding(20, 0, 10, 0);
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(subLayout, params);

			label = new TextView(me);
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
			subLayout.addView(label, params);
			label.setTextSize(18.0f);
			label.setId(OLDVALUE_ID);
			label.setText(r(R.string.saved));

			Button button = new Button(me);
			button.setText(r(R.string.apply));
			button.setId(APPLY_ID);
			params = new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT);
			subLayout.addView(button, params);
		}
		return layout;
	}
}
