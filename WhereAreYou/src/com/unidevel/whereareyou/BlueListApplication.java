/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.unidevel.whereareyou;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import com.ibm.mobile.services.core.IBMBaaS;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.data.IBMQueryResult;
import com.unidevel.whereareyou.model.Position;
import com.unidevel.whereareyou.model.Relation;
import com.unidevel.whereareyou.model.User;
import java.util.concurrent.atomic.*;

public final class BlueListApplication extends Application {
	public static final int EDIT_ACTIVITY_RC = 1;
	private static final String CLASS_NAME = BlueListApplication.class.getSimpleName();
	//boolean inited;
	AtomicBoolean loaded = new AtomicBoolean(false);
	User currentUser;
	List<User> friendsList;
	List<MarkerInfo> markers;

	public BlueListApplication() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity,Bundle savedInstanceState) {
				Log.d(CLASS_NAME, "Activity created: " + activity.getLocalClassName());
				//Initialize the SDK
			    IBMBaaS.initializeSDK(activity);
			}
			@Override
			public void onActivityStarted(Activity activity) {
				Log.d(CLASS_NAME, "Activity started: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityResumed(Activity activity) {
				Log.d(CLASS_NAME, "Activity resumed: " + activity.getLocalClassName());
			}
			@Override
			public void onActivitySaveInstanceState(Activity activity,Bundle outState) {
				Log.d(CLASS_NAME, "Activity saved instance state: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityPaused(Activity activity) {
				Log.d(CLASS_NAME, "Activity paused: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityStopped(Activity activity) {
				Log.d(CLASS_NAME, "Activity stopped: " + activity.getLocalClassName());
			}
			@Override
			public void onActivityDestroyed(Activity activity) {
				Log.d(CLASS_NAME, "Activity destroyed: " + activity.getLocalClassName());
			}
		});
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.loaded.set(false);
		this.friendsList = new ArrayList<User>();
		this.markers = new ArrayList<MarkerInfo>();
		this.currentUser = new User();
	    User.registerSpecialization(User.class);
	    Position.registerSpecialization( Position.class );
		Relation.registerSpecialization(Relation.class);
	}
	
	public User getCurrentUser() {
		return this.currentUser;
	}
	
	public void setCurrentUser( User currentUser )
	{
		this.currentUser = currentUser;
	}
	
	public boolean isLoaded(){
		return this.loaded.get();
	}
	
	public void setLoaded(boolean flag){
		this.loaded.set(flag);
	}
	
	private void getAllUsers(IBMQueryResult<User> resultCallback) throws IBMDataException{
		IBMQuery<User> query = IBMQuery.queryForClass( User.class );
			//query.whereKeyEqualsTo( User.USERNAME, userName );
		query.findObjectsInBackground( resultCallback );
	}
	
	private void getAllRelations(String uid, IBMQueryResult<Relation> resultCallback) throws IBMDataException{
		IBMQuery<Relation> query = IBMQuery.queryForClass( Relation.class );
			//query.whereKeyEqualsTo( User.USERNAME, userName );
		query.whereKeyEqualsTo("uid", uid);
		query.findObjectsInBackground(resultCallback );
	}
	
	public List<User> getFriends()
	{
		return this.friendsList;
	}
	
	public void getFriends(final IBMQueryResult<User> friendsResult)
	{
		final User my = this.getCurrentUser();
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
					Log.e("getAllUsers", ex.getMessage(), ex);
					friendsResult.onError( ex );
				}
				
				@Override
				public void onResult( final List<User> users )
				{
					final Map<String,User> allUsers = new LinkedHashMap<String, User>();
					for ( User user: users )
					{
						allUsers.put( user.getUserName(), user );
					}
					try
					{
						getAllRelations( my.getObjectId(), new IBMQueryResult<Relation>(){
							@Override
							public void onError( IBMDataException ex )
							{
								Log.e("getRelations", ex.getMessage(), ex);
								friendsResult.onError( ex );
							}
							
							@Override
							public void onResult( List<Relation> relations )
							{
								Set<String> ids = new HashSet<String>();
								final Map<String, Relation> allRelations = new LinkedHashMap<String, Relation>();
								for (Relation relation: relations)
								{
									allRelations.put( relation.getFriendId(), relation );
									ids.add( relation.getFriendId() );
								}
								List<User> friends = new ArrayList<User>();
								for ( int i = 0; i < users.size(); ++ i )
								{
									User user = users.get( i );
									if ( ids.contains( user.getObjectId() ) )
									{
										friends.add( user );
									}
								}
								friendsList.clear();
								friendsList.addAll( friends );
								friendsResult.onResult( friends );
							}
						});
					}
					catch (IBMDataException ex)
					{
						Log.e("getRelations", ex.getMessage(), ex);
					}
				}
			} );
		}
		catch(Exception ex)
		{
			Log.e("getUsers", ex.getMessage(), ex);
		}
	}
	
	public List<MarkerInfo> getMarkers()
	{
		synchronized (markers)
		{
			return Collections.unmodifiableList( markers );
		}
	}
	
	public void addMarker(MarkerInfo marker)
	{
		synchronized (markers)
		{
			this.markers.add( marker );
		}
	}
	
	public void removeMarker(MarkerInfo marker)
	{
		synchronized (markers)
		{
			this.markers.remove( marker );
		}
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
