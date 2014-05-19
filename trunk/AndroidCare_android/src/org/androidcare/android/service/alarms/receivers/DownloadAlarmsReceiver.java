package org.androidcare.android.service.alarms.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.alarms.DownloadAlarmsService;
import org.androidcare.android.service.alarms.messages.GetAlarmsMessage;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DownloadAlarmsReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE = "org.androidcare.android.service.UPDATE_ALARMS";
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
        List<Alarm> alarmList = new LinkedList();
        try {
            alarmList =  getHelper(context).getAlarmDao().queryForAll();
            closeDatabaseConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String iterationNumber = arg1.getStringExtra("tryNumber");
        int tryNumber = (iterationNumber != null ? Integer.parseInt(iterationNumber) : 0);
        tryNumber++;

        context.getApplicationContext().bindService(
                new Intent(context.getApplicationContext(), ConnectionService.class),
                mConnection, Context.BIND_AUTO_CREATE);

        Calendar cal = Calendar.getInstance();
        long timeInMillis = 20 * 1000;

        if (alarmList.size() > 0) {
            Log.d(TAG, "Starts downloading files");

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

            timeInMillis = hours * 60 * 60 * 1000;
            Intent serviceIntent = new Intent (context, DownloadAlarmsService.class);
            serviceIntent.putExtra("action", "schedule");
            context.startService(serviceIntent);
        }

        boolean setUpTheQuery = (PendingIntent.getBroadcast(context, 0,
                new Intent(DownloadAlarmsReceiver.ACTION_UPDATE), PendingIntent.FLAG_NO_CREATE) == null);

        if (tryNumber > 3) {
            tryNumber = 1;
            timeInMillis = 60 * 60 * 1000;
        }

        if (setUpTheQuery) {
            Log.d(TAG, "download alarms will be refreshed in " + timeInMillis + " millis");

            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(DownloadAlarmsReceiver.ACTION_UPDATE);
            intent.putExtra("tryNumber", String.valueOf(tryNumber));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            am.cancel(pendingIntent);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + timeInMillis, pendingIntent);
        }
    }

    private DatabaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void closeDatabaseConnection() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

}

