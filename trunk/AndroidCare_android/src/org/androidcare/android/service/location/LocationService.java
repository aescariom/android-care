package org.androidcare.android.service.location;

import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.ConnectionService.ConnectionServiceBinder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
    private final String tag = this.getClass().getName();

    /* Location parameters */
    private LocationManager locationManager;
    private final int minSeconds = 5*60*1000; // 5 min
    private final int minDistance = 100; // 100 meters

    private ConnectionService connectionService;
    boolean mBound = false;
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
            }else{
                bindConnectionService();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(tag, "Location service started: " + this.hashCode());

        bindConnectionService();
        // setting the criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String bestProvider = locationManager.getBestProvider(criteria, true);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(bestProvider, this.minSeconds,
                this.minDistance, locationListener);
        
        return result;
    }

    private void bindConnectionService() {
        getApplicationContext().bindService(
                           new Intent(getApplicationContext(), ConnectionService.class),
                           mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
