package com.unidevel.andmaze;

import java.util.*;
import android.os.*;

public class Path
{

	public long getLastMoveTime(){
		return lastMoveTime;
	}
	
	public class Item{
		public int x;
		public int y;
		public long duration;
	}
	long lastMoveTime;
	long cost;
	List<Item> items;
	
	public Path(){
		items = new ArrayList<Item>();
		lastMoveTime=System.currentTimeMillis();
		cost=0;
	}
	
	public void addMove(int x, int y){
		Item item = new Item();
		item.x = x;
		item.y = y;
		item.duration = System.currentTimeMillis()-this.lastMoveTime;
		cost+=item.duration;
		items.add(item);
		this.lastMoveTime = System.currentTimeMillis();
	}
	
	public void pause(){
		this.cost+=System.currentTimeMillis()-lastMoveTime;
		lastMoveTime=System.currentTimeMillis();
	}
	
	public void resume(){
		this.lastMoveTime=System.currentTimeMillis();
	}
	
	public Iterator<Item> iterator(){
		return items.iterator();
	}
	
	public Bundle store(){
		Bundle bundle = new Bundle();
		int n= items.size();
		int x[]=new int[n];
		int y[]=new int[n];
		long d[]=new long[n];
		n=0;
		for(Item item:items){
			x[n]=item.x;y[n]=item.y;
			d[n]=item.duration;
			++n;
		}
		bundle.putIntArray("x",x);
		bundle.putIntArray("y",y);
		bundle.putLongArray("d",d);
		bundle.putLong("cost",cost);
		return bundle;
	}
	
	public void load(Bundle bundle){
		lastMoveTime=System.currentTimeMillis();
		cost=0;
		if(bundle==null)return;
		int x[]=bundle.getIntArray("x");
		int y[]=bundle.getIntArray("y");
		long[] d=bundle.getLongArray("d");
		cost=bundle.getLong("cost",0);
		if(x==null||y==null||d==null)return;
		for(int n = 0; n < x.length; n++){
			Item item = new Item();
			item.x=x[n];item.y=y[n];item.duration=d[n];
			cost+=d[n];
			items.add(item);
		}
	}
	
	public long getMoveTime()
	{
		return cost;
	}
	
	public int getMoveSteps(){
		return items.size();
	}
}
