package com.unidevel.logo;

import android.app.Activity;
import android.os.Bundle;

import com.unidevel.logo.view.ConsoleView;

public class LogoConsole extends Activity {
	ConsoleView view;
	Thread scriptThread; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.view = new ConsoleView(this);
		setContentView(view);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		scriptThread = new Thread(){
			@Override
			public void run() {
				view.runScript(getIntent().getStringExtra(Editor.SCRIPT));
			}
		};
		scriptThread.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if ( scriptThread.isAlive() ) {
			scriptThread.stop();
		}
		try {
			scriptThread.join();
		}
		catch(Throwable ex){}
	}
}
