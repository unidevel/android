
package com.unidevel.whereareyou;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMQuery;
import com.unidevel.whereareyou.model.Position;
import com.unidevel.whereareyou.model.User;

public class LocateService extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener 
{
    private static final int MILLISECONDS_PER_SECOND = 1000;

	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	LocationClient client;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );

		if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            client = new LocationClient(this, this, this);
            client.connect();
		}
		else
		{
			Toast.makeText( this, "Google Play services is not available.", Toast.LENGTH_LONG ).show();
		}

	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		return super.onStartCommand( intent, flags, startId );
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.client.disconnect();
	}

	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}

	@Override
	public void onConnectionFailed( ConnectionResult result )
	{
		
	}

	@Override
	public void onConnected( Bundle connectionHint )
	{
		LocationRequest request = LocationRequest.create();
        // Use high accuracy
        request.setPriority(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the update interval to 5 seconds
        request.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        request.setFastestInterval(FASTEST_INTERVAL);
        
        client.requestLocationUpdates( request, this );
	}
	Position position;
	@Override
	public void onLocationChanged( Location location )
	{
		Log.i( "Location", "("+location.getLatitude()+","+
				location.getLongitude()+"),Accuracy="+location.getAccuracy()+",Speed="+location.getSpeed()+
				"time="+location.getTime()+",altitude="+location.getAltitude()+",Bearing="+location.getBearing());
				//",elapsedTime="+location.getElapsedRealtimeNanos());
		BlueListApplication app = (BlueListApplication)this.getApplication();
		User user = app.getCurrentUser();
		if ( user != null && user.getObjectId() != null )
		{
			if ( position == null )
			{
				try
				{
					IBMQuery<Position> query = IBMQuery.queryForClass( Position.class );
					query.whereKeyEqualsTo( "uid", user.getObjectId() );
					//query.findObjectsInBackground )
				}
				catch (IBMDataException e)
				{
				}
			}
			else
			{
				position.setLat( location.getLatitude() );
				position.setLng( location.getLongitude() );
				position.setAccuracy( location.getAccuracy() );
				position.setTime( location.getTime() );
				position.setUserId( user.getObjectId() );
			}
		}
	}

	@Override
	public void onDisconnected()
	{
		client.removeLocationUpdates( this );
	}


}
