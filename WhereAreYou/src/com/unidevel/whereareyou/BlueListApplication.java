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
import java.util.List;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import com.ibm.mobile.services.core.IBMBaaS;
import com.unidevel.whereareyou.model.Position;
import com.unidevel.whereareyou.model.Relation;
import com.unidevel.whereareyou.model.User;

public final class BlueListApplication extends Application {
	public static final int EDIT_ACTIVITY_RC = 1;
	private static final String CLASS_NAME = BlueListApplication.class.getSimpleName();
	User currentUser;
	List<User> userList;
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
		this.userList = new ArrayList<User>();
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
	
	public List<User> getUserList()
	{
		return this.userList;
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
	
	public void removeMarker(String id)
	{
		synchronized (markers)
		{
			MarkerInfo foundMarker = null;
			for ( MarkerInfo marker: markers)
			{
				if ( id.equals( marker.id ) )
				{
					foundMarker = marker;
					break;
				}
			}
			markers.remove( foundMarker );
		}
	}
}
