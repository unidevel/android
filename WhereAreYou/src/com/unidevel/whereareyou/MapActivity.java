
package com.unidevel.whereareyou;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unidevel.BaseActivity;

public class MapActivity extends BaseActivity implements GoogleMap.OnMapLongClickListener,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener
{
	Map<String, MarkerInfo> markers;

	@Override
	public void onInfoWindowClick( final Marker m )
	{
		String id = m.getId();
		MarkerInfo i = markers.get( id );
		if ( i == null )
			return;

		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		LayoutInflater inflater = LayoutInflater.from( this );
		View view = inflater.inflate( R.layout.marker, null, false );
		final EditText titleText = (EditText)view.findViewById( R.id.title );
		titleText.setText( i.title );

		final SeekBar rangeBar = (SeekBar)view.findViewById( R.id.radius );
		rangeBar.setProgress( (int)(i.radius * 100.0) );
		builder.setView( view );
		builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				String title = titleText.getText().toString();
				int radius = rangeBar.getProgress();
				dialog.dismiss();
			}
		} );
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onMarkerClick( Marker m )
	{

		return false;
	}

	List<Circle> circles;
	Intent serviceIntent = new Intent( "LOCATE_SERVICE" );

	@Override
	public void onMapLongClick( LatLng p )
	{
		CircleOptions circleOptions = new CircleOptions().center( p ).radius( 1000 ); // In
																						// meters

		// Get back the mutable Circle
		// Circle circle = mMap.addCircle(circleOptions);
		Marker m =
				mMap.addMarker( new MarkerOptions().position( p ).title( "Hello world" ).flat( true ).draggable( true ) );
		MarkerInfo i = new MarkerInfo();
		i.title = "Position";
		i.id = m.getId();
		i.lat = p.latitude;
		i.lng = p.longitude;
		i.radius = this.getDefaultRadius();
		i.marker = m;
		markers.put( i.id, i );
		// m.showInfoWindow();
	}

	protected double getDefaultRadius()
	{
		return .5;
	}

	private GoogleMap mMap;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.map );
		this.markers = new HashMap<String, MarkerInfo>();
		this.mMap = ((MapFragment)getFragmentManager().findFragmentById( R.id.map )).getMap();
		this.mMap.getUiSettings().setCompassEnabled( true );
		this.mMap.setMyLocationEnabled( true );
		this.mMap.setOnMapLongClickListener( this );
		// this.mMap.setOnMarkerClickListener(this);
		this.mMap.setOnInfoWindowClickListener( this );

		LocationManager locationManager = (LocationManager)getSystemService( Context.LOCATION_SERVICE );
		Criteria criteria = new Criteria();

		Location location = locationManager.getLastKnownLocation( locationManager.getBestProvider( criteria, false ) );
		if ( location != null )
		{
			mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(
					new LatLng( location.getLatitude(), location.getLongitude() ), 13 ) );

			CameraPosition cameraPosition =
					new CameraPosition.Builder().target( new LatLng( location.getLatitude(), location.getLongitude() ) ) 
							.zoom( 17 ) // Sets the zoom
							//.bearing( 90 ) // Sets the orientation of the camera to east
							//.tilt( 40 ) // Sets the tilt of the camera to 30 degrees
							.build(); // Creates a CameraPosition from the builder
			mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

		}

		this.startService( serviceIntent );
		Intent intent = new Intent( this, LogonActivity.class );
		this.startActivityForResult( intent, 1 );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		if ( resultCode != RESULT_OK )
		{
			this.finish();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.stopService( serviceIntent );
	}

	public double CalculationByDistance( LatLng StartP, LatLng EndP )
	{
		int Radius = 6371;// radius of earth in Km
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians( lat2 - lat1 );
		double dLon = Math.toRadians( lon2 - lon1 );
		double a =
				Math.sin( dLat / 2 ) * Math.sin( dLat / 2 ) + Math.cos( Math.toRadians( lat1 ) )
						* Math.cos( Math.toRadians( lat2 ) ) * Math.sin( dLon / 2 ) * Math.sin( dLon / 2 );
		double c = 2 * Math.asin( Math.sqrt( a ) );
		double valueResult = Radius * c;
		double km = valueResult / 1;
		DecimalFormat newFormat = new DecimalFormat( "####" );
		int kmInDec = Integer.valueOf( newFormat.format( km ) );
		double meter = valueResult % 1000;
		int meterInDec = Integer.valueOf( newFormat.format( meter ) );
		Log.i( "Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec );

		return Radius * c;
	}
}
