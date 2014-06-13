package org.androidcare.android.service.alarms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.AnySensorRetriever;
import org.androidcare.android.service.alarms.receivers.FellOffAlarmReceiver;

public class FellOffAlarmService extends AlarmService implements AnySensorListener {

    private final String TAG = this.getClass().getName();

    private boolean isTheAlarmLaunchable = true;
    private static final double DELTA = 8.5f;
    private FellOffAlarmService thisService = this;
    private double lastMed = 0;
    private boolean continueRunning = true;
    private Chronometer chrono;

    public FellOffAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Starting service");

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

        new AnySensorRetriever(this, this, wakeLock, Sensor.TYPE_ACCELEROMETER);

        int height = Integer.parseInt(prefs.getString("alarmPhoneHeigth", "80"));

        /*
         s(t) final height (ground = 0)
         s_0 initial height (pocket heigth)
         v_0 initial velocity (in y axis, no flying 0 m/s)
         t   flying time (in seconds)
         g   Sir Isaac Newton constant... 9.8 (@ Earth surface, change for Moon/Mars/Red Bull HQ/...)

         s(t) = s_0 + v_0 * t - 1/2 g * t^2
         0    = s_0 + 0 * t   - 1/2 g * t^2
         1/2 g * t^2 = s_0
         g * t^2 = 2 * s_0
         t^2 = 2 * s_0 / g
         t = sqrt(2 * s_0 / g)
         we don't like decimals, we have g = 9.8 and s_0 = [cm] (no International System)
         t = sqrt(2 * s_0 / (g * 100))
         t = sqrt(2 * s_0 / 980)
          */
        long millisFallingTime = new Double(Math.sqrt(2.0 * ((double) height) / 980.0) * 1000).longValue();

        Log.d(TAG, "falling time " + millisFallingTime + "ms");
        Log.d(TAG, "height " + height + "cm");

        this.chrono = new Chronometer(millisFallingTime);

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
        Log.d(TAG, "sensor values: " + values[0] + "; " + values[1] + "; " + values[2]);
        if (continueRunning) {
            if (mustLaunchAlarm(values)) {
                launchAlarm(lock, retriever);
            }
        } else {
            finishRunning(lock, retriever);
        }
    }

    private boolean mustLaunchAlarm(float[] values) {
        double vectorMod = calculateVectorMod(values);
        boolean mustLaunch = false;

        if (lastMed != 0.0f) {
            mustLaunch = isOutOfInterval(vectorMod);
            if (values != null) {
                lastMed = vectorMod;
            }
        }

        if (mustLaunch) {
            this.chrono.count();
            mustLaunch = this.chrono.hasABigInterval();
        } else {
            this.chrono.reset();
        }

        if (values != null) {
            lastMed = vectorMod;
        } else {
            thisService.stopSelf();
            Log.w(TAG, "No data available");
        }

        return mustLaunch;
    }

    private boolean isOutOfInterval(double vectorMod) {
        return lastMed + DELTA < vectorMod || vectorMod < lastMed - DELTA;
    }

    private double calculateVectorMod(float[] sensorsData) {
        double x = sensorsData[0];
        double y = sensorsData[1];
        double z = sensorsData[2];

        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
    }

    private void launchAlarm(PowerManager.WakeLock lock, AnySensorRetriever retriever) {
        Log.d(TAG, "INITIATE ALARM LAUNCH");
        if (isTheAlarmLaunchable) {
            abstractInitiateAlarm();
            isTheAlarmLaunchable = false;
        }
        finishRunning(lock, retriever);
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
