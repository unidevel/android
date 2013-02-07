package com.unidevel.gpsecho;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.LocationManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class BaiduLocator {
	Timer locatingTimer;
	LocationManager locationManager;
	LocationClient locationClient;
	BaiduLocationResult locationResult;
	BaiduLocationListener locationListener;

	Context context;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;

	public BaiduLocator(Context context) {
		this.context = context;
	}
	
	private LocationClient createBaiduLocationClient()
	{
		LocationClient locationClient = new LocationClient(this.context);
		LocationClientOption options = new LocationClientOption();
		options.setAddrType("all");
		options.setCoorType("bd09II");
		options.setScanSpan(5000);
		locationClient.setLocOption(options);
		return locationClient;
	}

	public void start() {
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		this.locationClient = createBaiduLocationClient();
		this.locationClient.start();
		this.locationListener = new BaiduLocationListener(this.locationClient);
	}

	public void stop() {
		this.locatingTimer.cancel();
		this.locationManager = null;
		this.locationClient.stop();
	}

	public boolean getLocation(BaiduLocationResult result) {
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

		this.locationClient.registerLocationListener(this.locationListener);
		locatingTimer = new Timer();
		locatingTimer.schedule(new GetLastLocation(), 5000);
		return true;
	}

	class BaiduLocationListener implements BDLocationListener {
		LocationClient locationClient;

		public BaiduLocationListener(LocationClient locationClient) {
			this.locationClient = locationClient;
		}

		public void onLocationChanged(BDLocation location) {
			
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			locatingTimer.cancel();
			locationResult.gotLocation(location);
			locationClient.unRegisterLocationListener(locationListener);
		}

		@Override
		public void onReceivePoi(BDLocation location) {	
		}
	};
	
	public void notify(double lat, double lnt, int poi)
	{
	}

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			BDLocation location = null;
			if (gpsEnabled || networkEnabled)
				location = locationClient.getLastKnownLocation();
			// if there are both values use the latest one
			if (location != null ) {
				locationResult.gotLocation(location);
				return;
			}
			locationResult.gotLocation(null);
		}
	}

	public interface BaiduLocationResult {
		public void gotLocation(BDLocation location);
	}
}
