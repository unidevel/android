package com.unidevel.devicemod;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.google.ads.*;
import java.io.*;
import java.util.*;
import android.opengl.*;

public class MainActivity extends Activity implements View.OnClickListener
{

	final MainActivity me=this;

	private static final int LABEL_ID = 1;

	private static final int NEWVALUE_ID = 2;

	private static final int OLDVALUE_ID = 3;

	private static final int EDIT_ID = 4;

	private static final int APPLY_ID = 5;
	
	private static final int SAVE_BUTTON_ID = 6;

	private static final int REFRESH_ID = 7;

	private static final int EXIT_ID = 8;
	
	private DeviceListAdapter adapter;
	
	private ListView deviceInfoList;
	
	ActionBar.Tab tabSystem;
	ActionBar.Tab tabDevice;
	
	class SystemFragment extends Fragment
	{
		
	}
	
	class DeviceFragment extends Fragment
	{
		
	}
	
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		/*
		LinearLayout root = new LinearLayout(me);
		root.setOrientation(LinearLayout.VERTICAL);
		this.setContentView(root);

		LinearLayout adLayout = new LinearLayout(me);
		adLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		adLayout.setOrientation(LinearLayout.HORIZONTAL);
		root.addView(adLayout, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		AdView adView = new AdView(this, AdSize.BANNER, " a1505186784b936"); 
		adLayout.addView(adView); 
		AdRequest req = new AdRequest(); 
		adView.loadAd(req);

		this.deviceInfoList = new ListView(me);
		this.adapter = new DeviceListAdapter();
		loadDeviceInfo();
		root.addView(deviceInfoList, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		deviceInfoList.setAdapter(this.adapter);
		*/
		ActionBar bar=this.getActionBar();	
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.tabSystem=bar.newTab().setText("System");
		this.tabDevice=bar.newTab().setText("Device");
		bar.addTab(tabSystem);
		bar.addTab(tabDevice);
	//	tabSystem.setTabListener(this);
		tabSystem.setTabListener(new TabListener<SystemFragment>(this,"System",SystemFragment.class));
		tabDevice.setTabListener(new TabListener<DeviceFragment>(this,"Device",DeviceFragment.class));
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, SAVE_BUTTON_ID, Menu.NONE, r(R.string.save));
		menu.add(Menu.NONE, REFRESH_ID, Menu.NONE, r(R.string.refresh));
		menu.add(Menu.NONE, EXIT_ID, Menu.NONE, r(R.string.exit));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == SAVE_BUTTON_ID)
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
	
	public void onClick(View view)
	{
		DeviceInfoItem item;
		switch (view.getId())
		{
			case SAVE_BUTTON_ID:
				saveDeviceInfo();
				return;
			case EDIT_ID:
				item = (DeviceInfoItem) view.getTag();
				showEditDialog(item, item.getValue());
				break;
			case APPLY_ID:
				item = (DeviceInfoItem) view.getTag();
				showEditDialog(item, item.getOldValue());
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
			//adapter.load(file);
			//adapter.notifyDataSetChanged();
			showMessage("Succeeded!Save to file " + file.getPath());
		}
		catch (Exception ex)
		{
			showError("Failed!Can't save to file " + file.getPath());
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
		Toast.makeText(me, msg, Toast.LENGTH_LONG).show();
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

	private void showEditDialog(final DeviceInfoItem item, String value)
	{
		try
		{
			installBinary();
		}
		catch (IOException e)
		{
			showError("Install binary file failed!Maybe not rooted!");
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
					item.setNewValue(newValue);
					try
					{
						item.edit();
						Thread.sleep(2000);
						me.adapter.notifyDataSetChanged();
						showMessage("Set " + item.getLabel() + " to " + newValue);
					}
					catch (Exception e)
					{
						showError("Set " + item.getLabel() + " error!");
						return;
					}
					//Intent intent=new Intent(me,me.getClass());
					//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
					//startActivity(intent);
					//me.finish();
					//System.exit(0);
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
	
	class DeviceListAdapter extends BaseAdapter
	{
		DeviceInfoItem[] items=new DeviceInfoItem[]
		{new AndroidIdItem(), new GoogleServiceIdItem(),
			new SubscriberIdItem(),new DeviceIdItem(),
			new SerialNoItem(),new MacAddressItem()};
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
			DeviceInfoItem item=(DeviceInfoItem)getItem(index);
			((TextView)view.findViewById(LABEL_ID)).setText(item.getLabel());
			((TextView)view.findViewById(NEWVALUE_ID)).setText(r(R.string.current, s(item.getValue())));
			((TextView)view.findViewById(OLDVALUE_ID)).setText(r(R.string.saved, s(item.getOldValue())));
			View editButton=view.findViewById(EDIT_ID);
			//editButton.setVisibility(item.canEdit()?View.VISIBLE:View.GONE);
			editButton.setTag(item);
			editButton.setOnClickListener(me);
			editButton.setVisibility(item.canEdit() ?View.VISIBLE: View.GONE);
			View applyButton=view.findViewById(APPLY_ID);
			//applyButton.setEnabled(item.canApply());
			applyButton.setTag(item);
			applyButton.setOnClickListener(me);
			applyButton.setVisibility(item.canApply() ?View.VISIBLE: View.GONE);

			return view;
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

		public void save(File file) throws FileNotFoundException
		{
			Properties prop = new Properties();
			FileOutputStream out=null;
			try
			{
				out = new FileOutputStream(file);
				for (DeviceInfoItem item:items)
				{
					prop.put(item.getKey(), s(item.getValue()));
				}
				prop.save(out, "");
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
				for (DeviceInfoItem item:items)
				{
					item.setOldValue(prop.getProperty(item.getKey()));
				}
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
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 2);
		layout.addView(span, params);

		TextView label=new TextView(me);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(label, params);
		label.setTextSize(24.0f);
		label.setText("Label");
		label.setId(LABEL_ID);
		{
			LinearLayout subLayout = new LinearLayout(me);
			subLayout.setOrientation(LinearLayout.HORIZONTAL);
			subLayout.setPadding(20, 0, 10, 0);
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
	
	abstract class DeviceInfoItem
	{
		String oldValue;
		String newValue;
		public abstract String getLabel();
		public abstract String getKey();
		public DeviceInfoItem()
		{
			oldValue = "";
			newValue = "";
		}
		public String getOldValue()
		{
			return oldValue;
		}
		public void setOldValue(String oldValue)
		{
			this.oldValue = oldValue;
		}
		public String getNewValue()
		{
			return this.newValue;
		}
		public void setNewValue(String newValue)
		{
			this.newValue = newValue;
		}
		public abstract String getValue();
		public void apply() throws Exception
		{}
		public void edit() throws Exception
		{}
		public boolean canApply()
		{
			return oldValue != null && oldValue.length() > 0;
		}
		public boolean canEdit()
		{
			return true;
		}
	}

	class AndroidIdItem extends DeviceInfoItem
	{

		public String getLabel()
		{
			return r(R.string.android_id);
		}

		public String getKey()
		{
			return "android_id";
		}

		public String getValue()
		{
			return DeviceUtil.getAndroidId(me);
		}

		public void apply() throws Exception
		{
			DeviceUtil.setAndroidId(getOldValue());
		}

		public void edit() throws Exception
		{
			DeviceUtil.setAndroidId(getNewValue());
		}
	}
	class GoogleServiceIdItem extends DeviceInfoItem
	{

		public String getLabel()
		{
			return r(R.string.gsf_id);
		}

		public String getKey()
		{
			return "gsf_id";
		}

		public String getValue()
		{
			try
			{
				return DeviceUtil.getGoogleServiceId(me);
			}
			catch (Exception e)
			{}
			return null;
		}

		public void apply() throws Exception
		{
			DeviceUtil.setGoogleServiceId(getOldValue());
		}

		public void edit() throws Exception
		{
			DeviceUtil.setGoogleServiceId(getNewValue());
		}
	}

	class DeviceIdItem extends DeviceInfoItem
	{

		public String getKey()
		{
			return "device_id";
		}

		public String getLabel()
		{
			return r(R.string.device_id);
		}

		public String getValue()
		{
			return DeviceUtil.getDeviceId(me);
		}

		public boolean canEdit()
		{
			return false;
		}

		public boolean canApply()
		{
			return false;
		}
	}

	class SerialNoItem extends DeviceInfoItem
	{

		public String getKey()
		{
			return "serial_no";
		}

		public String getLabel()
		{
			return r(R.string.serial_no);
		}

		public String getValue()
		{
			try
			{
				return DeviceUtil.getSerialNo();
			}
			catch (Exception e)
			{}
			return null;
		}

		public void apply() throws Exception
		{
			DeviceUtil.setSerialNo(getOldValue());
		}

		public void edit() throws Exception
		{
			DeviceUtil.setSerialNo(getNewValue());
		}
	}

	class SubscriberIdItem extends DeviceInfoItem
	{

		public String getKey()
		{
			return "subscriber_id";
		}

		public String getLabel()
		{
			return r(R.string.subscriber_id);
		}

		public String getValue()
		{
			return DeviceUtil.getSubscriberId(me);
		}

		public boolean canEdit()
		{
			return false;
		}

		public boolean canApply()
		{
			return false;
		}
	}

	class MacAddressItem extends DeviceInfoItem
	{

		public String getKey()
		{
			return "mac_address";
		}

		public String getLabel()
		{
			return r(R.string.mac_address);
		}

		public String getValue()
		{
			return DeviceUtil.getMacAddress(me);
		}

		public boolean canEdit()
		{
			return true;
		}

		public boolean canApply()
		{
			return true;
		}

		public void apply() throws Exception
		{
			DeviceUtil.setMacAddress(getOldValue());
		}

		public void edit() throws Exception
		{
			DeviceUtil.setMacAddress(getNewValue());
		}
	}
}
