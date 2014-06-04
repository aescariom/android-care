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
//Comentario  ¿no tendría sentido guardar una referencia a esto y desregistrarse cuando se pare el servicio de la alarma?
        new AnySensorRetriever(this, this, wakeLock, Sensor.TYPE_ACCELEROMETER);

        return START_STICKY;
    }

    //Comentario entiendo que aquí habría que desregistrar tanto tu listener como el listener del sistema que
    //Tu listener tiene dentro
    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping service");
    }
//Comentario no entiendo cuál es el propósito de esto
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

    @Override
    public void onChangeSensor(float[] values, PowerManager.WakeLock lock, AnySensorRetriever retriever) {
        Log.d(TAG, "sensor values: " + values[0] + "; " + values[1] + "; " + values[2]);
        if (mustLaunchAlarm(values)) {
            launchAlarm(lock, retriever);
        }
    }

    private boolean mustLaunchAlarm(float[] values) {
        double vectorMod = calculateVectorMod(values);
        boolean result = false;

        if (lastMed != 0.0f) {
            result = isOutOfInterval(vectorMod);
            if (values != null) {
                lastMed = vectorMod;
            }
        }

        if (values != null) {
            lastMed = vectorMod;
        } else {
            thisService.stopSelf();
            Log.w(TAG, "No data available");
        }

        return result;
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

        ComponentName component = new ComponentName(getApplicationContext(), FellOffAlarmReceiver.class);
        getApplicationContext().getPackageManager().
                setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }
//Comentario ¿y esto que viene?
    @Override
    public Context getContext() {
        return null;
    }
}
