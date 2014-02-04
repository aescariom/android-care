package org.androidcare.android.service.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import org.androidcare.android.service.ConnectionServiceBroadcastReceiver;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.PushMessagesReceiver;

import java.util.Calendar;
import java.util.List;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getName();
            
    /* Location parameters */
    private LocationManager locationManager;    
    // Binder given to clients
    private final IBinder mBinder = new LocationServiceBinder();


    boolean mBound = false;
    private boolean lastRequestForUpdateFullfilled = false;
    int errors = 0;
    
    private LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(Location location) {
            postData(new LocationMessage(location));
            Log.i(PushMessagesReceiver.class.getName(), 
                  "Location obtained and scheduled to be sent; " + location.toString());
            UpdateLocationReceiver.releaseLock();
            lastRequestForUpdateFullfilled = true;
            errors = 0;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "Provider status changed: " + provider + " to " + status);
        }

        public void onProviderEnabled(String provider) {
            Log.e(TAG, "Provider enabled: " + provider); 
        }

        public void onProviderDisabled(String provider) {
            Log.e(TAG, "Provider disabled: " + provider);
        }
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Location service started: " + this.hashCode());

        getLocation();
        scheduleNextUpdate();
        
        return result;
    }
    
   public void getLocation() {
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       boolean force = prefs.getBoolean("forceGPSLocationIfAirplaneMode", false);   
       if(force || !isAirplaneMode()){
           
           final Criteria criteria = getCriteria();

           final Looper looper = getLooper();
           // Acquire a reference to the system Location Manager
           this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
           
           lastRequestForUpdateFullfilled=false;
           List<String> providers = locationManager.getProviders(criteria, true);
           if(providers.size() > 0){
               locationManager.requestSingleUpdate(criteria, locationListener, looper);
               Log.i(TAG, "Asking for position using the criteria");
           }else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
               locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, looper);
               Log.w(TAG, "Any provider found, using NETWORK_PROVIDER");
           }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
               locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
               Log.w(TAG, "Any provider found, using GPS PROVIDER");
           }else{
             Log.e(TAG, "Location could be retrieved because any active radio could be found");
             UpdateLocationReceiver.releaseLock();
           }
       }else{
           UpdateLocationReceiver.releaseLock();
       }
    }

    private Looper getLooper() {
        final Looper looper = Looper.myLooper();
        Handler myHandler = new Handler(looper);
        myHandler.postDelayed(new Runnable() {
             public void run() {
                 if(!lastRequestForUpdateFullfilled){
                     locationManager.removeUpdates(locationListener);
                     Log.w(TAG, "Location could not be obtained; we stopt trying:");
                     errors++;
                     if(errors > 10){
                         locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
                         errors = 0;
                         Log.e(TAG, "Too many consecutive errors, trying to use the GPS");
                     }else{
                         UpdateLocationReceiver.releaseLock();
                     }
                 }
             }
        }, 60000);
        return looper;
    }

    private Criteria getCriteria() {
       // setting the criteria
       Criteria criteria = new Criteria();
       criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
       criteria.setPowerRequirement(Criteria.POWER_LOW);
       criteria.setAltitudeRequired(false);
       criteria.setBearingRequired(false);
       criteria.setSpeedRequired(false);
       criteria.setCostAllowed(true);
       return criteria;
    }
    
    public boolean isAirplaneMode() {
        int settingValue;
        try {
          settingValue = Settings.System.getInt(
              getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
          return settingValue != 0;
        } catch (SettingNotFoundException e) {
          return false; 
        }
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
    
    protected void postData(Message message) {
        Intent intent = new Intent(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);
        intent.putExtra(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE, message);
        getApplicationContext().sendBroadcast(intent);
    }
}
