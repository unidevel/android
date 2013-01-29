package com.unidevel.gpsecho;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Menu;
import android.widget.TextView;

import com.unidevel.gpsecho.MyLocation.LocationResult;

public class MainActivity extends Activity {
	TextView testView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        test();
    }
    
    public void test(){
        testView = new TextView(this);
        setContentView(testView);
        
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getAllProviders();
        for (String name: providers )
        {
        	LocationProvider provider = lm.getProvider(name);
        }
        testView.append(String.valueOf(providers));
        testView.setAutoLinkMask(Linkify.ALL);
        MyLocation location = new MyLocation();
        location.getLocation(this, new LocationResult(){
			@Override
			public void gotLocation(Location location) {
				double lat = location.getLatitude(); 
				double lng = location.getLongitude();
//				testView.append(String.valueOf(location));
//				testView.append("\n\n");
//				testView.append("http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&output=json&key=a9c37f3eb5a215fbd558d961a22867e1");
				testView.setText(Html.fromHtml("http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&output=json&key=a9c37f3eb5a215fbd558d961a22867e1\n"+
						BaiduQuery.getAddress(lat, lng)
						));
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
