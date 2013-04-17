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
		if ( this.listener != null )
		{
			String url=(String)v.getTag();
			this.listener.onClick( url );
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
	String starUrl;
	public LinkAdapter(Context ctx, String starUrl, List<String> links)
	{
		this.ctx = ctx;
		this.links = links;
		this.starUrl=starUrl;
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
		return this.links;
	}
	
	public int getCount()
	{
		return this.links.size();
	}

	public Object getItem(int pos)
	{
		return this.links.get( pos );
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
		String url = this.links.get( pos );
		text.setText(url);
		int padding = d2p( 4 );
		text.setPadding( 0, padding, 0, padding );
		ImageView image = (ImageView)view.findViewById( R.id.imageView1 );
		if(pos>0){
			image.setVisibility(View.VISIBLE);
			image.setPadding( d2p( 24 * (pos-1) ), 0, 0, 0);
		}
		else{
			image.setVisibility(View.GONE);
		}
		
		ImageView star=(ImageView)view.findViewById(R.id.starView);
		if(url!=null&&url.equals(starUrl)){
			star.setImageResource(android.R.drawable.btn_star_big_on);
		}
		else{
			star.setImageResource(android.R.drawable.btn_star);
		}
		star.setOnClickListener(this);
		star.setTag(url);
		return view;
	}
	
	public int d2p( float dipValue )
	{
		final float scale = this.ctx.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	public float p2d( int pxValue )
	{
		final float scale = this.ctx.getResources().getDisplayMetrics().density;
		return (float)pxValue / scale + 0.5f;
	}

	public void deleteLink(String link){
		this.links.remove(link);
		this.notifyDataSetChanged();
	}
}
