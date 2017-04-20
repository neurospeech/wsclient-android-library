package com.neurospeech.wsclient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SoapRequest {
	public Document document;
	public Element root;
	//public Element body;
	public Element header;
	public Element method;
	
	public String methodName;
	public String soapAction;
	
	public String rawHttpRequest;
	
	public String serviceUrl;
	//public String contentType;
	public String userAgent;
	public boolean isSoapActionRequired;
	
	public Element createElement(String name){
		return document.createElement(name);
	}
}
