package com.unidevel.webide;
import android.content.*;
import android.preference.*;
import java.io.*;
import android.os.*;

public class JavaScriptLibrary
{
	final Context ctx;
	public JavaScriptLibrary(Context context)
	{
		this.ctx=context;
	}
	
	public SharedPreferences pref()
	{
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this.ctx);
		return pref;
	}
	
	public String getPref(String name){
		return pref().getString(name,null);
	}
	
	public void putPref(String name,String value){
		pref().edit().putString(name,value).commit();
	}
	
	public String[] listDir(String dir){
		File d=new File(dir);
		return d.list();
	}
	
	public boolean isFile(String path){
		File f = new File(path);
		return f.isFile();
	}
	
	public String getFile(String name){
		File d= ctx.getFilesDir();
		File f=new File(d,name);
		return f.getPath();
	}
	
	public String dataDir(){
		return Environment.getDataDirectory().getPath();
	}
	
	public String rootDir(){
		return Environment.getRootDirectory().getPath();
	}
	
	public String read(String file){
		StringBuffer buf=new StringBuffer();
		BufferedReader fr=null;
		try
		{
			fr = new BufferedReader(new FileReader(file));
			char cbuf[]=new char[8192];
			for(int l=fr.read(cbuf);l>0;l=fr.read(cbuf)){
				buf.append(cbuf,0,l);
			}
			
		}
		catch (Exception e)
		{
			return null;
		}
		finally{
			try
			{
				fr.close();
			}
			catch (Exception e)
			{}
		}
		return buf.toString();
	}
	
	public boolean write(String file,String value){
		FileWriter fw=null;
		try
		{
			fw = new FileWriter(file);
			fw.write(value);
			fw.flush();
			return true;
		}
		catch (IOException e)
		{
			
		}
		finally{
			try
			{
				fw.close();
			}
			catch (Exception e)
			{}
		}
		return false;
	}
}
