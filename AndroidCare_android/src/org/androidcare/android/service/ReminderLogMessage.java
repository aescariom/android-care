package org.androidcare.android.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.androidcare.android.util.Reminder;
import org.androidcare.common.ReminderStatusCode;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

public class ReminderLogMessage extends Message {
	protected Reminder reminder;
	private ReminderStatusCode statusCode;
	
	public ReminderLogMessage(Reminder r, ReminderStatusCode statusCode){
		super();
		this.url = ReminderService.REMINDERS_LOG_URL;
		this.reminder = r;
		this.statusCode = statusCode;
	}

	@Override
	public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
		//1 - Set Connection
		HttpPost httppost = new HttpPost(this.url);
		
		//2 - Add your data
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("reminderId", String.valueOf(this.reminder.getId())));
		nameValuePairs.add(new BasicNameValuePair("statusCode", String.valueOf(this.statusCode.getCode())));
			
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		return httppost;
	}

}