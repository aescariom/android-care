package org.androidcare.android.service.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getName();
            
    // Binder given to clients
    private final IBinder mBinder = new LocationServiceBinder();
    boolean mBound = false;

    private LocationRetreiver locationRetreiver = new LocationRetreiver(this);
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Location service started: " + this.hashCode());

        getLocation();
        scheduleNextUpdate();
        
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stopping service");
        cancelUpdates();
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    protected void getLocation() {
        locationRetreiver.getLocation();
        UpdateLocationReceiver.releaseLock();
    }

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    public void scheduleNextUpdate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strMin = prefs.getString("locationUpdatesInterval", "3");        
        int min = 3;
        try{
            min = Integer.parseInt(strMin);
        }catch(NumberFormatException ex){
            Log.d(TAG, "Error converting: " + strMin + ". We will use the default value...");
        }
        if(min <= 0) min = 1;
        
        Log.d(TAG, "Next location update will take place in " + min + " minutes");
        
        int timeLapse = min*60*1000;
        
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), UpdateLocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + timeLapse, pendingIntent);
    }
    
    private void cancelUpdates() {
        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), UpdateLocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
    }

}
