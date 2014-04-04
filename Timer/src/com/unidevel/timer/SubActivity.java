
package com.unidevel.timer;

import android.view.View;


public class SubActivity extends MainActivity 
{
	protected void addNewTimer()
	{
		this.btnAdd.setVisibility( View.GONE );
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if ( this.started )
		{
			this.onStartStop( this.btnStart );
		}
	}
	
}
