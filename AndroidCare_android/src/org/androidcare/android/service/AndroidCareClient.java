package org.androidcare.android.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.androidcare.common.ReminderStatusCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;

public class AndroidCareClient {

	
	public static void postReminderStatus(int alertId, ReminderStatusCode statusCode) {
		//1 - Set Connection
	    final HttpPost httppost = new HttpPost(LocalService.ALERT_LOG_URL);

        //2 - Add your data
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("alertId", String.valueOf(alertId)));
        nameValuePairs.add(new BasicNameValuePair("statusCode", String.valueOf(statusCode.getCode())));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			//3 - connecting with the local service
			Intent intent = new Intent(this, LocalService.class);
			getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
			
			//4 - delegating to a thread the rescheduling
			Runnable r = new Runnable()  { 
	            public void run() { 
	            	//5 - rescheduling
	
	        	    try {
	        	        // Execute HTTP Post Request
	        	        HttpResponse response = conn.getService().getHttp_client().execute(httppost);
	        	    } catch (ClientProtocolException e) {
	        	    	e.printStackTrace();
	        	        // TODO Auto-generated catch block
	        	    } catch (IOException e) {
	        	    	e.printStackTrace();
	        	        // TODO Auto-generated catch block
	        	    }
	            } 
	        }; 
	        
	        //6 - delaying the schedule. Then the activity and the service should be connected
	        handler.postDelayed(r, 4000L); 	
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	} 
}
