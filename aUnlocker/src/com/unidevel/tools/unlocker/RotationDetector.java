package com.unidevel.tools.unlocker;
public class RotationDetector
{
	static final float R_THRESHOLD = 10f;
	static final float R1_THRESHOLD = -60f;
	static final float R2_THRESHOLD = -35f;
	static final long INTERVAL=1000;

	int count;
	int state;
	long stamp;

	public RotationDetector()
	{
		this.state = 0;
	}
	
	public void input(float x, float y, float z)
	{
		if (state == 0)
		{
			stamp = System.currentTimeMillis();
		}
		if (y > R_THRESHOLD || y < -R_THRESHOLD)
		{
			stamp = System.currentTimeMillis();
			state = 0;
			return;
		}
		long now=System.currentTimeMillis();
		if (z > R1_THRESHOLD && z < R2_THRESHOLD)
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
	/*
	public void input(float x, float y, float z)
	{
		if (state == 0)
		{
			stamp = System.currentTimeMillis();
		}
		if (y > R_THRESHOLD || y < -R_THRESHOLD)
		{
			stamp = System.currentTimeMillis();
			state = 0;
			return;
		}
		long now=System.currentTimeMillis();
		if (z > -R_THRESHOLD && z < R_THRESHOLD)
		{
			if (state == 0)
			{
				stamp = now;
				state = 1;
			}
			else if (state == 2)
			{
				if (now - stamp < INTERVAL)
					state = 3;
				else state = 0;
				stamp = now;
			}
			else if (now - stamp > INTERVAL)
			{
				state = 0;
				stamp = now;
			}
			return;
		}
		if (z > R1_THRESHOLD && z < R2_THRESHOLD)
		{
			if (now - stamp > INTERVAL)
			{
				state = 0;
				stamp = now;
				return;
			}
			if (state == 1)
			{
				state = 2;
				stamp = now;
			}
			else if (state == 3)
			{
				state = 4;
				stamp = now;
			}
			return;
		}
		if (now - stamp > INTERVAL)
		{
			state = 0;
			stamp = now;
		}
	}
	*/

	public boolean isMatch()
	{
		return state >=2;
	}

	public String toString()
	{
		return "State:" + state;
	}
}
