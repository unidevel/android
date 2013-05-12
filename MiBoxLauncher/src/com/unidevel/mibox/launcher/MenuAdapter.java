package com.unidevel.mibox.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	public static final int ID_REMOTER = 1;
	public static final int ID_SEARCH = 2;
	public static final int ID_FC = 3;
	public static final int ID_PAD = 4;
	public static final int ID_REFRESH = 5;
	public static final int ID_ABOUT = 6;
	public static final int ID_HOME = 7;
	class MenuItem
	{
		int id;
		int icon;
		String label;
		public MenuItem(int id, int icon, String label)
		{
			this.id = id;
			this.icon = icon;
			this.label = label;
		}
	}
	
	List<MenuItem> items;
	LayoutInflater inflater;
	Context context;
	public MenuAdapter(Context context)
	{
		super();
		this.context = context;
		items = new ArrayList<MenuAdapter.MenuItem>();
		MenuItem item;
		item = new MenuItem(ID_REMOTER, R.drawable.menu_mi, "Remoter");
		items.add(item);
		item = new MenuItem(ID_HOME, R.drawable.tv, "My Home");
		items.add(item);
		item = new MenuItem(ID_REFRESH, android.R.drawable.ic_popup_sync, "Refresh");
		items.add(item);
		item = new MenuItem(ID_ABOUT, android.R.drawable.ic_dialog_info, "About");
		items.add(item);
//		item = new MenuItem(ID_PAD, R.drawable.menu_pad, "Pad");
//		items.add(item);
//		item = new MenuItem(ID_FC, R.drawable.menu_fc, "Fc");
//		items.add(item);
		
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		if ( view == null )
		{
			view = this.inflater.inflate( R.layout.app, null );
		}
		ImageView icon = (ImageView)view.findViewById( R.id.icon );
		TextView label = (TextView)view.findViewById( R.id.label );
		MenuItem item = this.items.get( pos );
		icon.setImageDrawable(context.getResources().getDrawable(item.icon));
		label.setText(item.label);
		return view;
	}
	
	public int getId(int pos)
	{
		return items.get(pos).id;
	}
}
