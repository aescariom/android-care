package org.androidcare.android.service;

import org.androidcare.android.service.ConnectionService.ConnectionServiceBinder;
import org.androidcare.android.service.location.LocationService;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PowerManager;

public class PushMessagesReceiver extends BroadcastReceiver {

    private ConnectionService connectionService;
    boolean mBound = false;
    
    private static PowerManager.WakeLock wakeLock = null;
    private static final String LOCK_TAG = "org.androidcare.android.service";
    
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionServiceBinder binder = (ConnectionServiceBinder) service;
            connectionService = binder.getService();

            connectionService.processMessageQueue();
            connectionService.scheduleNextSynchronization();
            
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };
    
    public PushMessagesReceiver(){
        super();
    }
    
    @Override
    public void onReceive(Context context, Intent arg1) {
        // waking up the location Service just in case it felt asleep
        context.startService(new Intent(context, LocationService.class));
        // binding the connection Service
        acquireLock(context); // we will have to wait until the service is attached
        context.getApplicationContext().bindService(
                           new Intent(context.getApplicationContext(), ConnectionService.class),
                           mConnection, Context.BIND_AUTO_CREATE);
    }
    
    public static synchronized void acquireLock(Context ctx){
        if(wakeLock == null){
            PowerManager mgr = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
            wakeLock.setReferenceCounted(true);
        }
        wakeLock.acquire();
    }
    
    public static synchronized void releaseLock(){
        if(wakeLock != null){
            try{
                wakeLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        }
    }
    


}
