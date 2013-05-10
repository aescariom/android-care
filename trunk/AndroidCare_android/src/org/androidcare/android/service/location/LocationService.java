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
import android.os.PowerManager;
import android.util.Log;

public class LocationService extends Service {
    private final String tag = this.getClass().getName();
    private static PowerManager.WakeLock wakeLock = null;
    private static final String LOCK_TAG = "org.androidcare.android.service.location";

    /* Location parameters */
    private LocationManager locationManager;

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
            releaseLock();
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
        getLocation();
        
        return result;
    }

   private void getLocation() {
       acquireLock();
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
       locationManager.requestSingleUpdate(criteria, locationListener, null);
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    

    public synchronized void acquireLock(){
        if(wakeLock == null){
            PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
            wakeLock.setReferenceCounted(true);
        }
        wakeLock.acquire();
        Log.d(LocationService.class.getName(), "PowerManager lock acquired by LocationService");
    }
    
    public synchronized void releaseLock(){
        if(wakeLock != null && wakeLock.isHeld()){
            try{
                wakeLock.release();
                Log.d(LocationService.class.getName(), "PowerManager lock acquired by LocationService");
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
                Log.e(LocationService.class.getName(), "PowerManager lock acquired by LocationService");
            }
        }
    }
}
