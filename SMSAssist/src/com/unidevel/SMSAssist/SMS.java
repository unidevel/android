package com.unidevel.SMSAssist;

public class SMS {
	int id;
	int threadId;
	String address;
	int person;
	long date;
	int type;
	String body;
	boolean selected;
	public SMS(){
		selected = false;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("ID:").append(id).append(";THREAD_ID:").append(threadId).append(";");
		buf.append("ADDRESS:").append(address).append(";PERSON").append(person).append(";");
		buf.append("DATE:").append(date).append(";BODY:").append(body);
		return buf.toString();
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return selected;
	}
}
