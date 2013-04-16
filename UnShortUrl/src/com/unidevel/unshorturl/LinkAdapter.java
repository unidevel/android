package com.unidevel.unshorturl;

import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LinkAdapter extends BaseAdapter implements View.OnClickListener
{

	public void onClick(View v)
	{
		if(listener!=null){
			String url=(String)v.getTag();
			listener.onClick(url);			
		}
	}


	public interface StarClickListener
	{
		public void onClick(String url);
	}
	
	List<String> links;
	Context ctx;
	LayoutInflater inflater;
	StarClickListener listener;
	
	public LinkAdapter(Context ctx,List<String> links)
	{
		this.ctx = ctx;
		this.links = links;
		this.inflater = LayoutInflater.from(ctx);
	}
	

	public void removeOnStarClickListener(StarClickListener listener){
		this.listener=null;
	}
	
	
	public void setOnStarClickListener(StarClickListener listener){
		this.listener=listener;
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
		String url=links.get(pos);
		text.setText(url);
		int padding = d2p( 4 );
		text.setPadding( 0, padding, 0, padding );
		ImageView image = (ImageView)view.findViewById( R.id.imageView1 );
		if(pos>0){
			image.setVisibility(View.VISIBLE);
			image.setPadding( d2p( 24 * (pos-1) ), 0, 0, 0);
			//image.setImageResource(R.drawable.dot);
		}
		else{
			image.setVisibility(View.GONE);
		}
		
		ImageView star=(ImageView)view.findViewById(R.id.starView);
		star.setOnClickListener(this);
		star.setTag(url);
		return view;
	}
	
	public int d2p( float dipValue )
	{
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	public float p2d( int pxValue )
	{
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (float)pxValue / scale + 0.5f;
	}

	public void deleteLink(String link){
		this.links.remove(link);
		this.notifyDataSetChanged();
	}
}
