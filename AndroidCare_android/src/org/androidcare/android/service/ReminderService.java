package org.androidcare.android.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.androidcare.android.reminders.NoDateFoundException;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.view.ReminderReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

	private static final int REMINDER_REQUEST_CODE = 0;
	
	// communcation
	private final List<Intent> reminderIntents = new ArrayList<Intent>();
	private final IBinder binder = new ReminderServiceBinder();
	
	// service info
	private final String tag = this.getClass().getName();
	

    //intent broadcast receiver
    private ReminderServiceBroadcastReceiver reminderServiceReceiver = 
    		new ReminderServiceBroadcastReceiver(this);
    private IntentFilter filter = new IntentFilter(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);
	
	// location
	private LocationManager locationManager;
	private int minSeconds = 300000; // 5 min
	private int minDistance = 20; // 20 meters
	private LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	    	ReminderService.this.pushLowPriorityMessage(new GeoMessage(location));
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
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
												this.minSeconds, 
												this.minDistance, 
												locationListener);

        registerReceiver(reminderServiceReceiver, filter);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reminderServiceReceiver);
    }

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void schedule(Reminder[] reminders){
		cancelAllReminders();
		for(Reminder r : reminders){
			this.schedule(r);
		}
	}

	public void schedule(Reminder r) {
		Calendar cal = Calendar.getInstance();
		Date date = r.getNextTimeLapse(cal.getTime());
		if(date == null){
			return;
		}
		cal.setTime(date);
		
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
