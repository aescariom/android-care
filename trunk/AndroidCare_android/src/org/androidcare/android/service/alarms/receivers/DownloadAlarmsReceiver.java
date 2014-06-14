package org.androidcare.android.service.alarms.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.alarms.messages.GetAlarmsMessage;

import java.util.Calendar;

public class DownloadAlarmsReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    private ConnectionService connectionService;
    private DatabaseHelper databaseHelper = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionService.ConnectionServiceBinder binder = (ConnectionService.ConnectionServiceBinder) service;
            connectionService = binder.getService();

            connectionService.pushMessage(new GetAlarmsMessage());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    };

    public DownloadAlarmsReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        context.getApplicationContext().bindService(
                new Intent(context.getApplicationContext(), ConnectionService.class),
                mConnection, Context.BIND_AUTO_CREATE);

        Calendar cal = Calendar.getInstance();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String strHours = prefs.getString("alarmResquestInterval", "4");

        int hours = 4;
        try {
            hours = Integer.parseInt(strHours);
        } catch(NumberFormatException ex) {
            Log.d("RefreshReminders", "Error converting: " + strHours + ". We will use the default value...");
        }

        if(hours <= 0) {
            hours = 1;
        }

        long timeInMillis = hours * 60 * 60 * 1000;

        Log.d(TAG, "download alarms will be refreshed in " + timeInMillis + " millis");

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DownloadAlarmsReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + timeInMillis, pendingIntent);
    }

}

