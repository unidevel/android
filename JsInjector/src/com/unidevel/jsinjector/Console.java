package com.unidevel.jsinjector;
import android.widget.*;

public class Console
{
	TextView view;
	public Console(TextView view){
		this.view = view;
	}
	
	public void log(final String msg){
		Runnable r= new Runnable(){

			@Override
			public void run()
			{
				Console.this.view.setText(msg);
			}

			
		};
		this.view.post(r);
	}
}
