package org.androidcare.android.service.location;

import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.ConnectionService.ConnectionServiceBinder;
import org.androidcare.android.service.location.LocationService;
import org.androidcare.android.service.location.LocationService.LocationServiceBinder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

//Receives explicit intents from the AlarmManager of the system. Intents are created by the ConnectionService
public class UpdateLocationReceiver extends BroadcastReceiver {

    private LocationService locationService;
    boolean mBound = false;
    
    private static PowerManager.WakeLock wakeLock = null;
    private static final String LOCK_TAG = "org.androidcare.android.service";
        
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationServiceBinder binder = (LocationServiceBinder) service;
            locationService = binder.getService();

            locationService.getLocation();
            locationService.scheduleNextUpdate();
            
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };
    
    public UpdateLocationReceiver(){
        super();
    }
    
    @Override
    public void onReceive(Context context, Intent arg1) {
        // binding the connection Service
        acquireLock(context); // we will have to wait until the service is attached
        context.getApplicationContext().bindService(
                           new Intent(context.getApplicationContext(), LocationService.class),
                           mConnection, Context.BIND_AUTO_CREATE);
    }
    
    public static synchronized void acquireLock(Context ctx){
        if(wakeLock == null){
            PowerManager mgr = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
            wakeLock.setReferenceCounted(true);
            Log.d(UpdateLocationReceiver.class.getName(), "PowerManager lock acquired by UpdateLocationReceiver");
        }
        wakeLock.acquire();
    }
    
    public static synchronized void releaseLock(){        
        if(wakeLock != null && wakeLock.isHeld()){
            try{
                wakeLock.release();
                Log.d(UpdateLocationReceiver.class.getName(), "PowerManager lock released by UpdateLocationReceiver");
            } catch (Throwable th) {
                Log.e(UpdateLocationReceiver.class.getName(), "PowerManager lock could not be released by UpdateLocationReceiver");
            }
        }
    }
    


}
