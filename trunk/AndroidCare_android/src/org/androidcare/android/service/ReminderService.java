package org.androidcare.android.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.androidcare.android.ReminderReceiver;
import org.androidcare.android.util.NoDateFoundException;
import org.androidcare.android.util.Reminder;
import org.androidcare.android.util.TimeManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Main Local Service
 * @author Alejandro Escario Méndez
 *
 */
public class ReminderService extends ConnectionService {
	public static final String REMINDERS_URL = ConnectionService.APP_URL + "api/retrieveReminders";
	public static final String REMINDERS_LOG_URL = ConnectionService.APP_URL + "api/addReminderLog";
	public static final String POSITION_LOG_URL = ConnectionService.APP_URL + "api/addPosition";

	private static final int REMINDER_REQUEST_CODE = 0;
	
	// communcation
	private final List<Intent> reminderIntents = new ArrayList<Intent>();
	private final IBinder binder = new ReminderServiceBinder();
	// service info
	private final String tag = this.getClass().getName();
	// location
	private LocationManager locationManager;
	private int minSeconds = 300000; // 5 min
	private int minDistance = 20; // 20 meters
	private LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	    	ReminderService.this.pushMessage(new GeoMessage(location));
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		Log.i(tag, "Service started");
		
		this.pushMessage(new GetSchedulableRemindersMessage(this));
		
		// Acquire a reference to the system Location Manager
		this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.minSeconds, this.minDistance, locationListener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void schedule(Reminder[] reminders){
		cancelAllReminders();
		for(Reminder r : reminders){
			try {
				this.schedule(r);
			} catch (NoDateFoundException e) {
				Log.e(tag, "Reminder not scheduled: " + r.getTitle());
				e.printStackTrace();
			}
		}
	}

	public void schedule(Reminder r) throws NoDateFoundException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(TimeManager.getNextTimeLapse(r, cal.getTime()));
		
		Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
		intent.setData(Uri.parse("androidCare://" + r.getId() + ".- " + r.getTitle()));
		
		intent.putExtra("reminder", r);
		PendingIntent sender = PendingIntent.getBroadcast(this, 
				ReminderService.REMINDER_REQUEST_CODE, intent, 
				Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		
		Log.i(tag, "Reminder scheduled: " + r.getTitle() + " @ " + cal.getTime().toString());
	}

	private void cancelAllReminders() {
		for(Intent intent : this.reminderIntents){
			PendingIntent sender = PendingIntent.getBroadcast(this, 
					ReminderService.REMINDER_REQUEST_CODE, intent, 
					Intent.FLAG_GRANT_READ_URI_PERMISSION);
			sender.cancel();
			this.reminderIntents.remove(intent);
		}
	}

	public IBinder getBinder() {
		return binder;
	}

	/**
	 * this class will allow us to connect activities with the 
	 * running instance of this service
	 */
	public class ReminderServiceBinder extends Binder{
		public ReminderService getService(){
			return ReminderService.this;
		}
	}
}
