package com.unidevel.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public abstract class TicketSite implements TicketBotConstants {
	protected abstract Map<String, String> getHeaders();
	protected abstract String getBody();
	protected abstract String getMethod();
	protected abstract String getURL();
	protected abstract String getEncoding();
	protected String encode(String s){
		try {
			return URLEncoder.encode(s, getEncoding());
		} catch (UnsupportedEncodingException e) {
			
		}
		return null;
	}
	protected String submit() throws IOException{
		URL url = new URL(getURL());
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setDoOutput(true);
		try {
			Map<String, String> headers = getHeaders();
			String body = getBody();
			if ( headers.containsKey("Content-Length") && body != null ) {
				headers.put("Content-Length", String.valueOf(body.getBytes().length));
			}
			if ( METHOD_POST.equalsIgnoreCase(getMethod()) ) {
				conn.setRequestMethod(METHOD_POST);
			}
			else {
				conn.setRequestMethod(METHOD_GET);
			}
			for ( String key: headers.keySet() ) {
				String value = headers.get(key);
				conn.setRequestProperty(key, value);
			}
			if ( body != null && body.length()> 0 ){
				try {
					conn.getOutputStream().write(body.getBytes());
				}
				finally{
				}
			}
			InputStream in = conn.getInputStream();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, getEncoding()));
				StringBuffer buf = new StringBuffer();
				String line;
				while ((line=reader.readLine()) != null ) {
					buf.append(line);
					buf.append("\r\n");
				}
				return buf.toString();
			}
			finally{
			}
		}
		finally {
			try { conn.getOutputStream().close(); } catch(Throwable ex){}
			try { conn.getInputStream().close(); } catch(Throwable ex){}
			conn.disconnect();
		}
	}
	
	public String getDate(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String sDate = sdf.format(date);
		return sDate;
	}
	
	public String getDate(int wantDate, String format){
		Calendar cal = Calendar.getInstance();
		if ( DATE_THIS_SUNDAY == wantDate ) {
			cal.add(Calendar.DAY_OF_MONTH, 7);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}
		else if ( DATE_THIS_SATURDAY == wantDate ) {
			cal.add(Calendar.DAY_OF_MONTH, 7);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}
		else {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return getDate(cal.getTime(), format);
	}
}
