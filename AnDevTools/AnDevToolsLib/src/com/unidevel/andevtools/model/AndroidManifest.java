package com.unidevel.andevtools.model;

import java.io.File;

import org.w3c.dom.Document;

public class AndroidManifest extends AbstractModel  {
	String path;
	Document document;
	public AndroidManifest(String path)
	{
		this.path = path;
	}
	
	public void load() throws Exception
	{
		this.document = this.loadXML(new File(path));
	}
	
	public void save() throws Exception
	{
		this.saveXML(document, new File(path));
	}

	public class Permissions
	{
		
	}
}
