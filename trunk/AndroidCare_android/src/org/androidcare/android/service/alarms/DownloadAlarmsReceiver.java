package org.androidcare.android.service.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.reminders.RefreshRemindersReceiver;

import java.util.Calendar;

public class DownloadAlarmsReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE = "org.androidcare.android.service.UPDATE_ALARMS";
    private DownloadAlarmService downloadAlarmService;
    private ConnectionService connectionService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionService.ConnectionServiceBinder binder = (ConnectionService.ConnectionServiceBinder) service;
            connectionService = binder.getService();

            connectionService.pushMessage(new GetAlarmsMessage());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

    };


    public DownloadAlarmsReceiver(){
        super();
    }

    public DownloadAlarmsReceiver(DownloadAlarmService downloadAlarmService) {
        super();
        this.downloadAlarmService = downloadAlarmService;
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.e("TEST", "HE LLEGADO!!");

        context.getApplicationContext().bindService(
                new Intent(context.getApplicationContext(), ConnectionService.class),
                mConnection, Context.BIND_AUTO_CREATE);

        Calendar cal = Calendar.getInstance();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String strHours = prefs.getString("alarmResquestInterval", "4");

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
    }

}

