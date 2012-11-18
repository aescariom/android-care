package org.androidcare.android.service;

public class ConnectionServiceException extends Exception {
	protected final String description;
	
	public ConnectionServiceException(String str){
		super();
		this.description = str;
	}
	
	public String getDescription(){
		return this.description;
	}
}
