package org.androidcare.android.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public abstract class Message implements Comparable {
	protected String url;
	protected Date creationDate;
	
	public Message(){
		creationDate = new Date();
	}
	
	public void onPreSend(){
	}
	
	public void onPostSend(HttpResponse response) throws InvalidMessageResponseException{
		if(response == null){
			throw new InvalidMessageResponseException();
		}
	}

	public abstract HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException;
	
	public final int compareTo(Object o){
		Message m = (Message) o;
		return this.creationDate.compareTo(m.getCreationDate());
	}
	
	public Date getCreationDate(){
		return this.creationDate;
	}
}
