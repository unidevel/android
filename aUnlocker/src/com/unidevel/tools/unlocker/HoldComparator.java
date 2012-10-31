package com.unidevel.tools.unlocker;

import java.util.*;

public class HoldComparator
{
	float min,max;
	public HoldComparator(float min, float max)
	{
		this.min = min;
		this.max = max;
	}
	
	public boolean match(float value, float matchValue, float delta)
	{
		float m1=matchValue+delta;
		float m2=matchValue-delta;
		if(value>m2&&value<m1)return true;
		if(m1>max){
			float m3=min+(m1-max);
			float m4=min;
			if(value>m4&&value<m3)return true;
		}
		if(m2<min){
			float m3=max;
			float m4=max+(m2-min);
			if(value>m4&&value<m3)return true;
		}
		return false;
	}
	
}
