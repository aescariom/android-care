/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.androidcare.android.service.LocalService;
import org.androidcare.android.util.Reminder;
import org.androidcare.common.AlertStatusCode;
import org.androidcare.android.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * An Alert dialog receiver will display the title and description of the alert.
 * It will also send (sometimes) information to the server
 * @author Alejandro Escario MŽndez
 *
 */
public class AlertDialogReceiver extends Activity {
	
	// this handler will allow us to wait to the service
	private final Handler handler = new Handler(); 
	// this connection will allow us to interact with the service
	LocalServiceConnection conn = new LocalServiceConnection();
	Reminder alert;
	
	/**
	 * Default activity creator method
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//1 - setting up the layout
		setContentView(R.layout.alert);
		
		//2 - turning on the screen, display the activity over the locked screen, keeping the screen on, and unlocking the keyboard
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		//3 - getting the relevant information
		Bundle b = getIntent().getExtras();
		alert = (Reminder) b.getSerializable("alert");
		
		//4 - displaying the information
		TextView txt = (TextView)findViewById(R.id.alarm_title);
		txt.setText(alert.getTitle());
		txt = (TextView)findViewById(R.id.alarm_description);
		txt.setText(alert.getDescription());

		//5 - rescheduling the alert
		reschedule(alert);
		 postData(AlertStatusCode.ALERT_DISPLAYED);
	}

	/**
	 * reschedules an alert. In order to do that, we have to interact with the service
	 * @param a
	 */
	private void reschedule(final Reminder a) {
		//1 - connecting with the local service
		Intent intent = new Intent(this, LocalService.class);
		getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
		
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
	
	public void setDone(View view) {
		 postData(AlertStatusCode.ALERT_DONE);
	     this.finish();
	}
	
	public void postData(AlertStatusCode statusCode) {
		//1 - Set Connection
	    final HttpPost httppost = new HttpPost(LocalService.ALERT_LOG_URL);

        //2 - Add your data
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("alertId", String.valueOf(alert.getId())));
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
	
	public void setUndone(View view) {
		 postData(AlertStatusCode.ALERT_IGNORED);
	     this.finish();
	}
	
	/**
	 * Activity-Service Connection class
	 * @author Alejandro Escario MŽndez
	 *
	 */
	public class LocalServiceConnection implements ServiceConnection{
		
		// service reference
		private LocalService service;
		
		/**
		 * on connect handler
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			this.service = ((LocalService.LocalServiceBinder)service).getService();
		}
		
		/**
		 * returns the instance of the service
		 * @return
		 */
		public LocalService getService(){
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
