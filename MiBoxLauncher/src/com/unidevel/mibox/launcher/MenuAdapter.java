package com.unidevel.mibox.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MenuAdapter extends BaseAdapter {

	class MenuItem
	{
		int icon;
		String label;
		public MenuItem(int icon, String label)
		{
			this.icon = icon;
			this.label = label;
		}
	}
	
	List<MenuItem> items;
	
	public MenuAdapter(Context context)
	{
		items = new ArrayList<MenuAdapter.MenuItem>();
		MenuItem item;
		item = new MenuItem(R.drawable.menu_remoter, "");
	}
	
	@Override
	public int getCount() {
		return 0;
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
		return null;
	}
	
}
