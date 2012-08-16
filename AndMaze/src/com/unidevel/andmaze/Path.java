package com.unidevel.andmaze;

import java.util.*;

public class Path
{
	public class Item{
		public int x;
		public int y;
		public long duration;
	}
	long lastMoveTime;
	List<Item> items;
	
	public Path(){
		items = new ArrayList<Item>();
	}
	
	public void addMove(int x, int y){
		Item item = new Item();
		item.x = x;
		item.y = y;
		item.duration = System.currentTimeMillis()-this.lastMoveTime;
		items.add(item);
		this.lastMoveTime = System.currentTimeMillis();
	}
	
	public void resume(){
		this.lastMoveTime=System.currentTimeMillis();
	}
	
	public Iterator<Item> iterator(){
		return items.iterator();
	}
}
