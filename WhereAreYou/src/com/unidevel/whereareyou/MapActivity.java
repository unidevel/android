
package com.unidevel.whereareyou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.data.IBMQueryResult;
import com.unidevel.BaseActivity;
import com.unidevel.whereareyou.model.Relation;
import com.unidevel.whereareyou.model.User;

public class MapActivity extends BaseActivity implements GoogleMap.OnMapLongClickListener,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, InfoWindowAdapter, Constants
{
	Handler handler;
	String type;
	LayoutInflater inflater; 

	@Override
	public void onInfoWindowClick( final Marker m )
	{
		m.hideInfoWindow();
		final MarkerInfo i =getMarkerInfo(m);
		if ( i == null )
			return;

		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		View view = inflater.inflate( R.layout.marker, null, false );
		final EditText titleText = (EditText)view.findViewById( R.id.title );
		titleText.setText( i.title );
		builder.setTitle( R.string.alert_title );
		final EditText radiusText = (EditText)view.findViewById( R.id.radius );
		radiusText.setText( ""+i.radius );
		final RadioButton enterBtn = (RadioButton)view.findViewById( R.id.type_enter );
		final RadioButton leaveBtn = (RadioButton)view.findViewById( R.id.type_leave );
		final ToggleButton enableBtn=(ToggleButton)view.findViewById(R.id.enable);
		if( TYPE_ENTER.equals( i.type ))
		{
			enterBtn.setChecked( true );
			leaveBtn.setChecked( false );
		}
		else
		{
			enterBtn.setChecked( false );
			leaveBtn.setChecked( true );			
		}
		enableBtn.setChecked(i.enabled);
		final Spinner spinner = (Spinner)view.findViewById(R.id.alertUser);
		final UserAdapter adapter = getFriendsAndMeAdapter(spinner, i.uid);
		spinner.setAdapter( adapter );
		
		builder.setView( view );
		builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				i.title = titleText.getText().toString();
				try
				{
					i.radius = Double.valueOf( radiusText.getText().toString() );
				}
				catch(Exception ex)
				{
					
				}
				i.type = enterBtn.isChecked()?TYPE_ENTER:TYPE_LEAVE;
				i.enabled = enableBtn.isChecked();
				User user= (User)spinner.getSelectedItem();
				if(user!=null){
					i.uid=user.getObjectId();
					i.userName=user.getUserName();
				}
				dialog.dismiss();
				updateMarkerOnMap( i );
				m.showInfoWindow();
			}
		} );
		builder.setNegativeButton( R.string.delete_button, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				((BlueListApplication)getApplication()).removeMarker( i );
				dialog.dismiss();
			}
		} );
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private UserAdapter getFriendsAndMeAdapter(final Spinner spinner, final String selectedUid)
	{
		final List<User> friendsAndMe = new ArrayList<User>();
		BlueListApplication app = ((BlueListApplication)getApplication());
		friendsAndMe.add( app.getCurrentUser() );
		final UserAdapter adapter = new UserAdapter(this, friendsAndMe);
		List<User> friends = app.getFriends();
		if ( friends == null  || friends.size() == 0 )
		{
			app.getFriends( new IBMQueryResult<User>()
			{
				@Override
				public void onError( IBMDataException ex )
				{
					
				}
				@Override
				public void onResult( final List<User> friends )
				{
					handler.post( new Runnable(){
						public void run() {
							adapter.addFriends( friends );
							adapter.notifyDataSetChanged();
							if ( selectedUid == null || selectedUid.trim().length() == 0 )
							{
								spinner.setSelection( 0 );
							}
							else
							{
								for ( int i = 0; i <  friends.size(); ++ i )
								{
									User user = friends.get( i );
									if ( selectedUid.equals( user.getObjectId() ) )
									{
										spinner.setSelection( i+1 );
										return;
									}
								}
							}							
						}
					});
				}
			} );
		}
		else
		{
			friendsAndMe.addAll( app.getFriends() );
		}
		return adapter;
	}

	@Override
	public boolean onMarkerClick( Marker m )
	{
		return false;
	}

	List<Circle> circles;
	Intent serviceIntent = new Intent( "LOCATE_SERVICE" );
	Intent monitorIntent = new Intent( "MONITOR_SERVICE" );

	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem item;
		item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, getString(R.string.menu_manage_friends));
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showManageFriendsDailog();
				return true;
			}
		});
		item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, getString(R.string.menu_manage_alerts));
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(MapActivity.this, AlertListActivity.class);
				startActivityForResult( intent, 100 );
				return true;
			}
		});
		return true;
	}
	
	private void _showFriendsDialog(final String[] users, final boolean[] friends)
	{
		final HashMap<Integer, Boolean> changedValues = new HashMap<Integer, Boolean>();
		boolean[] checked = Arrays.copyOf( friends, friends.length );
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMultiChoiceItems( users, checked, new DialogInterface.OnMultiChoiceClickListener(){
			@Override
			public void onClick( DialogInterface dailog, int position, boolean checked )
			{
				changedValues.put( position, checked );
			}
		});
		builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				BlueListApplication app = (BlueListApplication)getApplication();
				final User my = app.getCurrentUser();
				dialog.dismiss();
				for ( Integer position: changedValues.keySet() )
				{
					String name = users[position];
					boolean newValue = changedValues.get(position);
					boolean oldValue = friends[position];
					if ( newValue != oldValue )
					{
						if ( newValue )
						{
							String uid = allUsers.get( name ).getObjectId();
							Relation relation = new Relation();
							relation.setFriendId( uid );
							relation.setUserId( my.getObjectId() );
							relation.saveInBackground();
						}
						else
						{
							String uid = allUsers.get( name ).getObjectId();
							Relation relation = allRelations.get( uid );
							relation.deleteInBackground();							
						}
					}
				}
			}
		} );
		builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener()
		{	
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
			}
		} );
		builder.setTitle( "Manage friends" );
		builder.create().show();
	}
	
	@Override
	public void onMapLongClick( LatLng p )
	{
		BlueListApplication app  = ((BlueListApplication)this.getApplication());
		User user=app.getCurrentUser();
		// Get back the mutable Circle
		// Circle circle = mMap.addCircle(circleOptions);
		MarkerInfo i = new MarkerInfo();
		i.title = getNextAlertName();
		i.lat = p.latitude;
		i.lng = p.longitude;
		i.radius = this.getDefaultRadius();
		i.type = this.type;
		i.enabled = true;
		i.uid=user.getObjectId();
		i.userName=user.getUserName();
		addMarker(i);
		updateMarkerOnMap(i);
		i.marker.showInfoWindow();
	}
	
	int index = 0;
	public String getNextAlertName()
	{
		index ++;
		return getString(R.string.alert_name)+index;
	}
	
	public void updateMarkerOnMap(MarkerInfo m)
	{
		LatLng p = null;
		if ( m.circle == null )
		{
			p = new LatLng(m.lat, m.lng);
			CircleOptions circleOptions = new CircleOptions().center( p )
					.radius( m.radius*1000 ); // In  meters
			Circle circle = mMap.addCircle( circleOptions );
			m.circle = circle;
		}
		if ( TYPE_ENTER == m.type )
		{
			m.circle.setFillColor( 0x800000FF );
			m.circle.setStrokeColor( 0xC00000FF );
		}
		else
		{
			m.circle.setFillColor( 0x8000FF00 );
			m.circle.setStrokeColor( 0xC000FF00 );
		}
		m.circle.setRadius( m.radius*1000 );
		m.circle.setStrokeWidth( 1 );
		if ( m.marker == null )
		{
			if  ( p == null )
			{
				p = new LatLng(m.lat, m.lng);
			}
			Marker marker =
					mMap.addMarker( new MarkerOptions().position( p ).
							title( m.title ).snippet( getMarkerDescription(m) ).flat( true ).draggable( true ) );
			m.marker = marker;
		}
		else
		{
			m.marker.setTitle( m.title );
			m.marker.setSnippet( getMarkerDescription(m) );
		}
	}
	
	private String getMarkerDescription(MarkerInfo m)
	{
		StringBuffer buf = new StringBuffer();
		buf.append( getString(R.string.info_alarm) ).append( m.title ).append( "\n" )
			.append( getString(R.string.info_type) ).append(
					TYPE_ENTER.equals( m.type )?getString(R.string.info_enter):getString(R.string.info_leave) ).append( "\n" )
			.append( getString(R.string.info_radius) ).append( m.radius ).append( getString(R.string.info_km) ).append( "\n" )
			.append( getString(R.string.info_people) )
			.append( m.userName == null?"":m.userName );
			//.append( getUserName(m.uid) );
		return buf.toString();
	}
	
	private Object getUserName( String uid )
	{
		BlueListApplication app  = ((BlueListApplication)this.getApplication());
		if ( uid == null || uid.length() == 0 )
		{
			User user = app.getCurrentUser();
			if ( user != null )
			{
				return user.getUserName();
			}
			else
			{
				return uid;
			}
		}
		List<User> friends = app.getFriends();
		if ( friends == null )
		{
			return uid;
		}
		for ( User user : friends )
		{
			if ( uid.equals( user.getObjectId() ))
			{
				return user.getUserName();
			}
		}
		return uid;
	}

	public void addMarker(MarkerInfo marker)
	{
		((BlueListApplication)getApplication()).addMarker( marker );
	}
	
	public List<MarkerInfo> getMarkers()
	{
		return ((BlueListApplication)getApplication()).getMarkers();
	}
	
	public MarkerInfo getMarkerInfo(Marker marker)
	{
		List<MarkerInfo> markers = getMarkers();
		for (MarkerInfo info :markers )
		{
			if ( marker.equals( info.marker ))
			{
				return info;
			}
		}
		return null;
	}
	
	/*
	public void removeMarker(String id)
	{
		MarkerInfo marker = ((BlueListApplication)getApplication()).removeMarker( id );
		if ( marker != null )
		{
			if ( marker.marker != null )
			{
				marker.marker.remove();
			}
			if ( marker.circle != null )
			{
				marker.circle.remove();
			}
		}
	}
	*/

	protected double getDefaultRadius()
	{
		return 0.05;
	}

	private GoogleMap mMap;
	private Map<String, User> allUsers;
	private Map<String, Relation> allRelations;
	private void showManageFriendsDailog(){
		BlueListApplication app = (BlueListApplication)getApplication();
		final User my = app.getCurrentUser();
		final ProgressDialog progress = ProgressDialog.show( this, "", "Getting user and friends information...", true, false);
		final List<String> names = new ArrayList<String>();
		final List<String> friendNames = new ArrayList<String>();
		if ( my == null || my.getObjectId() == null )
		{
			return;
		}
		try
		{
			getAllUsers( new IBMQueryResult<User>()
			{
				@Override
				public void onError( IBMDataException ex )
				{
					progress.cancel();
				}
				
				@Override
				public void onResult( final List<User> users )
				{
					allUsers = new LinkedHashMap<String, User>();
					for ( User user: users )
					{
						allUsers.put( user.getUserName(), user );
						names.add( user.getUserName() );
					}
					try
					{
						getAllRelations( my.getObjectId(), new IBMQueryResult<Relation>(){
							@Override
							public void onError( IBMDataException ex )
							{
								progress.cancel();
							}
							
							@Override
							public void onResult( List<Relation> relations )
							{
								progress.dismiss();
								Set<String> ids = new HashSet<String>();
								allRelations = new LinkedHashMap<String, Relation>();
								for (Relation relation: relations)
								{
									allRelations.put( relation.getFriendId(), relation );
									ids.add( relation.getFriendId() );
								}
								final boolean[] checked = new boolean[allUsers.size()];
								final String[] userNames = names.toArray(new String[0]);
								for ( int i = 0; i < users.size(); ++ i )
								{
									User user = users.get( i );
									checked[i] = ids.contains( user.getObjectId() );
								}
								handler.post( new Runnable(){
									public void run() {
										_showFriendsDialog( userNames, checked );
									};
								});
							}
						});
					}
					catch (IBMDataException ex)
					{
						progress.cancel();
						Log.e("getRelations", ex.getMessage(), ex);
					}
				}
			} );
		}
		catch(Exception ex)
		{
			progress.cancel();
			Log.e("getUsers", ex.getMessage(), ex);
		}
	}

	public void getAllUsers(IBMQueryResult<User> resultCallback) throws IBMDataException{
		IBMQuery<User> query = IBMQuery.queryForClass( User.class );
			//query.whereKeyEqualsTo( User.USERNAME, userName );
		query.findObjectsInBackground( resultCallback );
	}
	
	public void getAllRelations(String uid, IBMQueryResult<Relation> resultCallback) throws IBMDataException{
		IBMQuery<Relation> query = IBMQuery.queryForClass( Relation.class );
			//query.whereKeyEqualsTo( User.USERNAME, userName );
		query.whereKeyEqualsTo("uid", uid);
		query.findObjectsInBackground(resultCallback );
	}
	
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.map );
		this.handler = new Handler();
		this.inflater = LayoutInflater.from( this );

		this.mMap = ((MapFragment)getFragmentManager().findFragmentById( R.id.map )).getMap();
		this.mMap.getUiSettings().setCompassEnabled( true );
		this.mMap.setMyLocationEnabled( true );
		this.mMap.setOnMapLongClickListener( this );
		// this.mMap.setOnMarkerClickListener(this);
		this.mMap.setOnInfoWindowClickListener( this );
		this.mMap.setInfoWindowAdapter( this );
		this.type = TYPE_ENTER;
		
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
		this.startService( monitorIntent );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		if (requestCode == 100 && data != null)
		{
			int index = data.getIntExtra( "index", -1 );
			if ( index >= 0 )
			{
				List<MarkerInfo> markers = ((BlueListApplication)getApplication()).getMarkers();
				MarkerInfo marker = markers.get( index );
				if ( marker != null )
				{
					LatLng p = new LatLng(marker.lat, marker.lng);
					CameraUpdate update = CameraUpdateFactory.newLatLng( p );
					mMap.moveCamera( update );
					marker.marker.showInfoWindow();
				}
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.stopService( serviceIntent );
	}

	@Override
	public View getInfoContents( Marker marker )
	{
		View view = this.inflater.inflate( R.layout.infowin, null );
		TextView textView = (TextView)view.findViewById( R.id.text );
		textView.setText( marker.getSnippet() );
		return view;
	}

	@Override
	public View getInfoWindow( Marker marker )
	{
		return null;
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		saveMarkers();
	}
	
	private void saveMarkers()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		BlueListApplication app = (BlueListApplication)getApplication();
		List<MarkerInfo> markers = app.getMarkers();
		JSONArray alerts = new JSONArray();
		for ( int i = 0; i < markers.size(); ++i )
		{
			JSONObject alert = new JSONObject();
			MarkerInfo marker = markers.get( i );
			alerts.put( alert );
			try
			{
				alert.put( "title", marker.title ).put( "enabled", marker.enabled )
					.put( "type", marker.type ).put( "extra", marker.extra )
					.put( "uid", marker.uid ).put( "username", marker.userName )
					.put( "lat", marker.lat ).put("lng", marker.lng )
					.put( "radius", marker.radius );
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		Editor edit = pref.edit();
		edit.putString( "alerts", alerts.toString() );
		edit.commit();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		BlueListApplication app = (BlueListApplication)getApplication();
		loarMarkers();
		for(MarkerInfo m:app.getMarkers()){
			updateMarkerOnMap(m);
		}
	}

	private void loarMarkers()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		BlueListApplication app = (BlueListApplication)getApplication();
		List<MarkerInfo> markers = app.getMarkers();
		for ( MarkerInfo m: markers )
		{
			if ( m.circle != null )
			{
				m.circle.remove();
			}
			if ( m.marker != null )
			{
				m.marker.remove();
			}
		}
		app.clearMarkers();
		String data = pref.getString( "alerts", null );
		JSONArray alerts = null;
		try
		{
			alerts = new JSONArray(data);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if ( alerts != null )
		{
			for ( int i = 0; i < alerts.length(); ++ i )
			{
				try
				{
					JSONObject alert = alerts.getJSONObject( i );
					MarkerInfo marker = new MarkerInfo();
					marker.index = i;
					marker.enabled = alert.getBoolean( "enabled" );
					marker.title = alert.getString( "title" );
					marker.type = alert.getString( "type" );
					//marker.extra = alert.getString( "extra" );
					marker.uid = alert.getString( "uid" );
					marker.userName = alert.getString( "username" );
					marker.lat = alert.getDouble( "lat" );
					marker.lng = alert.getDouble( "lng" );
					marker.radius = alert.getDouble( "radius" );
					app.addMarker( marker );
				}
				catch (JSONException e)
				{
					Log.e( "loadAlert", e.getMessage(), e );
				}
			}
		}
	}
}
