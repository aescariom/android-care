package org.androidcare.android.service.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.alarms.AlarmType;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.alarms.messages.GetAlarmsMessage;
import org.androidcare.android.service.alarms.receivers.DownloadAlarmsReceiver;
import org.androidcare.android.service.alarms.receivers.FellOffAlarmReceiver;
import org.androidcare.android.service.alarms.receivers.GreenZoneAlarmReceiver;
import org.androidcare.android.service.alarms.receivers.WakeUpAlarmReceiver;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmManagerService extends Service {

    private final String TAG = this.getClass().getName();
    private DatabaseHelper databaseHelper;

    private WakeUpAlarmReceiver wakeUpAlarmReceiver = new WakeUpAlarmReceiver();
    private IntentFilter wakeUpAlarmBroadcastFilter = new IntentFilter(WakeUpAlarmReceiver.ACTION_TRIGGER_WAKEUP_SENSOR);

    private GreenZoneAlarmReceiver greenZoneAlarmReceiver = new GreenZoneAlarmReceiver();
    private IntentFilter redZoneAlarmBroadcastFilter = new IntentFilter(GreenZoneAlarmReceiver.ACTION_TRIGGER_GREENZONE_SENSOR);

    private FellOffAlarmReceiver fellOffAlarmReceiver = new FellOffAlarmReceiver();
    private IntentFilter fellOffAlarmBroadcastFilter = new IntentFilter(FellOffAlarmReceiver.ACTION_TRIGGER_FELLOFF_SENSOR);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        String action = intent.getStringExtra("action");

        Log.i(TAG, "Alarms downloads service started");
        Log.d(TAG, "Alarms download action " + action);

        registerReceiver(wakeUpAlarmReceiver, wakeUpAlarmBroadcastFilter);
        Log.d(TAG, "Registred Wake up alarm receiver");
        registerReceiver(greenZoneAlarmReceiver, redZoneAlarmBroadcastFilter);
        Log.d(TAG, "Registred Green zone alarm receiver");
        registerReceiver(fellOffAlarmReceiver, fellOffAlarmBroadcastFilter);
        Log.d(TAG, "Registred Fell off alarm receiver");

        Log.d(TAG, "Scheduling alarms");
        this.scheduleAlarmsFromDatabase();

        Log.d(TAG, "Trying to update alarms");
        this.downloadAlarms();

        GetAlarmsMessage.setAlarmManagerService(this);

        return result;
    }

    private void downloadAlarms() {
        try {
            Intent intent = new Intent(getApplicationContext(), DownloadAlarmsReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Log.d(TAG, "Downloading alarm");
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void scheduleAlarmsFromDatabase() {
        List<Alarm> alarms = null;
        try {
            alarms = getHelper().getAlarmDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Alarm alarm : alarms) {
            scheduleFirstLaunch(alarm);
        }
    }

    private void scheduleFirstLaunch(Alarm alarm) {
        Context context = getApplicationContext();

        Intent intent = getIntentByAlarmType(alarm);
        intent.putExtra("alarm", alarm);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = getNextTime(alarm);

        Log.d(TAG, "Alarm data @ AlarmManagerService " + alarm.getName() + " (" + alarm.getAlarmStartTime().getHours()
                + ":" + alarm.getAlarmStartTime().getMinutes() + " - "
                + alarm.getAlarmEndTime().getHours() + ":" + alarm.getAlarmEndTime().getMinutes() + ")");

        if (calendar != null) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

            Log.d(TAG, "Alarm in the future: " + alarm + " scheduled @ " + calendar.getTime());
            Log.d(TAG, "Alarm " + alarm + " will be triggered @ " + alarm.getAlarmEndTime());
        } else {
            Log.d(TAG, "Alarm in the past, we will schedule it tomorrow, when it is in the future");
        }
    }

    private Intent getIntentByAlarmType(Alarm alarm) {
        Intent intent = null;

        if (alarm.getAlarmType() == AlarmType.WAKE_UP) {
            intent = new Intent(WakeUpAlarmReceiver.ACTION_TRIGGER_WAKEUP_SENSOR);
            Log.d(TAG, "Launching Wake up alarm");
        } else if (alarm.getAlarmType() == AlarmType.GREEN_ZONE) {
            intent = new Intent(GreenZoneAlarmReceiver.ACTION_TRIGGER_GREENZONE_SENSOR);
            intent.putExtra("heWasInside", true);
            Log.d(TAG, "Launching Green zone alarm");
        } else if (alarm.getAlarmType() == AlarmType.FELL_OFF) {
            intent = new Intent(FellOffAlarmReceiver.ACTION_TRIGGER_FELLOFF_SENSOR);
            Log.d(TAG, "Launching Fell off alarm");
        }
        return intent;
    }

    public void unscheduleAllDatabaseAlarms() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), FellOffAlarmService.class));
        getApplicationContext().stopService(new Intent(getApplicationContext(), WakeUpAlarmService.class));
        getApplicationContext().stopService(new Intent(getApplicationContext(), GreenZoneAlarmService.class));
    }

    private Calendar getNextTime(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (alarm.getAlarmType() == AlarmType.WAKE_UP) {
            Log.d(TAG, "Alarm start time schedule " + alarm.getAlarmStartTime().getHours() + ":" + alarm.getAlarmStartTime().getMinutes());
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getAlarmStartTime().getHours());
            calendar.set(Calendar.MINUTE, alarm.getAlarmStartTime().getMinutes());
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTime().compareTo(new Date()) < 0) {
                return null;
            }
        }

        return calendar;
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void closeDatabaseConnection() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(fellOffAlarmReceiver);
        Log.d(TAG, "Unregistred fellOffAlarmReceiver");
        unregisterReceiver(greenZoneAlarmReceiver);
        Log.i(TAG, "Unregistred greenZoneAlarmReceiver");
        unregisterReceiver(wakeUpAlarmReceiver);
        Log.i(TAG, "Unregistred wakeUpAlarmReceiver");
        closeDatabaseConnection();

        unscheduleAllDatabaseAlarms();

        super.onDestroy();
    }

}
