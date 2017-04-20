package com.neurospeech.wsclient;


public class StringWriter {

	StringBuilder sb = new StringBuilder();
	
	public StringWriter(){
		
	}
	
	public void write(String string) {
		sb.append(string);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
