package com.unidevel.power2;

public class Box
{
	int index;
	int value;
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
			if(n.value==this.value){
				t("move (%d,%d) to (%d, %d)",
				  this.index,this.value,n.index,n.value);
				n.value+=this.value;
				this.value=0;
				if(n.value>0)
					moved=true;
			}
			else if(n.value==0){
				t("move (%d,%d) to (%d, %d)",
				  this.index,this.value,n.index,n.value);
				n.value=this.value;
				this.value=0;
				moved=n.move(d,next);
			}
		}
		return moved;
	}
	
	void t(String fmt, Object... args){
		String s=String.format(fmt, args);
		System.out.println(s);
	}
	
	@Override
	public String toString()
	{
		return ""+this.value;
	}
}
