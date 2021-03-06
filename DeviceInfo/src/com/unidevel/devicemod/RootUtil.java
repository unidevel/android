package com.unidevel.devicemod;
import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;

public class RootUtil
{
	public static boolean isRooted()
	{
		File dataDir=Environment.getDataDirectory();
		File file;
		for(int n=0;true;++n){
			file=new File(dataDir,"rooted."+n);
			if(!file.exists())break;
		}
		run("echo \"rooted\">"+file.getPath());
		try{
			return file.exists();
		}
		finally{
			run("rm -f "+file.getPath());
		}
	}
	
	public static boolean hasBusybox()
	{
		try{
			java.lang.Process proc=Runtime.getRuntime().exec("busybox");
			return proc.waitFor()==0;
		}
		catch(Throwable ex){
			Log.e("CHECK BUSYBOX",ex.getMessage(),ex);
		}
		return false;
	}
	
	public static void run(String cmd)
	{
		java.lang.Process proc = null;
		Log.i("RUN",cmd);
		try
		{
			proc = Runtime.getRuntime().exec("su");
			proc.getOutputStream().write((cmd+"\nexit\n").getBytes());
			proc.getOutputStream().flush();
			proc.waitFor();
		}
		catch (Exception ex)
		{
			Log.e("RUN",ex.getMessage(),ex);
		}
	}
	
	public static int runWithResult(String cmd, List<String> outputs) throws Exception
	{
		java.lang.Process proc = null;
		Log.i("RUNWITHRESULT",cmd);
		
		{
			proc = Runtime.getRuntime().exec("su");
			proc.getOutputStream().write((cmd+"\necho $?\nexit\n").getBytes());
			proc.getOutputStream().flush();
			InputStream stdout = proc.getInputStream();
			StringBuffer buf=new StringBuffer();
			int exitCode=getExitCode(stdout,buf);
			outputs.add(buf.toString());
			Log.i("STDOUT",outputs.get(0));
			InputStream stderr = proc.getErrorStream();
			outputs.add(toString(stderr));
			Log.i("STDERR",outputs.get(1));
			proc.waitFor();
			return exitCode;
		}
	}
	
	private static String toString(InputStream in) throws IOException
	{
		StringBuffer buf=new StringBuffer();
		char[] cbuf=new char[4096];
		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
		int len=0;
		while((len=reader.read(cbuf))>0){
			buf.append(cbuf,0,len);
		}
		return buf.toString();
	}
	
	private static int getExitCode(InputStream in,StringBuffer buf) throws IOException
	{
		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
		String line,lastLine=null;
		while((line=reader.readLine())!=null){
			if(lastLine!=null){
				buf.append(lastLine).append('\n');
			}
			lastLine=line;
		}
		return Integer.valueOf(lastLine).intValue();
	}
}
