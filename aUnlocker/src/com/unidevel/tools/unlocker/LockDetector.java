package com.unidevel.tools.unlocker;

public class LockDetector
{
	static final float R_THRESHOLD = 10f;
	static final float R1_THRESHOLD = 55f;
	static final float R2_THRESHOLD = 75f;
	static final long INTERVAL=1000;

	int count;
	int state;
	long stamp;

	public LockDetector()
	{
		this.state = 0;
	}

	public void input(float x, float y, float z)
	{
		if (state == 0)
		{
			stamp = System.currentTimeMillis();
		}
		if (z > R_THRESHOLD || z < -R_THRESHOLD)
		{
			stamp = System.currentTimeMillis();
			state = 0;
			return;
		}
		long now=System.currentTimeMillis();
		if ((y > R1_THRESHOLD && y < R2_THRESHOLD)||(y > -R2_THRESHOLD && y < -R1_THRESHOLD))
		{
			if (state == 0)
			{
				stamp = now;
				state = 1;
				return;
			}
			else if (state==1){
				if(now-stamp>INTERVAL){
					state = 2;
					return;
				}
			}
		}
		else{
			state=0;
			stamp=now;
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
