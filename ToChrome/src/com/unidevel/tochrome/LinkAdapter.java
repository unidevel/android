package com.unidevel.tochrome;

import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.util.*;

public class LinkAdapter extends BaseAdapter
{
	List<String> links;
	Context ctx;
	LayoutInflater inflater;
	int selected;
	
	public LinkAdapter(Context ctx,List<String> links)
	{
		this.ctx = ctx;
		this.links = links;
		this.selected = -1;
		this.inflater = LayoutInflater.from(ctx);
	}

	public void deleteSelected()
	{
		if(selected<0||selected>=links.size()){
			return;
		}
		this.links.remove(selected);
		this.notifyDataSetChanged();
	}
	
	public int getCount()
	{
		return links.size();
	}

	public Object getItem(int pos)
	{
		return links.get(pos);
	}

	public long getItemId(int pos)
	{
		return pos;
	}

	public View getView(final int pos, View view, ViewGroup group)
	{
		if(view==null){
			view=this.inflater.inflate(R.layout.item,null);
			view.setClickable(true);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.setOnClickListener(new View.OnClickListener(){

					public void onClick(View view)
					{
						selected = pos;
						view.setSelected(true);
					//	((TextView)view)
						Log.i("tochrome","selected="+pos);
					}
			});
		}
		TextView text=(TextView)view.findViewById(android.R.id.text1);
		text.setText(links.get(pos));
		return view;
	}
	
	public String getSelectedLink(){
		if(selected<0||selected>=links.size()){
			return null;
		}
		return links.get(selected);
	}
	
	public void setSelected(int pos){
		this.selected=pos;
	}
}
