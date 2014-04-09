
package com.unidevel.whereareyou;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

public class MonitorService extends Service implements Constants
{
	User currentUser;
	Timer timer;
	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		if ( timer == null )
		{
			timer = new Timer();
			timer.schedule( new TimerTask(){
				@Override
				public void run()
				{
					getFriendsLocation();
				}
			}, 0, 5000);
		}
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
			int index = 0;
			for(String uid: userIds)
			{
				query.whereKeyEqualsTo( "userid",  uid);
				query.findObjectsInBackground( new PositionChecker(uid, markers, index) );
				index ++; 
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
		int index;
		public PositionChecker(String uid, List<MarkerInfo> markers, int index)
		{
			this.userId = uid;
			this.markers = markers;
			this.index = index;
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
				String userId = pos.getUserId();
				String userName = pos.getUserName();
				boolean enterAlarm = false;
				boolean leaveAlarm = true;
				boolean hasLeave = false;
				for (MarkerInfo marker: markers)
				{
					if ( !marker.enabled ) continue;
					if ( marker.uid == null && currentUser != null )
					{
						marker.uid = currentUser.getObjectId();
					}
					
					if ( userId.equals( marker.uid ) )
					{
						double distance = calcDistance(new LatLng(marker.lat, marker.lng), new LatLng(pos.getLat(), pos.getLng()));
						if ( TYPE_ENTER.equalsIgnoreCase( marker.type ) )
						{
							if ( distance < marker.radius )
							{
								enterAlarm = true;
							}
						}
						else
						{
							hasLeave = true;
							if ( distance >= marker.radius )
							{
								leaveAlarm &= true;
							}
							else
							{
								leaveAlarm &= false;
							}
						}
					}
					
					if ( hasLeave && leaveAlarm )
					{
						showAlarm(pos, TYPE_LEAVE, index);
					}
					if ( enterAlarm )
					{
						showAlarm(pos, TYPE_ENTER, index*2);
					}
				}
			}
		}
	}
	
	private void showAlarm(Position position, /* MarkerInfo marker, String uid, String userName,*/ String type, int index)
	{
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
		Notification n = new Notification();  
		n.icon = android.R.drawable.ic_dialog_alert;  
		if ( TYPE_ENTER.equals( type ) )
		{
			n.tickerText = this.getString( R.string.notify_title_enter, position.getUserName()==null?"":position.getUserName() );
		}
		else
		{
			n.tickerText = this.getString( R.string.notify_title_leave, position.getUserName()==null?"":position.getUserName() );
		}
		
		n.defaults = Notification.DEFAULT_SOUND;  
		Intent intent = new Intent(this, AlertListActivity.class);
		intent.putExtra( "uid", position.getUserId() );
		intent.putExtra( "username", position.getUserName() );
		PendingIntent pd = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
		n.setLatestEventInfo(this, n.tickerText, "", pd);   
		
		nm.notify(index, n);  
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
