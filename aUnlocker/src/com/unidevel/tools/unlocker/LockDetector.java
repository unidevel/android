package com.unidevel.tools.unlocker;

public class LockDetector extends AbstractDetector
{
	static final float R_THRESHOLD = 10f;
	static final float R1_THRESHOLD = 165f;
	static final float R2_THRESHOLD = 180f;
	static final long INTERVAL=300;

	int count;
	int state;

	public LockDetector()
	{
		this.state = 0;
	}

	public void input(float x, float y, float z)
	{
		if (state == 0)
		{
			updateStamp();
		}
		if (z > R_THRESHOLD || z < -R_THRESHOLD)
		{
			updateStamp();
			state = 0;
			return;
		}
		long now=System.currentTimeMillis();
		if ((y > R1_THRESHOLD && y < R2_THRESHOLD)||(y > -R2_THRESHOLD && y < -R1_THRESHOLD))
		{
			if (state == 0)
			{
				updateStamp();
				state = 1;
				return;
			}
			else if (state==1){
				if(now-stamp>INTERVAL){
					state = 2;
					return;
				}
			}
			else state = 0;
		}
		else{
			state=0;
			updateStamp();
		}
	}
	public boolean isMatch()
	{
		return state >=2;
	}

	public String toString()
	{
		return "State:" + state;
	}
}
