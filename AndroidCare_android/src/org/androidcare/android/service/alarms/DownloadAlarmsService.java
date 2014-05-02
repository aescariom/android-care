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
import org.androidcare.android.service.ConnectionService;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DownloadAlarmsService extends Service {

    public static final long A_DAY = 24 * 60 * 60 * 1000;
    private final String TAG = this.getClass().getName();

    private ConnectionService connectionService;
    boolean mBound = false;

    private DownloadAlarmsReceiver downloadAlarmsReceiver =
            new DownloadAlarmsReceiver(this);
    private IntentFilter downloadAlarmFilter =
            new IntentFilter(DownloadAlarmsReceiver.ACTION_UPDATE);
    private DatabaseHelper databaseHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "Alarms downloads service started");

        registerReceiver(downloadAlarmsReceiver, downloadAlarmFilter);

        this.downloadAlarms();
        this.scheduleAlarms();

        return result;
    }

    private void downloadAlarms() {
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DownloadAlarmsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        Log.i(TAG, "Download scheduled @ " + cal.getTime().toString());
    }

    private void scheduleAlarms() {
        List<Alarm> alarms = null;
        try {
            alarms = getHelper().getAlarmDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Alarm alarm : alarms) {
            ifIsRedZoneAddsGeoPointsTo(alarm);
            scheduleFirstLaunch(alarm);
        }
    }

    private void ifIsRedZoneAddsGeoPointsTo(Alarm alarm) {
        if (alarm.getAlarmType() == AlarmType.RED_ZONE) {
            try {
                List<GeoPoint> geoPoints = getHelper().getGeoPointsFor(alarm.getId());
                alarm.setGeoPoints(geoPoints);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void scheduleFirstLaunch(Alarm alarm) {
        Context context = getApplicationContext();

        Intent intent = null;

        if (alarm.getAlarmType() == AlarmType.WAKE_UP) {
            intent = new Intent(context, WakeUpAlarmReceiver.class);
        } else if (alarm.getAlarmType() == AlarmType.RED_ZONE) {
            intent = new Intent(context, RedZoneAlarmReceiver.class);
            Log.i(TAG, "Alarm sent " + alarm.getGeoPoints().size());
        } else if (alarm.getAlarmType() == AlarmType.FELL_OFF) {
            intent = new Intent(context, FellOffAlarmReceiver.class);
        }

        intent.putExtra("alarm", alarm);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = getNextTime(alarm);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

    private Calendar getNextTime(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (alarm.getAlarmType() == AlarmType.WAKE_UP) {
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getAlarmStartTime().getHours());
            calendar.set(Calendar.MINUTE, alarm.getAlarmStartTime().getMinutes());
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTime().compareTo(new Date()) < 0) {
                long calendarTimeInMillis = calendar.getTimeInMillis();
                calendar.setTimeInMillis(calendarTimeInMillis + A_DAY);
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
        unregisterReceiver(downloadAlarmsReceiver);
        super.onDestroy();
    }
}
