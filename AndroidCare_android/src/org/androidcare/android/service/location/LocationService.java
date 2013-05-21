package org.androidcare.android.service.location;

import java.util.Calendar;

import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.PushMessagesReceiver;
import org.androidcare.android.service.ConnectionService.ConnectionServiceBinder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getName();
            
    /* Location parameters */
    private LocationManager locationManager;
    // Binder given to clients
    private final IBinder mBinder = new LocationServiceBinder();

    private ConnectionService connectionService;
    boolean mBound = false;
    private boolean lastRequestForUpdateFullfilled=false;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionServiceBinder binder = (ConnectionServiceBinder) service;
            connectionService = binder.getService();
            
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };
    
    private LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(Location location) {
            if(mBound){
                connectionService.pushLowPriorityMessage(new LocationMessage(location));
                Log.i(PushMessagesReceiver.class.getName(), 
                      "Location obtained and scheduled to be sent; " + location.toString());
            }else{
                bindConnectionService();
                Log.i(PushMessagesReceiver.class.getName(), 
                      "Connecting with connectionService");
            }
            UpdateLocationReceiver.releaseLock();
            lastRequestForUpdateFullfilled = true;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(LocationListener.class.getName(), "Status changed: " + provider + ": " + status);
        }

        public void onProviderEnabled(String provider) {
            Log.d(LocationListener.class.getName(), "Provider enabled: " + provider);
        }

        public void onProviderDisabled(String provider) {
            Log.d(LocationListener.class.getName(), "Provider disabled: " + provider);
        }
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Location service started: " + this.hashCode());

        bindConnectionService();
        getLocation();
        scheduleNextUpdate();
        
        return result;
    }
    
   public void getLocation() {
       if(!isAirplaneMode()){
           final Criteria criteria = getCriteria();
           lastRequestForUpdateFullfilled=false;
           // Acquire a reference to the system Location Manager
           this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
           Looper looper = Looper.myLooper();
           final Handler myHandler = new Handler(looper);
           myHandler.postDelayed(new Runnable() {
                public void run() {
                    if(!lastRequestForUpdateFullfilled){
                        locationManager.removeUpdates(locationListener);
                        UpdateLocationReceiver.releaseLock();
                        Log.w(TAG, "Location could not be obtained; we stopt trying:");
                    }
                }
           }, 60000);
           
           locationManager.requestSingleUpdate(criteria, locationListener, looper);
       }
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

    private void bindConnectionService() {
       if(!mBound){
           getApplicationContext().bindService(
                           new Intent(getApplicationContext(), ConnectionService.class),
                           mConnection, Context.BIND_AUTO_CREATE);
       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // we must unbind the service in order to destroy ConnectionService
        getApplicationContext().unbindService(mConnection); 
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
        String strMin = prefs.getString("locationUpdatesInterval", "15");        
        int min = 15;
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
