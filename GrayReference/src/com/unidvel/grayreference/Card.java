package com.unidvel.grayreference;

public class Card
{
	String name;
	int level;
	
	public Card(String name, int level)
	{
		this.name = name;
		this.level = level;
	}
	
	public int getColor()
	{
		int color = 0xFF000000 | (0xFF0000&(level<<16)) | (0xFF00&(level<<8)) | (level&0xFF);
		return color;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public String getName()
	{
		return this.name;
	}
}
