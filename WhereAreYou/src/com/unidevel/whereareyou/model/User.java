package com.unidevel.whereareyou.model;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("User")
public class User extends IBMDataObject
{

	public static final String CLASS_NAME = "Item";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	public String getUserName()
	{
		return (String) getObject(USERNAME);
	}
	
	public void setUserName(String userName) {
		setObject(USERNAME, (userName != null) ? userName : "");
	}
	
	public String getPassword()
	{
		return (String) getObject(PASSWORD);
	}
	
	public void setPassword(String password)
	{
		setObject(PASSWORD, (password != null) ? password : "");
	}
	
	public String toString() {
		String theId = "";
		theId = this.getUserName();
		return theId;
	}

}
