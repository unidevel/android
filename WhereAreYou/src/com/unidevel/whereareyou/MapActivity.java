
package com.unidevel.whereareyou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		final Spinner spinner = (Spinner)view.findViewById(R.id.alertUser);
		UserAdapter adapter = getFriendsAndMeAdapter(spinner, i.uid);
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
				dialog.dismiss();
				updateMarkerOnMap( i );
				m.showInfoWindow();
			}
		} );
		builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				dialog.dismiss();
				m.showInfoWindow();
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
		
		MenuItem item = menu.add(0, 0, Menu.NONE, "Manage friends");
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showManageFriendsDailog();
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
		// Get back the mutable Circle
		// Circle circle = mMap.addCircle(circleOptions);
		MarkerInfo i = new MarkerInfo();
		i.title = getNextAlertName();
		i.lat = p.latitude;
		i.lng = p.longitude;
		i.radius = this.getDefaultRadius();
		i.type = this.type;
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
			.append( getString(R.string.info_people) ).append( getUserName(m.uid) );
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
		Editor edit = pref.edit();
		BlueListApplication app = (BlueListApplication)getApplication();
		List<MarkerInfo> markers = app.getMarkers();
		for ( int i = 0; i < markers.size(); ++i )
		{
			MarkerInfo marker = markers.get( i );
			String prefix = "marker.";
			edit.putInt( prefix+"index."+i, marker.index);
			edit.putBoolean( prefix+"enabled."+i, marker.enabled);
			edit.putString( prefix+"title."+i, marker.title);
			edit.putString( prefix+"type."+i, marker.type);
			edit.putString( prefix+"extra."+i, marker.extra);
			edit.putString( prefix+"uid."+i, marker.uid);
			edit.putString( prefix+"username."+i, marker.userName);
			edit.putString( prefix+"lat."+i, String.valueOf( marker.lat ) );
			edit.putString( prefix+"lng."+i, String.valueOf( marker.lng ) );
			edit.putString( prefix+"radius."+i, String.valueOf( marker.radius ) );
		}
		edit.commit();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//loarMarkers();
	}

	private void loarMarkers()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		BlueListApplication app = (BlueListApplication)getApplication();

		for ( int i = 0; true; ++ i )
		{
			String prefix = "marker.";
			MarkerInfo marker = new MarkerInfo();
			int index = pref.getInt( prefix+"index."+i, -1);
			if ( index < 0 ) break;
			marker.enabled = pref.getBoolean( prefix+"enabled."+i, true);
			marker.title = pref.getString( prefix+"title."+i, "Unknown");
			marker.type = pref.getString( prefix+"type."+i, TYPE_ENTER);
			marker.extra = pref.getString( prefix+"extra."+i, marker.extra);
			marker.uid = pref.getString( prefix+"uid."+i, null);
			marker.userName = pref.getString( prefix+"username."+i, "");
			try
			{
				marker.lat = Double.valueOf( pref.getString( prefix+"lat."+i, "0" ) );
			}
			catch(Exception ex){}
			try
			{
				marker.lng = Double.valueOf( pref.getString( prefix+"lng."+i, "0" ) );
			}
			catch(Exception ex){}
			try
			{
				marker.radius = Double.valueOf( pref.getString( prefix+"radius."+i, String.valueOf( marker.radius ) ) );
			}
			catch(Exception ex){}
			app.addMarker( marker );
		}
	}
}
