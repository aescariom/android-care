package org.androidcare.android.service.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import org.androidcare.android.service.ConnectionService;

import java.util.Calendar;

public class DownloadAlarmService extends Service {

    private final String TAG = this.getClass().getName();

    private ConnectionService connectionService;
    boolean mBound = false;

    private DownloadAlarmsReceiver downloadAlarmsReceiver =
            new DownloadAlarmsReceiver(this);
    private IntentFilter downloadAlarmFilter =
            new IntentFilter(DownloadAlarmsReceiver.ACTION_UPDATE);

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
    }

    private void scheduleAlarms() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
