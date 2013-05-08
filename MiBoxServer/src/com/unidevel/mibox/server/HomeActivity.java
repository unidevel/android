
package com.unidevel.mibox.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class HomeActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.home );

		Intent i = new Intent(HomeService.SERVICE_ACTION);
		startService(i);
	}

}
