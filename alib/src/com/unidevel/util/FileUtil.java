package com.unidevel.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Environment;

public class FileUtil {
	public static boolean newDir(String base, String dir){
		File d=new File(base,dir);
		d.mkdirs();
		return d.isDirectory();
	}

	public static boolean newFile(String path) throws IOException{
		File f=new File(path);
		return newFile(f);
	}
	
	public static boolean newFile(String base, String file) throws IOException{
		File f=new File(base,file);
		return newFile(f);
	}
	
	public static boolean newFile(File f) throws IOException{
		if (f.isFile()) return false;
		f.getParentFile().mkdirs();
		try{
			save(f.getPath(),"");
			return true;
		}
		catch(Throwable ex){
		}
		return false;
	}

	public static boolean delete(String path){
		File f=new File(path);
		return delete(f);
	}
	
	public static boolean rename(String from, String to){
		File f1=new File(from);
		File f2=new File(to);
		if(!f1.exists()||f2.exists())
			return false;
		File dir=f2.getParentFile();
		if(dir.exists()||dir.mkdirs()){
			return f1.renameTo(f2);
		}
		return false;
	}
	
	public static boolean delete(File f){
		if(f.isDirectory()){
			File[] files=f.listFiles();
			for(File file:files){
				delete(file);
			}
		}
		return f.delete();
	}

	public static String dataDir() {
		return Environment.getDataDirectory().getPath();
	}

	public static String rootDir() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static String load(String path) throws IOException{
		File file = new File(path);
		return load(file);
	}
	
	public static String load(File file) throws IOException {
		return load(file, null);
	}
	
	public static String load(File file, String enc) throws IOException {
		StringBuffer buf = new StringBuffer();
		FileInputStream in = null;
		try {
			BufferedReader fr ;
			in = new FileInputStream(file);
			if ( enc != null ) 
			{
				fr = new BufferedReader(new InputStreamReader(in));	
			}
			else
			{
				fr = new BufferedReader(new InputStreamReader(in, enc));				
			}
			char cbuf[] = new char[8192];
			for (int l = fr.read(cbuf); l > 0; l = fr.read(cbuf)) {
				buf.append(cbuf, 0, l);
			}
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return buf.toString();
	}

	public static void save(String file, String value) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(value);
			fw.flush();
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static void copy(String sourcePath, String destPath) throws IOException{
		File srcFile = new File(sourcePath);
		File destFile = new File(destPath);
		copy(srcFile, destFile);
	}
	
	public static void copy(File srcFile, File destFile) throws IOException{
		if( !srcFile.exists() ) throw new FileNotFoundException(srcFile.getPath()+" not found!");
		if ( srcFile.isFile() )
		{
			if ( destFile.exists() && destFile.isDirectory() ) 
			{
				throw new IOException(destFile.getPath()+" is directory!");
			}
			if ( !destFile.exists() ) 
			{
				if ( !destFile.getParentFile().exists() )
				{
					if ( !destFile.getParentFile().mkdirs() ) {
						throw new IOException("Can't create directory "+destFile.getParentFile().getPath()+"!");
					}
				}
			}
			copyFile(srcFile, destFile);
		}
		else
		{
			if ( destFile.exists() && (!destFile.isDirectory()) ) 
			{
				throw new IOException(destFile.getPath()+" is not a directory!");
			}
			if ( !destFile.exists() ) {
				if ( !destFile.mkdirs() )
					throw new IOException("Can't create directory "+destFile.getPath()+"!");
			}
			copyDir(srcFile, destFile);
		}
	}
	
	private static void copyDir(File srcDir, File destDir) throws IOException {
		File[] srcFiles = srcDir.listFiles();
		for ( File srcFile : srcFiles )
		{
			File destFile = new File(destDir, srcFile.getName());
			if ( srcFile.isDirectory() ) 
			{
				if (!destFile.mkdir()){
					throw new IOException("Can't create directory "+destFile.getPath()+"!");
				}
				copyDir(srcFile, destFile);
			}
			else
			{
				copyFile(srcFile, destFile);
			}
		}
	}
	
	private static void copyFile(File src, File dest) throws IOException{
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			byte[] buf = new byte[1024*32];
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			int len;
			while ( (len=in.read(buf)) > 0 ) {
				out.write(buf,0,len);
			}
		}
		finally {
			try { in.close(); } catch(Throwable ex){}
			try { out.close(); } catch(Throwable ex){}
		}
	}
	
	public static void copy(InputStream in, File file) throws IOException{
		FileOutputStream out = null;
		try {
			byte[] buf = new byte[1024*32];
			out = new FileOutputStream(file);
			int len;
			while ( (len=in.read(buf)) > 0 ) {
				out.write(buf,0,len);
			}
		}
		finally {
			try { out.close(); } catch(Throwable ex){}
		}		
	}
}
