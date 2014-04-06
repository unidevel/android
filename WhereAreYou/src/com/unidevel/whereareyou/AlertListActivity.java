package com.unidevel.whereareyou;

import android.os.Bundle;
import com.unidevel.BaseActivity;
import java.util.*;
import com.unidevel.whereareyou.model.*;
import android.widget.*;
import android.view.*;

public class AlertListActivity extends BaseActivity
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
			return 0;
		}

		@Override
		public boolean hasStableIds()
		{
			return false;
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
			TextView v1=(TextView)view.findViewById(R.id.type);
			TextView v2=(TextView)view.findViewById(R.id.info);
			MarkerInfo m=(MarkerInfo)this.getChild(p1,p2);
			return view;
		}

		@Override
		public boolean isChildSelectable(int p1, int p2)
		{
			return false;
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.data = new LinkedHashMap<String,List<MarkerInfo>>();
		BlueListApplication app = (BlueListApplication)getApplication();
		User my = app.getCurrentUser();
		for(MarkerInfo m:app.getMarkers()){
			List<MarkerInfo> ml=data.get(m.userName);
			if(ml==null){
				ml=new ArrayList<MarkerInfo>();
				data.put(m.userName,ml);
			}
			ml.add(m);
		}
		setContentView(R.layout.alert_list);
		this.inflater = LayoutInflater.from(this);
		ExpandableListView list=(ExpandableListView)this.findViewById(R.id.list);
		list.setAdapter(new AlertAdapter());
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
}
