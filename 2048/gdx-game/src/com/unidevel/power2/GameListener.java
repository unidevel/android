package com.unidevel.power2;

import java.io.File;

public interface GameListener
{
	public void onGameOver(File screenShot);
	
	public void onGamePause();
	
	public void onGameResume();
}
