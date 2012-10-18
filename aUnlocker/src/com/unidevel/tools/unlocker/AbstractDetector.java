package com.unidevel.tools.unlocker;

public abstract class AbstractDetector
{
	protected long stamp;
	
	public abstract void input(float x, float y, float z);
	public abstract boolean isMatch();
	public void setStamp(long value)
	{
		this.stamp=value;
	}
	public void updateStamp(){
		
		long value=System.currentTimeMillis();
		if (this.stamp<value){
			this.stamp=value;
		}
	}
}
