package com.unidevel.whereareyou.model;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("Position")
public class Position extends IBMDataObject
{

	public static final String CLASS_NAME = "Item";
	private static final String USERID = "userid";
	private static final String LAT = "LAT";
	private static final String LNG = "LNG";
	private static final String TIME = "TIME";

	public String getUserId() {
		return (String) getObject(USERID);
	}

	public void setUserId(String userId) {
		setObject(USERID, (userId != null) ? userId : "");
	}

	public String getLat()
	{
		return (String) getObject(LAT);
	}
	
	public void setLat(String lat) {
		setObject(LAT, (lat != null) ? lat : "");
	}
	
	public String getLng()
	{
		return (String) getObject(LNG);
	}
	
	public void setLng(String lng)
	{
		setObject(LNG, (lng != null) ? lng : "");
	}
	
	public double getOffset(){
		return (Double) getObject("offset");
	}
	
	public void setOffset(double offset){
		setObject("offset", offset);
	}
	
	public String getTime()
	{
		return (String) getObject(TIME);
	}
	
	public void setTime(String time) {
		setObject(TIME, (time != null) ? time : "");
	}
	
	public String toString() {
		String theId = "";
		theId = getUserId();
		return theId;
	}
}
