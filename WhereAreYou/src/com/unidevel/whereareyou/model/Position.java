package com.unidevel.whereareyou.model;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("Position")
public class Position extends IBMDataObject
{

	public static final String CLASS_NAME = "Position";
	private static final String USERID = "userid";
	private static final String LAT = "LAT";
	private static final String LNG = "LNG";
	private static final String TIME = "TIME";
	private static final String ACCURACY = "ACCURACY";

	public String getUserId() {
		return (String) getObject(USERID);
	}

	public void setUserId(String userId) {
		setObject(USERID, (userId != null) ? userId : "");
	}
	
	public String getUserName() {
		return (String) getObject("username");
	}

	public void setUserName(String userId) {
		setObject("username", (userId != null) ? userId : "");
	}


	public double getLat()
	{
		return (Double) getObject(LAT);
	}
	
	public void setLat(double lat) {
		setObject(LAT, lat);
	}
	
	public double getLng()
	{
		return (Double) getObject(LNG);
	}
	
	public void setLng(double lng)
	{
		setObject(LNG, lng);
	}

	public String getAccuracy()
	{
		return (String) getObject(LNG);
	}
	
	public void setAccuracy(double accuracy)
	{
		setObject(ACCURACY, accuracy);
	}
	
	public Long getTime()
	{
		return (Long) getObject(TIME);
	}
	
	public void setTime(long time) {
		setObject(TIME, time);
	}
	
	public String toString() {
		String theId = "";
		theId = getUserId();
		return theId;
	}
}
