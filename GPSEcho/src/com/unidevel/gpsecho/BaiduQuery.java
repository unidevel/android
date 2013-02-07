package com.unidevel.gpsecho;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BaiduQuery extends HttpBase{
	public static final String BAIDU_KEY = "a9c37f3eb5a215fbd558d961a22867e1";
	public static final String STATUS_OK = "OK";
	public static final String KEY_STATUS = "status";
	public static final String KEY_RESULT = "result";
	public static final String KEY_FORMATTED_ADDRESS = "formatted_address";
	public static final String KEY_BUSINESS = "business";
	public static final String KEY_CITYCODE = "cityCode";
	public static final String KEY_ADDRESS = "addressComponent";
	public static String getFormattedAddress(double lat,double lng) 
	{
		JSONObject result = queryAddress(lat,lng);
		try {
			if (result != null && STATUS_OK.equalsIgnoreCase(result.getString(KEY_STATUS)) )
			{
				JSONObject r = (JSONObject) result.getJSONObject(KEY_RESULT);
				return r.getString(KEY_FORMATTED_ADDRESS);
			}
		} catch (JSONException e) {
			Log.e("Baidu.getFormattedAddress", e.getMessage(), e);
		}
		return null;
	}
	
	public static JSONObject getAddress(double lat,double lng) 
	{
		JSONObject result = queryAddress(lat,lng);
		try {
			if (result != null && STATUS_OK.equalsIgnoreCase(result.getString(KEY_STATUS)) )
			{
				JSONObject r = result.getJSONObject(KEY_RESULT);
				return r.getJSONObject(KEY_ADDRESS);
			}
		} catch (JSONException e) {
			Log.e("Baidu.getFormattedAddress", e.getMessage(), e);
		}
		return null;
	}
	
	private static JSONObject queryAddress(double lat, double lng)
	{
		String link = "http://api.map.baidu.com/geocoder?location="+lat+","+lng+"&output=json&key="+BAIDU_KEY;
		try {
			HttpBase httpBase = new HttpBase();
			String s = httpBase.syncGet(link);
			if ( s != null )
			{
				JSONObject result = new JSONObject(s);
				return result;
			}
		} catch (JSONException e) {
			Log.e("BAIDU_QUERY_LOCATION", e.getMessage(), e);
			e.printStackTrace();
		} catch(Throwable e){
			Log.e("BAIDU_QUERY_LOCATION", e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}
}
