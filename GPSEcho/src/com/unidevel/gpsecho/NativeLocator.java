package com.unidevel.gpsecho;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

public class NativeLocator {
	Timer locatingTimer;
	LocationManager locationManager;
	NativeLocationResult locationResult;
	LocationListener locationListenerGps;

	Context context;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;

	public NativeLocator(Context context) {
		this.context = context;
	}

	public void start() {
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		this.locationListenerGps = new NativeLocationListener(
				this.locationManager);
	}

	public void stop() {
		locatingTimer.cancel();
		locationManager = null;
	}

	public boolean getLocation(NativeLocationResult result) {
		// I use LocationResult callback class to pass location value from
		// MyLocation to user code.
		this.locationResult = result;
		// exceptions will be thrown if provider is not permitted.
		try {
			gpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			networkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled)
			return false;

		if (gpsEnabled)
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		if (networkEnabled)
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);
		locatingTimer = new Timer();
		locatingTimer.schedule(new GetLastLocation(), 5000);
		return true;
	}

	class NativeLocationListener implements LocationListener {
		LocationManager locationManager;

		public NativeLocationListener(LocationManager locationManager) {
			this.locationManager = locationManager;
		}

		public void onLocationChanged(Location location) {
			locatingTimer.cancel();
			locationResult.gotLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public class MyBDLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			locatingTimer.cancel();
			locationResult.gotLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			locationManager.removeUpdates(locationListenerGps);
			locationManager.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
//			if (false)
//				gps_loc = locationManager.getLastKnownLocation("hybrid");
			if (gpsEnabled)
				gps_loc = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (networkEnabled)
				net_loc = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			// if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime())
					locationResult.gotLocation(gps_loc);
				else
					locationResult.gotLocation(net_loc);
				return;
			}

			if (gps_loc != null) {
				locationResult.gotLocation(gps_loc);
				return;
			}
			if (net_loc != null) {
				locationResult.gotLocation(net_loc);
				return;
			}
			locationResult.gotLocation(null);
		}
	}

	public interface NativeLocationResult {
		public void gotLocation(Location location);
	}
}
