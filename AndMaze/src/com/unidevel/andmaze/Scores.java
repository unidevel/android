package com.unidevel.andmaze;
import java.util.*;
import android.content.*;
import android.app.*;
import java.io.*;

public class Scores
{
	Context context;
	Map<Integer,List<Long>> scores;
	public Scores(Context context){
		scores = new HashMap<Integer,List<Long>>();
		this.context=context;
	}
	
	public int addScore(int level,long time){
		List<Long> list = scores.get(level);
		if (list==null){
			list=new ArrayList<Long>();
		}
		list.add(time);
		Collections.sort(list);
		Collections.reverse(list);
		int pos=list.indexOf(time);
		while(list.size()>5){
			list.remove(5);
		}
		return pos;
	}
	
	public void save(){
		Properties p = new Properties();
		for(int key : scores.keySet()){
			List l = scores.get(key);
			String s="";
			for(long t:l){
				s+=","+t;
			}
			if(s.length()>0)s=s.substring(1);
			p.put(key,s);
		}
		try
		{
			p.save(new FileOutputStream(getScoreFile()), "");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public void load(){
		Properties p = new Properties();
		try
		{
			p.load(new FileInputStream(getScoreFile()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		for (Object k: p.keySet()){
			int key = Integer.valueOf(String.valueOf(k));
			String s=p.getProperty(k.toString());
			String[] v=s.split(",");
			for(String t:v){
				this.addScore(key,Long.valueOf(t));
			}
		}
	}
	
	private File getScoreFile(){
		File file=new File(this.context.getFilesDir(),"scores");
		return file;
	}
}
