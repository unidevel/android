package com.unidevel.whereareyou;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapActivity extends Activity
{
	private GoogleMap mMap;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.map);
		this.mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		this.mMap.getUiSettings().setCompassEnabled( true );
		this.mMap.getUiSettings().setMyLocationButtonEnabled( true );
	}
}
