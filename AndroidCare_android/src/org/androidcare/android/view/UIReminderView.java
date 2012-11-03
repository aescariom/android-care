package org.androidcare.android.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.androidcare.android.service.ReminderService;
import org.androidcare.android.util.Reminder;
import org.androidcare.common.ReminderStatusCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

public abstract class UIReminderView extends RelativeLayout {
	
	// this handler will allow us to wait to the service
	protected final Handler handler = new Handler(); 
	// this connection will allow us to interact with the service
	protected LocalServiceConnection conn = new LocalServiceConnection();
	protected Reminder reminder;

	public UIReminderView(Context context, Reminder reminder) {
		super(context);
		this.reminder = reminder;
		reschedule(reminder);
	}
	
	public void performed(){
		 postData(ReminderStatusCode.ALERT_DONE);
	}
	
	public void notPerformed(){
		 postData(ReminderStatusCode.ALERT_IGNORED);
	}
	
	public void delayed(){
		
	}
	
	public void displayed(){
		 postData(ReminderStatusCode.ALERT_DISPLAYED);
	}
	
	public void finish(){
		Activity parent = (Activity)getContext();
		parent.finish();
	}
	
	protected Window getWindow(){
		Activity parent = (Activity)getContext();
		return parent.getWindow();
	}

	protected void reschedule(final Reminder a) {
		//1 - connecting with the local service
		Intent intent = new Intent(getContext(), ReminderService.class);
		getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
		
		//2 - delegating to a thread the rescheduling
		Runnable r = new Runnable()  { 
            public void run() { 
            	//4 - rescheduling
            	if (conn.getService() != null)  { 
                      conn.getService().schedule(a);
                } else{
                	Log.e(getClass().getName(), "Could not connect with the service, the alert won't be scheduled again");
                }
            } 
        }; 
        
        //3 - delaying the schedule. Then the activity and the service should be connected
        handler.postDelayed(r, 4000L); 		
	}
	
	protected void postData(ReminderStatusCode statusCode) {
		//1 - Set Connection
	    final HttpPost httppost = new HttpPost(ReminderService.REMINDERS_LOG_URL);

        //2 - Add your data
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("alertId", String.valueOf(reminder.getId())));
        nameValuePairs.add(new BasicNameValuePair("statusCode", String.valueOf(statusCode.getCode())));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			//3 - connecting with the local service
			Intent intent = new Intent(getContext(), ReminderService.class);
			getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
			
			//4 - delegating to a thread the rescheduling
			Runnable r = new Runnable()  { 
	            public void run() { 
	            	//5 - rescheduling
	
	        	    try {
	        	        // Execute HTTP Post Request
	        	        HttpResponse response = conn.getService().getHttpClient().execute(httppost);
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
	
	/**
	 * Activity-Service Connection class
	 * @author Alejandro Escario MŽndez
	 *
	 */
	public class LocalServiceConnection implements ServiceConnection{
		
		// service reference
		private ReminderService service;
		
		/**
		 * on connect handler
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			this.service = ((ReminderService.LocalServiceBinder)service).getService();
		}
		
		/**
		 * returns the instance of the service
		 * @return
		 */
		public ReminderService getService(){
			return service;
		}

		/**
		 * 
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;				
		}
	};
}
