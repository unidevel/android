package com.unidevel.whereareyou;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import java.text.*;
import android.util.*;
import java.util.*;
import com.unidevel.util.*;
import com.unidevel.*;

public class MapActivity extends BaseActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener
{

	@Override
	public void onInfoWindowClick(Marker m)
	{
		alert("hello");
	}


	@Override
	public boolean onMarkerClick(Marker m)
	{
		
		return false;
	}

	List<Circle> circles;
	List<Marker> markers;
	@Override
	public void onMapLongClick(LatLng p)
	{
		CircleOptions circleOptions = new CircleOptions()
			.center(p).radius(1000); // In meters

		// Get back the mutable Circle
		//Circle circle = mMap.addCircle(circleOptions);
		Marker m = mMap.addMarker(new MarkerOptions()
					   .position(p)
					   .title("Hello world")
					   .flat(true).draggable(true));
		this.markers.add(m);
		//m.showInfoWindow();
	}

	private GoogleMap mMap;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.map);
		
		this.markers = new ArrayList<Marker>();
		this.mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		this.mMap.getUiSettings().setCompassEnabled( true );
		this.mMap.setMyLocationEnabled( true );
		this.mMap.setOnMapLongClickListener(this);
		//this.mMap.setOnMarkerClickListener(this);
		this.mMap.setOnInfoWindowClickListener(this);
	}
	
	public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km         
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
			Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);

        return Radius * c;
	}
}
