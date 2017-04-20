package com.neurospeech.wsclient;

import android.util.Xml;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;

//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


public class HttpWebService {
	
	//private static DefaultHttpClient client = new DefaultHttpClient();



	private int _socketTimeout = 0;
	public void setSocketTimeout(int socketTimeout) {
		this._socketTimeout = socketTimeout;
	}

	public int getSocketTimeout() {
		return _socketTimeout;
	}

	private int _connectionTimeout = 0;
	public void setConnectionTimeout(int connectionTimeout) {
		this._connectionTimeout = connectionTimeout;
	}

	public int getConnectionTimeout() {
		return _connectionTimeout;
	}
	
	
	@SuppressWarnings("rawtypes")
	private Hashtable _httpHeaders = null;
	
	

	@SuppressWarnings("rawtypes")
	public void setHttpHeaders(Hashtable httpHeaders) {
		this._httpHeaders = httpHeaders;
	}

	@SuppressWarnings("rawtypes")
	public Hashtable getHttpHeaders() {
		return _httpHeaders;
	}

//	protected DefaultHttpClient GetHttpClient() throws Exception{
//		if(ignoreCertificateErrors){
//	        X509HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//
//	        DefaultHttpClient client = new DefaultHttpClient();
//
//	        SchemeRegistry registry = new SchemeRegistry();
//	        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//	        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
//	        registry.register(new Scheme("https", socketFactory, 443));
//	        SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
//	        DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
//
//	        // Set verifier
//	        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//
//			workAroundReverseDnsBugInHoneycombAndEarlier(httpClient);
//	        return httpClient;
//
//		}else{
//			workAroundReverseDnsBugInHoneycombAndEarlier(client);
//		}
		
//		return client;
//	}
	
	@SuppressWarnings("rawtypes")
	protected void setHeaders(SoapRequest request, Hashtable headers)
	{
		
	}
	
	public void setIgnoreCertificateErrors(boolean ignoreCertificateErrors) {
		this.ignoreCertificateErrors = ignoreCertificateErrors;
	}

	public boolean getIgnoreCertificateErrors() {
		return ignoreCertificateErrors;
	}

	private boolean ignoreCertificateErrors = false;
	
	protected String getUserAgent(){
		return "WSClient Android";
	}
	
	protected boolean IsSoapActionRequired(){
		return true;
	}
	
	protected String getContentType(String action)
	{
		return "";
	}
	
	@SuppressWarnings("rawtypes")
	protected InputStream postWS(SoapRequest request, String xml) throws Exception{

		String url = request.serviceUrl;
		String contentType = getContentType(request.soapAction);
		String userAgent = request.userAgent;


		WSHttpPost post = new WSHttpPost(new URL(url));
		
		post.setHeader("Content-Type", contentType);

		request.rawHttpRequest = url + "\r\n";
		request.rawHttpRequest += "Content-Type: " + contentType + "\r\n";
		if(userAgent!=null){
			request.rawHttpRequest += "User-Agent: " + userAgent + "\r\n";
			post.setHeader("User-Agent", userAgent);
		}
		if(this.IsSoapActionRequired()){
			post.setHeader("SOAPAction",request.soapAction);

			request.rawHttpRequest += "SOAPAction: " + request.soapAction + "\r\n";
		
		}
		
		Hashtable headers = new Hashtable();
		
		setHeaders(request, headers);
		

		Enumeration en = headers.keys();
		while(en.hasMoreElements()){
			String key = (String)en.nextElement();
			post.setHeader(key, (String)headers.get(key));
		}
		
		if(this._httpHeaders!=null){
			 Enumeration enKey = _httpHeaders.keys();
			 while(enKey.hasMoreElements()){
				 String k = (String)enKey.nextElement();
				 String v = (String)_httpHeaders.get(k);
				 if(v!=null){
					 post.setHeader(k,v);
				 }
			 }
		}

		if(this._connectionTimeout>0){
			//HttpConnectionParams.setConnectionTimeout(c.getParams(), _connectionTimeout);
			post.setConnectionTimeout(this._connectionTimeout);
		}

		if(this._socketTimeout>0){
			//HttpConnectionParams.setSoTimeout(c.getParams(), _socketTimeout);
			post.setTimeout(this._connectionTimeout);
		}

		
		post.setEntity(xml, "UTF8");
		
		request.rawHttpRequest += "\r\n";
		request.rawHttpRequest += xml;
		
		//HttpResponse response = c.execute(req);
		
		//InputStream stream = response.getEntity().getContent();
		InputStream stream = post.getResponseStream();
		return stream;
		
	}
	
//	private void workAroundReverseDnsBugInHoneycombAndEarlier(DefaultHttpClient client) {
//
//
//
//		HttpProtocolParams.setUseExpectContinue(client.getParams(), false);
//
//		client.setHttpRequestRetryHandler(new HttpRequestRetryHandler(){
//
//			@Override
//			public boolean retryRequest(IOException ex, int retry,
//					HttpContext arg2) {
//				if(retry>=5){
//					return false;
//				}
//				if(ex instanceof NoHttpResponseException){
//					System.out.println("WSClient++ trying for " + String.valueOf(retry));
//					return true;
//				}else if (ex instanceof ClientProtocolException){
//					System.out.println("WSClient++ trying for " + String.valueOf(retry));
//					return true;
//				}
//				return false;
//			}});
//
//
//
//
//	    // Android had a bug where HTTPS made reverse DNS lookups (fixed in Ice Cream Sandwich)
//	    // http://code.google.com/p/android/issues/detail?id=13117
//	    SocketFactory socketFactory = new LayeredSocketFactory() {
//	        SSLSocketFactory delegate = SSLSocketFactory.getSocketFactory();
//
//
//
//	        @Override public Socket createSocket() throws IOException {
//	            return delegate.createSocket();
//	        }
//
//	        @Override public Socket connectSocket(Socket sock, String host, int port,
//	                InetAddress localAddress, int localPort, HttpParams params) throws IOException {
//	            return delegate.connectSocket(sock, host, port, localAddress, localPort, params);
//	        }
//	        @Override public boolean isSecure(Socket sock) throws IllegalArgumentException {
//	            return delegate.isSecure(sock);
//	        }
//	        @Override public Socket createSocket(Socket socket, String host, int port,
//	                boolean autoClose) throws IOException {
//	            injectHostname(socket, host);
//	            SSLSocket ss = (SSLSocket)delegate.createSocket(socket, host, port, autoClose);
//	            ss.setEnabledProtocols(new String[] {"SSLv3"});
//	            return ss;
//	        }
//
//
//
//	        private void injectHostname(Socket socket, String host) {
//	            try {
//	                Field field = InetAddress.class.getDeclaredField("hostName");
//	                field.setAccessible(true);
//	                Object val = field.get(socket.getInetAddress());
//	                if(val!=null){
//	                	System.out.println("WSClient++ " + val.toString());
//	                }else{
//	                	System.out.println("WSClient++ hostName is not set");
//	                }
//	                field.set(socket.getInetAddress(), host);
//	                System.out.println("WSClient++ " + field.get(socket.getInetAddress()).toString());
//		            System.out.println("WSClient++ Successfully injected " + host);
//	            } catch (Exception ignored) {
//	            }
//	        }
//	    };
//
//	    client.getConnectionManager().getSchemeRegistry()
//	            .register(new Scheme("https", socketFactory, 443));
//	}
	
	

}
