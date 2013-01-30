package com.unidevel.andmaze;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("UseSparseArrays")
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
	
	@SuppressWarnings("deprecation")
	public void save(){
		Properties p = new Properties();
		for(int key : scores.keySet()){
			List<Long> l = scores.get(key);
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
