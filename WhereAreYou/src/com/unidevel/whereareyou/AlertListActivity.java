package com.unidevel.whereareyou;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.unidevel.whereareyou.model.User;

public class AlertListActivity extends ExpandableListActivity implements Constants, OnCheckedChangeListener, OnItemLongClickListener
{
	LayoutInflater inflater;
	LinkedHashMap<String, List<MarkerInfo>> data;
	class AlertAdapter extends BaseExpandableListAdapter
	{
		List<String> keys;
		public AlertAdapter(){
			this.keys = new ArrayList<String>();
			this.keys.addAll(data.keySet());
		}
		@Override
		public int getGroupCount()
		{
			return data.size();
		}
		
		@Override
		public int getChildrenCount(int p)
		{
			String key=keys.get(p);
			List<MarkerInfo> ml=data.get(key);
			return ml==null?0:ml.size();
		}

		@Override
		public Object getGroup(int p)
		{
			String key=keys.get(p);
			List<MarkerInfo> ml=data.get(key);
			
			return ml;
		}

		@Override
		public Object getChild(int p1, int p2)
		{
			String key=keys.get(p1);
			List<MarkerInfo> ml=data.get(key);
			if(ml!=null){
				return ml.get(p2);
			}
			return null;
		}

		@Override
		public long getGroupId(int p1)
		{
			return p1;
		}

		@Override
		public long getChildId(int p1, int p2)
		{
			return p2;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}

		@Override
		public View getGroupView(int p1, boolean expand, View view, ViewGroup p4)
		{
			if(view==null){
				view=inflater.inflate(R.layout.alert_group, null);
			}
			TextView u = (TextView)view.findViewById(R.id.user);
			u.setText(keys.get(p1));
			return view;
		}

		@Override
		public View getChildView(int p1, int p2, boolean isLast, View view, ViewGroup p5)
		{
			if(view==null){
				view=inflater.inflate(R.layout.alert_item, null);
			}
			CheckBox c =(CheckBox)view.findViewById(R.id.enabled );
			TextView v1=(TextView)view.findViewById(R.id.type);
			TextView v2=(TextView)view.findViewById(R.id.info);
			MarkerInfo m=(MarkerInfo)this.getChild(p1,p2);
			c.setChecked( m.enabled );
			c.setTag( m );
			c.setOnCheckedChangeListener( AlertListActivity.this );
			String enabledStr = getString(m.enabled?R.string.alert_list_enabled:R.string.alert_list_dsiabled);
			String typeStr = getString(TYPE_ENTER.equalsIgnoreCase( m.type )?R.string.alert_list_type_enter:R.string.alert_list_type_leave);
			String title = getString(R.string.alert_list_title_format, m.title, enabledStr, typeStr);
			v1.setText( title );
			String info = getString(R.string.alert_list_info_format, m.lat, m.lng, m.radius);
			v2.setText( info );
			view.setTag( m );
			return view;
		}

		@Override
		public boolean isChildSelectable(int p1, int p2)
		{
			return true;
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.data = new LinkedHashMap<String,List<MarkerInfo>>();
		BlueListApplication app = (BlueListApplication)getApplication();
		User my = app.getCurrentUser();
		List<MarkerInfo> markers = app.getMarkers();
		String userName = getIntent().getStringExtra( "username" );
		if ( false )
		{
			markers = new ArrayList<MarkerInfo>();
			for ( int i = 0; i  < 3; ++ i )
			{
				MarkerInfo m = new MarkerInfo();
				m.title = "Test"+i;
				m.uid = ""+i;
				m.userName = "user"+i;
				m.index = i;
				m.lat = 100* i;
				m.lng = 100* i;
				m.type = TYPE_ENTER;
				m.radius = 10*i;
				markers.add(m);
			}
		}
		for(MarkerInfo m: markers){
			if ( userName != null && userName.trim().length() > 0 )
			{
				if ( !userName.equals( m.userName ) )
					continue;
			}
			List<MarkerInfo> ml=data.get(m.userName);
			if(ml==null){
				ml=new ArrayList<MarkerInfo>();
				data.put(m.userName,ml);
			}
			ml.add(m);
		}
		//setContentView(R.layout.alert_list);
		this.inflater = LayoutInflater.from(this);
		this.getExpandableListView().setAdapter(new AlertAdapter());
		this.getExpandableListView().setLongClickable( true );
		this.getExpandableListView().setOnItemLongClickListener( this );
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onCheckedChanged( CompoundButton button, boolean isChecked )
	{
		MarkerInfo m = (MarkerInfo)button.getTag();
		if ( m == null )
			return;
		m.enabled = isChecked;
	}

	@Override
	public boolean onItemLongClick( AdapterView<?> adapterView, View view, int index, long id )
	{
		MarkerInfo m = (MarkerInfo) view.getTag();
		if ( m == null )
			return false;
		Intent intent = new Intent();
		intent.putExtra( "index", m.index );
		setResult( RESULT_OK, intent );
		this.finish();
		return false;
	}
}
