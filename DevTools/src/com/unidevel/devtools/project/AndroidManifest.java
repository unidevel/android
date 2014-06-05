package com.unidevel.devtools.project;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;

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
	
	public void save() throws FileNotFoundException, TransformerException
	{
		File f=project.getManifestFile();
		project.save(f, content);
	}
	
	public int getVersionCode(){
		String v=content.getDocumentElement().getAttribute("android:versionCode");
		return Integer.valueOf(v);
	}
	
	public void setVersionCode(int code){
		Attr attr=content.getDocumentElement().getAttributeNode("android:versionCode");
		attr.setValue(String.valueOf(code));
	}
	
	public String getPackage(){
		String p=content.getDocumentElement().getAttribute("package");
		return p;
	}
}
