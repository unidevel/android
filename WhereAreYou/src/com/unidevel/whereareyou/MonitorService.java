
package com.unidevel.whereareyou;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.data.IBMQueryResult;
import com.unidevel.whereareyou.model.Position;
import com.unidevel.whereareyou.model.User;

public class MonitorService extends Service 
{
	User currentUser;
	@Override
	public void onCreate()
	{
		super.onCreate();
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
	}

	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}
	
	private void getCurrentUser()
	{
		this.currentUser = ((BlueListApplication)getApplication()).getCurrentUser();
	}
	
	private void getFriendsLocation()
	{
		if ( this.currentUser == null )
		{
			this.getCurrentUser();
		}
		if ( this.currentUser == null ) 
			return;
		List<MarkerInfo> markers = ((BlueListApplication)getApplication()).getMarkers();
		Set<String> userIds = new HashSet<String>();
		for(MarkerInfo markerInfo : markers )
		{
			String uid = markerInfo.uid ;
			if ( uid == null || uid.trim().length() == 0 )
			{
				uid = currentUser.getObjectId();
			}
			userIds.add( uid );
		}
		try
		{
			IBMQuery<Position> query = IBMQuery.queryForClass( Position.class );
			for(String uid: userIds)
			{
				query.whereKeyEqualsTo( "uid",  uid);
				query.findObjectsInBackground( new PositionChecker(uid, markers) );
			}
		}
		catch (IBMDataException e)
		{
			Log.e( "getFriendsLocation", e.getMessage(), e );
		}
	}
	
	class PositionChecker implements IBMQueryResult<Position>
	{
		String userId;
		List<MarkerInfo> markers;
		public PositionChecker(String uid, List<MarkerInfo> markers)
		{
			this.userId = uid;
			this.markers = markers;
		}
		
		public String getUserId()
		{
			return userId;
		}
		
		@Override
		public void onError( IBMDataException ex )
		{
			Log.i( "PositionChecker.onError", ex.getMessage(), ex );
		}

		@Override
		public void onResult( List<Position> positions )
		{
			if ( positions != null && positions.size() > 0 )
			{
				Position pos = positions.get( 0 );
				for (MarkerInfo marker: markers)
				{
					if ( userId.equals( marker.uid ) )
					{
						double distance = calcDistance(new LatLng(marker.lat, marker.lng), new LatLng(pos.getLat(), pos.getLng()));
						if ( "enter".equalsIgnoreCase( marker.type ) )
						{
							if ( distance < marker.radius )
							{
								showAlarm(pos, marker, userId);
							}
						}
						else
						{
							if ( distance > marker.radius )
							{
								showAlarm(pos, marker, userId);
							}
						}
					}
				}
			}
		}
	}
	
	private void showAlarm(Position position, MarkerInfo marker, String uid)
	{
		
	}
	
	public double calcDistance( LatLng StartP, LatLng EndP )
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
