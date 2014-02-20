package com.unidevel.jsinjector;
import android.widget.*;

public class Source
{
	TextView view;
	public Source(TextView view){
		this.view = view;
	}
	
	public void set(final String msg){
		Runnable r= new Runnable(){

			@Override
			public void run()
			{
				Source.this.view.setText(msg);
			}

			
		};
		this.view.post(r);
	}
	
	public void clear(){
		Runnable r= new Runnable(){

			@Override
			public void run()
			{
				Source.this.view.setText("");
			}


		};
		this.view.post(r);
		
	}
}
