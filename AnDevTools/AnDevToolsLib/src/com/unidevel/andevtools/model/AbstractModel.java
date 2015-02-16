package com.unidevel.andevtools.model;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AbstractModel {
	String path;

	public String getPath() {
		return path;
	}

	protected static Document loadXML(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(file);
	}

	protected static void saveXML(Document document, File file)
			throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();

		DOMSource domSource = new javax.xml.transform.dom.DOMSource(
				document.getDocumentElement());
		javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(
				file);

		transformer.transform(domSource, result);
	}
}
