package org.androidcare.android.service;

public class ReminderServiceException extends Exception {
	protected final String description;
	
	public ReminderServiceException(String str){
		super();
		this.description = str;
	}
	
	public String getDescription(){
		return this.description;
	}
}
