package org.androidcare.android.service.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.GravitySensorRetriever;
import org.androidcare.android.service.alarms.receivers.WakeUpAlarmReceiver;

import java.util.Calendar;
import java.util.Date;

public class WakeUpAlarmService extends AlarmService implements GravitySensorListener {

    private static final float DELTA = 2.5f;
    private WakeUpAlarmService thisService = this;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;
    private String TAG = this.getClass().getName();
    private boolean isTheAlarmAlreadyLaunched = false;

    public WakeUpAlarmService() {
        super();
    }

    @Override
    public void onCreate() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Starting service");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelook " + TAG);
        wakeLock.acquire();

        new GravitySensorRetriever(this, wakeLock);

        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onChangeSensor(float[] values, PowerManager.WakeLock lock) {
        Log.e(TAG, "sensor values: " + values[0] + "; " + values[1] + "; " + values[2]);
        detectMovement(values, lock);
        if (mustLaunchAlarmByTime()) {
            launchAlarm(lock);
        }
    }

    private void detectMovement(float[] values, PowerManager.WakeLock lock) {
        if (lastX != 0.0f && lastY != 0.0f && lastZ != 0.0f ) {
            if (isOutOfinterval(values)) {
                finishRunning(lock);
            }
        }

        if (values != null) {
            lastX = values[0];
            lastY = values[1];
            lastZ = values[2];
        } else {
            thisService.stopSelf();
            Log.w(TAG, "No data available");
        }
    }

    private boolean isOutOfinterval(float[] sensorsData) {
        Log.e(TAG, "Unos sensores: " + sensorsData[0] + "; " + sensorsData[1] + "; " + sensorsData[2]);
        Log.e(TAG, "Avg sensores: " + lastX + "; " + lastY + "; " + lastZ);

        return lastX + DELTA < sensorsData[0] || sensorsData[0] < lastX - DELTA ||
                lastY + DELTA < sensorsData[1] || sensorsData[1] < lastX - DELTA ||
                lastZ + DELTA < sensorsData[2] || sensorsData[2] < lastX - DELTA;
    }

    private void launchAlarm(PowerManager.WakeLock lock) {
        Log.d(TAG, "INITIATE ALARM LAUNCH");
        if (! isTheAlarmAlreadyLaunched) {
            abstractInitiateAlarm();
            isTheAlarmAlreadyLaunched = true;
        }

        finishRunning(lock);
    }

    private void finishRunning(PowerManager.WakeLock lock) {
        Log.d(TAG, "Finishes execution");

        ComponentName component = new ComponentName(getApplicationContext(), WakeUpAlarmReceiver.class);
        getApplicationContext().getPackageManager().
                setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        thisService.stopSelf();

        if(lock.isHeld()){
            lock.release();
        }
    }

    private boolean mustLaunchAlarmByTime() {
        Alarm alarm = super.getAlarm();
        Date endTime = alarm.getAlarmEndTime();
        Date now = new Date();
        return isNowAfterEndTime(now, endTime);
    }

    private boolean isNowAfterEndTime(Date now, Date endTime) {
        return now.compareTo(endTime) > 0;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

}
