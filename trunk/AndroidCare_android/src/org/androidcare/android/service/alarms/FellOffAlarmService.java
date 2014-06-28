package org.androidcare.android.service.alarms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.AnySensorListener;
import org.androidcare.android.service.AnySensorRetriever;
import org.androidcare.android.service.alarms.receivers.FellOffAlarmReceiver;
import org.androidcare.android.view.NoCalibrationFoundWindow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FellOffAlarmService extends AlarmService implements AnySensorListener {

    private final String TAG = this.getClass().getName();
    private boolean isTheAlarmLaunchable = true;
    private boolean continueRunning = true;
    private int fellOffThreshold = 9;
    private final Lock monitorLock = new ReentrantLock();

    public FellOffAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Starting service");

        DatabaseHelper helper = new DatabaseHelper(this);
        List<FellOffAlgorithm> thresholds = new ArrayList();
        try {
            thresholds = helper.getFellOffAlgorithmDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (thresholds.size() > 0) {
            try {
                fellOffThreshold = helper.getFellOffThreshold();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "fellOffThreshold value " + fellOffThreshold);
            Toast.makeText(this, "fellOffThreshold value " + fellOffThreshold, Toast.LENGTH_SHORT).show();

            isTheAlarmLaunchable = true;

            Bundle bundle = intent.getExtras();

            Alarm alarmReceived = (Alarm) bundle.getSerializable("alarm");
            super.setAlarm(alarmReceived);

            Log.d(TAG, "Alarm data watchdog @ WakeUpAlarmService " + alarmReceived.getName());

            PowerManager.WakeLock wakeLock = null;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean useLock = prefs.getBoolean("forceLockWithFellOffAlarm", true);

            if (useLock) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelook " + TAG);
                wakeLock.acquire();
                Log.d(TAG, "lock set");
            }

            new AnySensorRetriever(this, this, wakeLock, Sensor.TYPE_LINEAR_ACCELERATION);
        } else {
            Intent intentWindow = new Intent(this, NoCalibrationFoundWindow.class);
            intentWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentWindow);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        continueRunning = false;
        Log.d(TAG, "Stopping service");
    }

    @Override
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

    @Override
    public void onChangeSensor(float[] values, PowerManager.WakeLock lock, AnySensorRetriever retriever) {

        if (continueRunning) {
            monitorLock.lock();
                if (mustLaunchAlarm(values)) {
                    launchAlarm();
                }
            monitorLock.unlock();
        } else {
            finishRunning(lock, retriever);
        }
    }

    private boolean mustLaunchAlarm(float[] values) {
         return FellOffAlgorithm.run(values) >= fellOffThreshold && isTheAlarmLaunchable ;
    }


    private void launchAlarm() {
        Log.d(TAG, "INITIATE ALARM LAUNCH");
        isTheAlarmLaunchable = false;
        abstractInitiateAlarm();
    }

    private void finishRunning(PowerManager.WakeLock lock, AnySensorRetriever retriever) {
        Log.d(TAG, "Finishes execution");

        retriever.unregister();

        ComponentName component = new ComponentName(getApplicationContext(), FellOffAlarmReceiver.class);
        getApplicationContext().getPackageManager().
                setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        if(lock.isHeld()){
            lock.release();
        }
        continueRunning = false;
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
