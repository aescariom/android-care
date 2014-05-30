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
import org.androidcare.android.alarms.GeoPoint;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.alarms.receivers.*;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DownloadAlarmsService extends Service {

    private final String TAG = this.getClass().getName();
    private DatabaseHelper databaseHelper;

    private DownloadAlarmsReceiver downloadAlarmsReceiver = new DownloadAlarmsReceiver();
    private IntentFilter downloadAlarmFilter = new IntentFilter(DownloadAlarmsReceiver.ACTION_UPDATE);

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

        registerReceiver(downloadAlarmsReceiver, downloadAlarmFilter);
        Log.d(TAG, "Registred download alarm receiver");
        registerReceiver(wakeUpAlarmReceiver, wakeUpAlarmBroadcastFilter);
        Log.d(TAG, "Registred Wake up alarm receiver");
        registerReceiver(greenZoneAlarmReceiver, redZoneAlarmBroadcastFilter);
        Log.d(TAG, "Registred Green zone alarm receiver");
        registerReceiver(fellOffAlarmReceiver, fellOffAlarmBroadcastFilter);
        Log.d(TAG, "Registred Fell off alarm receiver");

        if ("schedule".equals(action)) {
            this.scheduleAlarms();
        } else {
            this.removeAllAlarms();
            this.downloadAlarms();
        }

        return result;
    }

    private void removeAllAlarms() {
        this.removeAllGeoPoints();
        try {
            List<Alarm> alarms = getHelper().getAlarmDao().queryForAll();
            for (Alarm alarm : alarms) {
                getHelper().getAlarmDao().delete(alarm);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            closeDatabaseConnection();
        }
    }

    private void removeAllGeoPoints() {
        try {
            List<GeoPoint> geoPoints = getHelper().getGeoPointDao().queryForAll();
            getHelper().getGeoPointDao().delete(geoPoints);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            closeDatabaseConnection();
        }
    }

    private void downloadAlarms() {
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(DownloadAlarmsReceiver.ACTION_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    private void scheduleAlarms() {
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

        intent.putExtra("alarm", alarm);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = getNextTime(alarm);
        Log.d(TAG, "Alarm data @ DownloadAlarmsService " + alarm.getName() + " (" + alarm.getAlarmStartTime().getHours()
                + ":" + alarm.getAlarmStartTime().getMinutes() + " - "
                + alarm.getAlarmEndTime().getHours() + ":" + alarm.getAlarmEndTime().getMinutes() + ")");

        if (calendar != null) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

            Log.d(TAG, "Alarm in the future " + alarm + " scheduled @ " + calendar.getTime());
            Log.d(TAG, "Alarm will be triggered " + alarm + " scheduled @ " + alarm.getAlarmEndTime());
        } else {
            Log.d(TAG, "Alarm in the past, we will schedule it tomorrow, when it is in the future");
        }
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
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(fellOffAlarmReceiver);
        Log.d(TAG, "Unregistred fellOffAlarmReceiver");
        unregisterReceiver(greenZoneAlarmReceiver);
        Log.i(TAG, "Unregistred greenZoneAlarmReceiver");
        unregisterReceiver(wakeUpAlarmReceiver);
        Log.i(TAG, "Unregistred wakeUpAlarmReceiver");
        unregisterReceiver(downloadAlarmsReceiver);
        Log.i(TAG, "Unregistred downloadAlarmsReceiver");
        closeDatabaseConnection();

        super.onDestroy();
    }
}
