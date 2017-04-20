package com.neurospeech.wsclient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class XmlDocumentBuilder {

	public static Document createDocument() throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setCoalescing(true);
		
		Document doc = dbf.newDocumentBuilder().newDocument();
		doc.normalize();
		return doc;
	}
	
	public static Document parseDocument(String doc) throws Exception
	{
		ByteArrayInputStream sr = new ByteArrayInputStream(doc.getBytes());
		return parseDocument(sr);
	}

	public static Document parseDocument(InputStream sr) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setCoalescing(true);
		//dbf.setValidating(false);
		Document xdoc = dbf.newDocumentBuilder().parse(new InputSource(sr));
		xdoc.normalize();
		return xdoc;
	}
	
	public static Element createElement(Document doc, String name){
		return doc.createElement(name);
	}
}
