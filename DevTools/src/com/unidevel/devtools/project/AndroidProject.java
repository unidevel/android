package com.unidevel.devtools.project;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class AndroidProject extends AbstractProject
{
	final static String MANIFEST_FILE="AndroidManifest.xml";
	public static AndroidProject find(File path){
		if(path==null)return null;
		File dir,pdir;
		if(path.isFile())dir=path.getParentFile();
		else dir=path;
		do{
			File f=new File(dir,MANIFEST_FILE);
			if(f.exists()){
				return new AndroidProject(dir);
			}
			pdir=dir.getParentFile();
		}
		while(pdir!=null&&!pdir.equals(dir));
		return null;
	}
	
	File location;
	File manifestFile;
	protected AndroidProject(File path){
		this.location=path;
	}
	
	public File getManifestFile(){
		if(manifestFile==null){
			manifestFile=new File(location,MANIFEST_FILE);
		}
		return manifestFile;
	}
		
	protected Document load(File f) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException
	{
		FileInputStream stream=new FileInputStream(f);
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=factory.newDocumentBuilder();
		Document document=builder.parse(stream);
		return document;
	}
	
	protected void save(File f,Document document) throws TransformerConfigurationException, TransformerException, FileNotFoundException
	{
		FileOutputStream stream = new FileOutputStream(f);
		TransformerFactory tFactory =
			TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(stream);
		transformer.transform(source, result); 
	}
}
