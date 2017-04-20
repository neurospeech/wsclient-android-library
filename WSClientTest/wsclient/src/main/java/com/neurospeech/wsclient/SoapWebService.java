package com.neurospeech.wsclient;


import java.io.*;
import java.util.Date;

import org.w3c.dom.*;

public class SoapWebService extends HttpWebService {
	
	public static String globalBaseUrl = null;
	
	public static void setGlobalBaseUrl(String gurl){
		globalBaseUrl = gurl;
	}
	
	private String _baseUrl = null;
	public String getBaseUrl(){
		return _baseUrl;
	}
	public void setBaseUrl(String b){
		_baseUrl = b;
	}
	
	
	
	private String _url = null;
	public String getUrl()
	{
		return _url;
	}
	public void setUrl(String url)
	{
		_url = url;
	}
	
	protected void executeAsync(Runnable r){
		Thread th = new Thread(r);
		th.start();
	}
	
	protected String getNamespaces(){
		return "";
	}
	
	protected void appendNamespaces(Element e){
		e.setAttribute("", "");
	}
	
	protected SoapRequest buildSoapRequest(String action) throws Exception
	{
		SoapRequest req = new SoapRequest();
		req.soapAction = action;
		String soapDoc  = "<?xml version='1.0' encoding='utf-8'?>" +
						"<soap:Envelope "+ getNamespaces() + "><soap:Header/><soap:Body/></soap:Envelope>";
		Document doc = XmlDocumentBuilder.parseDocument(soapDoc);
		req.document = doc;
		req.root = doc.getDocumentElement();
		appendNamespaces(req.root);
		req.isSoapActionRequired= this.IsSoapActionRequired();
		req.header = WSHelper.getElementNS(req.root, getSoapEnvelopeNS(), "Header");
		return req;
	}
	
	protected boolean IsSoapActionRequired() {
		return true;
	}
	
	
	protected String getServiceUrl(){
		if(_baseUrl!=null && _baseUrl.length()>0){
			return _baseUrl + _url;
		}
		if(globalBaseUrl!=null && globalBaseUrl.length()>0)
		{
			return globalBaseUrl + _url;
		}
		return _url;
	}
	
	protected SoapResponse getSoapResponse(SoapRequest request) throws Exception
	{
		request.serviceUrl = getServiceUrl();
		request.userAgent = getUserAgent();
		
		// build body...
		Element e = WSHelper.getElementNS(request.root, getSoapEnvelopeNS(), "Body");
		e.appendChild(request.method);
		
		/*if(request.header!=null){
			e = WSHelper.getElementNS(request.root, getSoapEnvelopeNS(), "Header");
			e.appendChild(request.header);
		}*/
		
		SoapResponse rs = postXML(request);
		
		// search for fault first...
		Element fault = WSHelper.getElementNS(rs.document, getSoapEnvelopeNS(), "Fault");
		if(fault!=null)	{
			String code = WSHelper.getString(fault, "faultcode", false);
			String text = WSHelper.getString(fault, "faultstring", false);
			SoapFaultException fe = new SoapFaultException(code, text, request.rawHttpRequest);
			throw fe;
		}
		
		rs.header = WSHelper.getElementNS(rs.document,getSoapEnvelopeNS(), "Header");
		rs.body = WSHelper.getElementNS(rs.document,getSoapEnvelopeNS(), "Body");
		return rs;
	}
	
	protected String getContentType(String action) {
		return "text/xml; charset=utf-8";
	}
	
	protected String getSoapEnvelopeNS(){
		return "http://schemas.xmlsoap.org/soap/envelope/";
	}
	
	SoapResponse postXML(SoapRequest request) throws Exception{
		
		SoapResponse sr = new SoapResponse();
		
		try{
			String text = WSHelper.getString(request.document);
			InputStream stream = postWS(request, text);
		
			
			java.io.ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			byte[] buff = new byte[5120];
			do{
				int count = stream.read(buff, 0, 5120);
				if(count<=0)
					break;
				bos.write(buff,0,count);
				
			}while(true);
			
			sr.httpResponse = new String(bos.toByteArray());
			
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			
			Document d = XmlDocumentBuilder.parseDocument(bin);
			
			
			
			d.normalize();
			sr.document = d.getDocumentElement();
		}catch(Exception ex)
		{
			throw new SoapFaultException("Server Error", ex.toString(), sr.httpResponse);
		}
		return sr;
	}
	

}
