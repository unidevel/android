
package com.unidevel.whereareyou;

import java.text.DecimalFormat;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.data.IBMQueryResult;
import com.unidevel.BaseActivity;
import com.unidevel.whereareyou.model.Relation;
import com.unidevel.whereareyou.model.User;

public class MapActivity extends BaseActivity implements GoogleMap.OnMapLongClickListener,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener
{
	Map<String, MarkerInfo> markers;
	Handler handler;
	
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
}
