package com.unidevel.deviceinfo;
import android.os.*;
import android.util.*;
import java.io.*;

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
	
	public static void run(String cmd)
	{
		java.lang.Process proc = null;
		Log.i("run",cmd);
		try
		{
			proc = Runtime.getRuntime().exec("su");
			proc.getOutputStream().write((cmd+"\nexit\n").getBytes());
			proc.getOutputStream().flush();
			proc.waitFor();
		}
		catch (Exception e)
		{}
	}
}
