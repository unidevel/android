package com.unidevel.andevtools.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Project {
	String path;
	boolean loaded;
	private static void filter(File[] files, File[] result, HashMap<String, Integer> paths, Set<String> ignores)
	{
		for (int i = 0; i < files.length; ++ i)
		{
			File f = files[i];
			if ( ignores.contains(f.getName()) )
				continue;
			Integer pos = paths.get(f.getName());
			if ( pos != null )
			{
				result[pos] = f;
			}
			else
			{
				if ( f.isDirectory() )
				{
					File[] items = f.listFiles();
					filter(items, result, paths, ignores);
				}
			}
		}
	}
	
	public static File[] find(String base, String[] paths, String[] ignores)
	{
		File dir = new File(base);
		File[] files = dir.listFiles();
		HashMap<String, Integer> s1 = new HashMap<String, Integer>();
		HashSet<String> s2 = new HashSet<String>();
		int n = 0;
		if (paths != null )
		{
			n = paths.length;
			for (int i = 0; i < paths.length; ++ i){
				String path = paths[i];
				s1.put(path, i);
			}
		}
		if (ignores != null)
		{
			for ( String s: ignores) s2.add(s);
		}
		File[] results = new File[n];
		filter(files, results, s1, s2);
		return results;
	}
	
	public Project(String path)
	{
		this.path = path;
		this.loaded = false;
	}
	
	public void load() throws Exception
	{
		File[] items = find(this.path, new String[]{
				"AndroidManifest.xml",
				"res"
		}, new String[]{"bin"});
		LogManager.getLog().trace("Load projects => %s", Arrays.asList(items));
	}
	
	public boolean isLoaded()
	{
		return loaded;
	}
	
	public static void main(String[] args) throws Exception
	{
		URL url = Project.class.getClassLoader().getResource(".");
		File dir = new File(url.getFile());
		String base = new File(dir.getParentFile().getParentFile(), "AnDevTools").getPath();
		Project project = new Project(base);
		project.load();
		
	}
}
