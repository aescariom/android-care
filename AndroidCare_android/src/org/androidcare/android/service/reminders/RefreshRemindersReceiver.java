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
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String strHours = prefs.getString("reminderResquestInterval", "24");
        
        int hours = 24;
        try{
            hours = Integer.parseInt(strHours);
        }catch(NumberFormatException ex){
            Log.d("RefreshReminders", "Error converting: " + strHours + ". We will use the default value...");
        }
        if(hours <= 0) hours = 1;
        
        Log.d("RefreshReminders", "The reminders will be reloaded in " + hours + " hours");
        
        long fourHours = hours*60*60*1000;

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RefreshRemindersReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + fourHours, pendingIntent);        
    }

}
