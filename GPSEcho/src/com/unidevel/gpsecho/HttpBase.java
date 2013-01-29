package com.unidevel.gpsecho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpBase {
	public interface ResponseListener{ 
		void onResponse(String response);
		void onFailure(Exception exception);
	}
	
	private class ResultWrapper 
	{
		public String result;
		public Throwable exception;
	}
	
	protected String responseEncoding="UTF-8";
	protected int connectTimeout=10000;
	protected int readTimeout=5000;
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	
	public String getResponseEncoding() {
		return responseEncoding;
	}
	
	public void setResponseEncoding(String responseEncoding) {
		this.responseEncoding = responseEncoding;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	public int getReadTimeout() {
		return readTimeout;
	}
	
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public Thread asyncGet(final String link, final ResponseListener listener)
	{
		Thread thread = new Thread()
		{
			public void run() {
				try {
					String response = httpGet(link);
					listener.onResponse(response);
				} catch (Exception e) {
					listener.onFailure(e);
				}
			}
		};
		thread.start();
		return thread;
	}
	
	public String syncGet(final String link)
	{
		final ResultWrapper result = new ResultWrapper();
		Thread thread = asyncGet(link, new ResponseListener() {
			
			@Override
			public void onResponse(String response) {
				result.result = response;
			}
			
			@Override
			public void onFailure(Exception exception) {
				result.exception = exception;
			}
		});
		try {
			thread.join();
		}
		catch(Throwable ex)
		{
			Log.e("HttpBase.syncGet", ex.getMessage(), ex);
		}
		return result.result;
	}
	
	private String httpGet(String link) throws IOException
	{
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(this.connectTimeout);
		conn.setReadTimeout(this.readTimeout);
		conn.setDoOutput(false);
		conn.setRequestMethod("GET");
		InputStream in = conn.getInputStream();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, this.responseEncoding));
			StringBuffer resultBuf = new StringBuffer();
			char readBuf[] = new char[8192];
			int len;
			while ( (len = reader.read(readBuf) ) > 0 ){
				resultBuf.append(readBuf, 0, len);
			}
			return resultBuf.toString();
		}
		finally{
			try { in.close(); } catch(Throwable ex){}
		}
	}	
}
