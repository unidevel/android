package com.unidevel.whereareyou;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.unidevel.whereareyou.model.User;

public class UserAdapter extends BaseAdapter
{
	Context context;
	List<User> users;
	LayoutInflater inflater; 
	class ViewTag
	{
		TextView label;
		ImageView icon;
	}
	
	UserAdapter(Context context, List<User> users)
	{
		this.context = context;
		this.users = users;
	}

	@Override
	public int getCount()
	{
		return users.size();
	}

	@Override
	public Object getItem( int position )
	{
		return users.get( position );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		if ( convertView == null )
		{
			if ( this.inflater == null )
			{
				this.inflater = LayoutInflater.from( context );
			}
			convertView = this.inflater.inflate( R.layout.user_item, null );
			ViewTag tag = new ViewTag();
			tag.label = (TextView)convertView.findViewById( R.id.user_name );
			tag.icon = (ImageView)convertView.findViewById( R.id.user_icon );
			convertView.setTag( tag );
		}
		ViewTag tag = (ViewTag)convertView.getTag();
		User user = users.get( position );
		tag.label.setText( user.getUserName() );
		return convertView;
	}
	
	public void addFriends(List<User> friends)
	{
		this.users.addAll( friends );
	}
}
