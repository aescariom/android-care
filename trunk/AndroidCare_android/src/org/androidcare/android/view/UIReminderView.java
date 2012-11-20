package org.androidcare.android.view;

import org.androidcare.android.reminders.NoDateFoundException;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.ReminderLogMessage;
import org.androidcare.android.service.ReminderService;
import org.androidcare.common.ReminderStatusCode;

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
	protected ReminderServiceConnection conn = new ReminderServiceConnection();
	protected Reminder reminder;

	public UIReminderView(Context context, Reminder reminder) {
		super(context);
		this.reminder = reminder;
		reschedule(reminder);
	}
	
	public void performed(){
		 postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DONE));
	}
	
	public void notPerformed(){
		 postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_IGNORED));
	}
	
	public void delayed(){
		
	}
	
	public void displayed(){
		 postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DISPLAYED));
	}
	
	public void finish(){
		Activity parent = (Activity)getContext();
		parent.finish();
	}
	
	protected Window getWindow(){
		Activity parent = (Activity)getContext();
		return parent.getWindow();
	}
	
	private void connectWithReminderService() {
		if(this.conn.getService() == null){
			//1 - connecting with the local service
			Intent intent = new Intent(getContext(), ReminderService.class);
			getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
		}
	}

	protected void reschedule(final Reminder a) {
		connectWithReminderService();
		
		//TODO arreglar esta chapuza v
		Runnable r = new Runnable()  { 
            public void run() { 
            	//4 - rescheduling
            	if (conn.getService() != null)  { 
                      try {
						conn.getService().schedule(a);
					} catch (NoDateFoundException e) {
	                	Log.e(getClass().getName(), "Could not connect with the service, the alert won't be scheduled again");
						e.printStackTrace();
					}
                } else{
                	Log.e(getClass().getName(), "Could not connect with the service, the alert won't be scheduled again");
                }
            } 
        }; 
        handler.postDelayed(r, 4000L); 		
	}

	protected void postData(final Message message) {
		connectWithReminderService();
		
		//TODO arreglar esta chapuza v
		Runnable r = new Runnable()  { 
            public void run() { 
            	conn.getService().pushLowPriorityMessage(message);	
            }
		};
		handler.postDelayed(r, 4000L); 
	}
	
	/**
	 * Activity-Service Connection class
	 * @author Alejandro Escario MŽndez
	 *
	 */
	public class ReminderServiceConnection implements ServiceConnection{
		
		// service reference
		private ReminderService service;
		
		/**
		 * on connect handler
		 */
		public void onServiceConnected(ComponentName name, IBinder service) {
			this.service = ((ReminderService.ReminderServiceBinder)service).getService();
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
		public void onServiceDisconnected(ComponentName name) {
			service = null;				
		}
	};
}
