package com.unidevel.whereareyou.model;
import com.ibm.mobile.services.data.*;

@IBMDataObjectSpecialization("Relation")
public class Relation extends IBMDataObject
{
	public String getUserId()
	{
		return (String) getObject("uid");
	}

	public void setUserId(String userId) {
		setObject("uid", (userId != null) ? userId : "");
	}

	public String getFriendId()
	{
		return (String) getObject("fid");
	}

	public void setFriendId(String fid)
	{
		setObject("fid", (fid != null) ? fid : "");
	}
	
	public String getGroupId()
	{
		return (String) getObject("gid");
	}

	public void setGroupId(String gid)
	{
		setObject("gid", (gid != null) ? gid : "");
	}
	
	public String toString() {
		return getUserId() + ":"+getFriendId();
	}
}
