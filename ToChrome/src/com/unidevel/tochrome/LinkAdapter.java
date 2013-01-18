package com.unidevel.tochrome;

import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.util.*;
import android.text.*;

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

	public List<String> getLinks()
	{
		return links;
	}

	public void deleteSelected()
	{
		if(selected<0||selected>=links.size()){
			return;
		}
		this.links.remove(selected);
		selected=-1;
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
			view.setOnClickListener(new View.OnClickListener(){

					public void onClick(View view)
					{
						selected = pos;
						notifyDataSetChanged();
						Log.i("tochrome","selected="+pos);
					}
			});
		}
		TextView text=(TextView)view.findViewById(android.R.id.text1);
		text.setSingleLine(true);
		if(pos==selected){
			text.setBackgroundResource(R.color.DarkOrange);
			text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			text.setMarqueeRepeatLimit(-1);
			text.setSelected(true);
		}
		else{
			text.setSelected(false);
			text.setBackgroundResource(R.color.Black);
			text.setEllipsize(TextUtils.TruncateAt.END);
			text.setMarqueeRepeatLimit(0);
		}
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
