package com.unidevel.tools.unlocker;

public class LockMotionDetector
{
	int count;
	int state;
	long stamp;

	public LockMotionDetector()
	{
		this.state = 0;
	}

	public void input(float x, float y, float z)
	{
		if (state == 0)
		{
			stamp = System.currentTimeMillis();
		}
	}
}
