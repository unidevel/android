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
	
	public LinkAdapter(Context ctx,List<String> links)
	{
		this.ctx = ctx;
		this.links = links;
		this.inflater = LayoutInflater.from(ctx);
	}

	public List<String> getLinks()
	{
		return links;
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
		}
		TextView text=(TextView)view.findViewById(android.R.id.text1);
		text.setSingleLine(true);
		text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		text.setText(links.get(pos));
		return view;
	}
	
	public void deleteLink(String link){
		this.links.remove(link);
		this.notifyDataSetChanged();
	}
}
