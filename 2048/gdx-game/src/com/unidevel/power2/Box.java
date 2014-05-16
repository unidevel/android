package com.unidevel.power2;

public class Box
{
	int index;
	int value;
	int score;
	boolean added;
	public Box next[];
	public Box prev[];
	
	public Box(int index){
		this.index = index;
		this.value =0;
		this.next = new Box[3];
		this.prev = new Box[3];
	}
	
	public void setNext(int dir, Box b){
		this.next[dir] = b;
		b.prev[dir] = this;
	}

	public Box getNext(int dir){
		return this.next[dir];
	}
	
	public boolean move(int d,boolean next){
		boolean moved=false;
		if(this.value==0)return moved;
		Box[] link=next?this.next:this.prev;
		Box n=link[d];
		if(n!=null)
		{
			if(n.value==0){
				Log.i("move (%d,%d) to (%d, %d)",
				  this.index,this.value,n.index,n.value);
				n.value=this.value;
				if(this.value>0)
					moved=true;
				this.value=0;
				n.move(d,next);
			}
			else if(n.value==this.value&&!n.added){
				Log.i("move (%d,%d) to (%d, %d)",
				  this.index,this.value,n.index,n.value);
				n.value+=this.value;
				score+=n.value;
				this.value=0;
				n.added=true;
				if(n.value>0)
					moved=true;
			}
		}
		return moved;
	}
		
	@Override
	public String toString()
	{
		return ""+this.value;
	}
}
