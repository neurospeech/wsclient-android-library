package com.neurospeech.wsclient;

public class Soap12WebService extends SoapWebService {
	
	protected String getSoapEnvelopeNS() {
		return "http://www.w3.org/2003/05/soap-envelope";
	}
	
	protected String getContentType(String action) {
		if(action!=null)
			return "application/soap+xml; charset=utf-8; action=\""+ action +"\"";
		return "application/soap+xml; charset=utf-8";
	}
	
	protected boolean IsSoapActionRequired() {
		return false;
	}

}
