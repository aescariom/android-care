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
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.AnySensorListener;
import org.androidcare.android.service.AnySensorRetriever;
import org.androidcare.android.service.alarms.receivers.FellOffAlarmReceiver;

public class FellOffAlarmService extends AlarmService implements AnySensorListener {

    private static final double DELTA = 8.0;

    private final String TAG = this.getClass().getName();
    private boolean isTheAlarmLaunchable = true;
    private boolean continueRunning = true;

    private int currentIndex = 0;

    private final int SAMPLING_NUMBER = 30;

    private float[] xIndexes = new float[SAMPLING_NUMBER];
    private float[] yIndexes = new float[SAMPLING_NUMBER];
    private float[] zIndexes = new float[SAMPLING_NUMBER];

    public FellOffAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Starting service");
        Log.d(TAG, "Value of intent " + intent);
        isTheAlarmLaunchable = true;

        Bundle bundle = intent.getExtras();

        Alarm alarmReceived = (Alarm) bundle.getSerializable("alarm");
        super.setAlarm(alarmReceived);

        Log.d(TAG, "Alarm data watchdog @ WakeUpAlarmService " + alarmReceived.getName());

        PowerManager.WakeLock wakeLock = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean useLock = prefs.getBoolean("forceLockWithFellOffAlarm", true);

        if(useLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelook " + TAG);
            wakeLock.acquire();
            Log.d(TAG, "lock set");
        }

        new AnySensorRetriever(this, this, wakeLock, Sensor.TYPE_LINEAR_ACCELERATION);

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
            synchronized(this) {
                if (mustLaunchAlarm(values)) {
                    launchAlarm();
                }
            }
        } else {
            finishRunning(lock, retriever);
        }
    }

    private boolean mustLaunchAlarm(float[] values) {
        int xAboveMin = 0;
        int yAboveMin = 0;
        int zAboveMin = 0;

        xIndexes[currentIndex] = values[0];
        yIndexes[currentIndex] = values[1];
        zIndexes[currentIndex] = values[2];

        for (int position = 0 ; position < SAMPLING_NUMBER - 1; position++) {
            float currentX = xIndexes[position];
            float currentY = yIndexes[position];
            float currentZ = zIndexes[position];

            float nextX = xIndexes[position + 1];
            float nextY = yIndexes[position + 1];
            float nextZ = zIndexes[position + 1];

            if (Math.abs(nextX - currentX) > DELTA) {
                xAboveMin++;
            }

            if (Math.abs(nextY - currentY) > DELTA) {
                yAboveMin++;
            }

            if (Math.abs(nextZ - currentZ) > DELTA) {
                zAboveMin++;
            }
            if(xAboveMin > 0 || yAboveMin > 0 || zAboveMin > 0) {
                Log.d(TAG, currentIndex + " - " + xAboveMin + ", " + yAboveMin + ", " + zAboveMin);
            }
        }

        currentIndex = (currentIndex + 1) % SAMPLING_NUMBER;

        return xAboveMin + yAboveMin + zAboveMin > 9;
    }


    private void launchAlarm() {
        Log.d(TAG, "INITIATE ALARM LAUNCH");
        if (isTheAlarmLaunchable) {
            isTheAlarmLaunchable = false;
            abstractInitiateAlarm();
        }
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
