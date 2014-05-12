package com.unidevel.power2;

public class Box
{
	int value;
	public Box next[];
	public Box prev[];
	
	public Box(int value){
		this.value =value;
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
	
	public void move(int d,boolean next){
		if(this.value==0)return;
		Box[] link=next?this.next:this.prev;
		Box n=link[d];
		if(n!=null)
		{
			if(n.value==this.value){
				n.value+=this.value;
				this.value=0;
			}
			else if(n.value==0){
				n.value=this.value;
				this.value=0;
				n.move(d,next);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return ""+this.value;
	}
}
