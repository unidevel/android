package com.unidevel.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import android.content.Context;
import android.os.Environment;

public class TicketBotUser implements TicketBotConstants {
	String version;
	public String name;
	public String id;
	public String phone;
	public int wantTickets;
	public int wantDate;
	
	public TicketBotUser(){
		version = VERSION;
		name = "";
		id = "";
		phone = "";
		wantTickets = 0;
		wantDate = 0;
	}
	
	private static File getConfigurationFile(Context context){
		String name = "TicketBot";
		File dir;
		if ( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState() )){
			dir = new File(Environment.getExternalStorageDirectory(), name);
		}
		else {
			dir = context.getFilesDir();
		}
		if (!dir.exists() ) dir.mkdirs();
		return new File(dir, "prefs.xml");
	}
	
	public static TicketBotUser load(Context context) throws InvalidPropertiesFormatException, IOException{
		TicketBotUser user = new TicketBotUser();
		user.load(getConfigurationFile(context));
		return user;
	}
	
	private void load(File file) throws IOException{
		FileInputStream in = new FileInputStream(file);
		Properties props = new Properties();
		props.loadFromXML(in);

		name = props.getProperty(KEY_NAME, "");
		id = props.getProperty(KEY_ID, "");
		phone = props.getProperty(KEY_PHONE, "");
		wantTickets = toInt(props.getProperty(KEY_TICKETS), 0);
		wantDate = toInt(props.getProperty(KEY_DATE), 0);
	}
	
	private int toInt(String value, int defValue){
		try {
			return Integer.valueOf(value);
		}
		catch(Throwable ex){
			return defValue;
		}
	}
	
	public void save(Context context) throws IOException{
		save(getConfigurationFile(context));
	}
	
	private void save(File file) throws IOException{
		Properties props = new Properties();
		props.setProperty(KEY_NAME, name);
		props.setProperty(KEY_ID, id);
		props.setProperty(KEY_PHONE, phone);
		props.setProperty(KEY_TICKETS, String.valueOf(wantTickets));
		props.setProperty(KEY_DATE, String.valueOf(wantDate));
		FileOutputStream out = new FileOutputStream(file);
		props.storeToXML(out, "");
	}
}
