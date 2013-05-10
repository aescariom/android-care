package org.androidcare.android.service.reminders;

import java.util.Calendar;

import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.ConnectionService.ConnectionServiceBinder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class RefreshRemindersReceiver extends BroadcastReceiver {

    private ConnectionService connectionService;
    boolean mBound = false;
    
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionServiceBinder binder = (ConnectionServiceBinder) service;
            connectionService = binder.getService();

            connectionService.pushMessage(new GetRemindersMessage());
            
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };
    
    public RefreshRemindersReceiver(){
        super();
    }
    
    @Override
    public void onReceive(Context context, Intent arg1) {
        context.getApplicationContext().bindService(
                           new Intent(context.getApplicationContext(), ConnectionService.class),
                           mConnection, Context.BIND_AUTO_CREATE);
        
        Calendar cal = Calendar.getInstance();
        long fourHours = 4*60*60*1000; // 24h

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RefreshRemindersReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + fourHours, pendingIntent);        
    }

}
