package org.androidcare.android.service.alarms;

import android.content.*;
import android.os.IBinder;
import android.os.PowerManager;
import org.androidcare.android.service.ConnectionService;

public class DownloadAlarmsReceiver extends BroadcastReceiver {

    private static final String TAG = DownloadAlarmsReceiver.class.getName();
    private AlarmDownloadService downloadAlarmsService;

    private static PowerManager.WakeLock wakeLock = null;
    private static final String LOCK_TAG = "org.androidcare.android.service.GetAlarms";

    private ConnectionService connectionService;
    boolean mBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionService.ConnectionServiceBinder binder = (ConnectionService.ConnectionServiceBinder) service;
            connectionService = binder.getService();

            connectionService.pushMessage(new GetAlarmsMessage());

            mBound = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };

    public DownloadAlarmsReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        /*
        context.getApplicationContext().bindService(
                new Intent(context.getApplicationContext(), ConnectionService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        */

        /*
        Calendar cal = Calendar.getInstance();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String strHours = prefs.getString("reminderResquestInterval", "4");

        int hours = 4;
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

        cleanReminderCache(context);

        */
    }

}

