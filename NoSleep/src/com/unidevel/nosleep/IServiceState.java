package com.unidevel.nosleep;

public interface IServiceState {
	boolean isStarted();
	void setTimeout(int minutes);
	int getTimeout();
	
	void setStartTime(int time);
	void setStopTime(int time);
	int getStartTime();
	int getStopTime();
	
	boolean saveSettings();
}
