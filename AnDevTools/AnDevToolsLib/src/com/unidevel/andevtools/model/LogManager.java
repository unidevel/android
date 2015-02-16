package com.unidevel.andevtools.model;

public class LogManager {
	public static ILog log = null;
	public static synchronized ILog getLog()
	{
		if ( log == null )
		{
			log = new DefaultLog();
		}
		return log;
	}
	
	
}
