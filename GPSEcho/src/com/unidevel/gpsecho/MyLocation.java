package com.unidevel.gpsecho;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.baidu.location.*;

public class MyLocation {
    Timer timer1;
    LocationManager lm;
	LocationClient lc;

	Context ctx;
    boolean gps_enabled=false;
    boolean network_enabled=false;
	boolean useBaidu=false;
	
	public MyLocation(Context ctx){
		this.ctx=ctx;
	}
	
	public void start(){
        if(lm==null)
            lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		if(lc==null){
			lc = new LocationClient(ctx);
		}
		lc.start();		
	}
	
	public void stop(){
		lc.stop();
		timer1.cancel();
		lm=null;
		lc=null;
	}
	
    public boolean getLocation(LocationResult<Location> result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1=new Timer();
        timer1.schedule(new GetLastLocation(), 5000);
        return true;
    }

    public boolean getBDLocation(LocationResult<BDLocation> result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1=new Timer();
        timer1.schedule(new GetLastLocation(), 5000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener(LocationResult<Location> result) {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            result.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

	public class MyBDLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
		}
	}
	
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
             lm.removeUpdates(locationListenerGps);
             lm.removeUpdates(locationListenerNetwork);

             Location net_loc=null, gps_loc=null;
             if(false)
				gps_loc=lm.getLastKnownLocation("hybrid");
			 if(gps_enabled)
                 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             if(network_enabled)
                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

             //if there are both values use the latest one
             if(gps_loc!=null && net_loc!=null){
                 if(gps_loc.getTime()>net_loc.getTime())
                     locationResult.gotLocation((T)gps_loc);
                 else
                     locationResult.gotLocation((T)net_loc);
                 return;
             }

             if(gps_loc!=null){
                 locationResult.gotLocation((T)gps_loc);
                 return;
             }
             if(net_loc!=null){
                 locationResult.gotLocation((T)net_loc);
                 return;
             }
             locationResult.gotLocation(null);
        }
    }

    public interface LocationResult<T>{
        public void gotLocation(T location);
    }
}
