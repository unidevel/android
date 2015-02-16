package com.unidevel.andevtools.model;

public interface ILog {
	public void trace(String fmt, Object... args);
	public void warn(String fmt, Object... args);
	public void error(String fmt, Object... args);
	public void error(Throwable e);
	public void info(String fmt, Object... args);
}
