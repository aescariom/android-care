package org.androidcare.android.service;

import org.androidcare.android.service.ConnectionService.ConnectionServiceBinder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

//Receives explicit intents from the AlarmManager of the system. Intents are created by the ConnectionService
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
        Log.d(PushMessagesReceiver.class.getName(), 
              "PowerManager lock acquired by PushMessagesReceiver; lock count: " + wakeLock.toString());
    }
    
    public static synchronized void releaseLock(){        
        if(wakeLock != null && wakeLock.isHeld()){
            try{
                wakeLock.release();
                Log.d(PushMessagesReceiver.class.getName(), 
                      "PowerManager lock released by PushMessagesReceiver; lock count: " + wakeLock.toString());
            } catch (Throwable th) {
                Log.e(PushMessagesReceiver.class.getName(), 
                      "PowerManager lock could not be released by PushMessagesReceiver; lock count: " + wakeLock.toString());
            }
        }
    }
    


}
