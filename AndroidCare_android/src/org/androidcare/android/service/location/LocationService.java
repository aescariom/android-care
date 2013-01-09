package org.androidcare.android.service.location;

import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.reminders.GetRemindersMessage;
import org.androidcare.android.service.reminders.ReminderService;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends ConnectionService {
    private final String tag = this.getClass().getName();

    private LocationManager locationManager;
    private int minSeconds = 300000; // 5 min
    private int minDistance = 20; // 20 meters
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            LocationService.this.pushLowPriorityMessage(new LocationMessage(location));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(tag, "Location service started");

        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.minSeconds,
                this.minDistance, locationListener);
        
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
