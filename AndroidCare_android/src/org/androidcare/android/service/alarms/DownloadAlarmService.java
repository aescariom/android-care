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
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.ConnectionService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DownloadAlarmService extends Service {

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

    public void downloadAlarms() {
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DownloadAlarmsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        Log.i(TAG, "Reminder scheduled @ " + cal.getTime().toString());
    }

    private void scheduleAlarms() {
        List<Alarm> alarms = new ArrayList();
        try {
            alarms = getHelper().getAlarmDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Alarm alarm : alarms) {
            schedule(alarm);
        }
    }

    private void schedule(Alarm alarm) {
        Context context = getApplicationContext();

        // AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, WakeUpAlarmReceiver.class);
        intent.putExtra("alarm", alarm);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getAlarmStartTime().getHours());
        calendar.set(Calendar.MINUTE, alarm.getAlarmStartTime().getMinutes());

        Log.e("TEST Este es el tiempo:", calendar.toString());

        try {
            alarmIntent.send();
            Log.e("TEST", "ESTA ES LA ALARMA QUE ENVIAMOS: " + alarm);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
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
}
