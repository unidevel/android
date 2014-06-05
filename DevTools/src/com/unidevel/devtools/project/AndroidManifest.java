package com.unidevel.devtools.project;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class AndroidManifest
{
	AndroidProject project;
	Document content;
	protected AndroidManifest(AndroidProject project){
		this.project = project;
	}
	
	public void load() throws IOException, SAXException, ParserConfigurationException
	{
		File f=project.getManifestFile();
		content=project.load(f);
	}
	
	public void save()
	{
		
	}
}
