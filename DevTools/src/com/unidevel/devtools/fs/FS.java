package com.unidevel.devtools.fs;
import java.io.*;

public class FS
{
	static ISystem sys;
	
	public static interface ISystem{
		File getExternalDir() throws FileNotAvailableException;
			
		File getTempDir() throws FileNotAvailableException;
		
		File getAppDir() throws FileNotAvailableException;
	}
	
	public static void setSystem(ISystem system){
		sys = system;
	}
	
	public static File extern(String path) throws FileNotAvailableException{
		File f = new File(sys.getExternalDir(), path);
		return f;
	}
	
	public static File temp(String path) throws FileNotAvailableException{
		File f = new File(sys.getTempDir(), path);
		return f;
	}
	
	public static File app(String path) throws FileNotAvailableException{
		File f = new File(sys.getAppDir(), path);
		return f;
	}
}
