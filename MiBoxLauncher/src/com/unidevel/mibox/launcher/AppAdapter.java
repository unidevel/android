package com.unidevel.mibox.launcher;

import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter
{
	List<AppInfo> apps;
	int selected;
	Drawable defaultBackground;
	Context ctx;
	LayoutInflater inflater;

	public AppAdapter( Context ctx, List<AppInfo> apps )
	{
		this.ctx = ctx;
		this.apps = apps;
		this.selected = -1;
		for ( int index = 0; index < apps.size(); ++index )
		{
			AppInfo app = apps.get( index );
			if ( app.selected )
			{
				this.selected = index;
				break;
			}
		}
		this.inflater = LayoutInflater.from( ctx );
	}

	@Override
	public int getCount()
	{
		return this.apps.size();
	}

	@Override
	public Object getItem( int position )
	{
		return this.apps.get( position );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@SuppressWarnings ("deprecation")
	@Override
	public View getView( int position, View view, ViewGroup group )
	{
		if ( view == null )
		{
			view = this.inflater.inflate( R.layout.app, null );
			if ( this.defaultBackground == null )
			{
				this.defaultBackground = view.getBackground();
			}
		}
		ImageView icon = (ImageView)view.findViewById( R.id.icon );
		TextView label = (TextView)view.findViewById( R.id.label );
		AppInfo info = this.apps.get( position );
		if ( info.icon != null )
		{
			icon.setImageDrawable( info.icon );
		}
		if ( info.label != null )
		{
			label.setText( info.label );
		}
		if ( this.selected == position )
		{
			view.setBackgroundColor( 0x80b0b0b0 );
		}
		else
		{
			view.setBackgroundDrawable( this.defaultBackground );
		}
		return view;
	}

	public AppInfo getSelectedApp()
	{
		if ( this.selected < 0 || this.selected > this.apps.size() )
			return null;
		return this.apps.get( this.selected );
	}

	public void setSelected( int pos )
	{
		this.selected = pos;
		notifyDataSetChanged();
	}
	
	public int getSelected(){
		return this.selected;
	}

	public AppInfo getApp( int pos )
	{
		return this.apps.get( pos );
	}

	public List<AppInfo> getApps()
	{
		return apps;
	}
}
