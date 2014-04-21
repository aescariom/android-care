package org.androidcare.android.service.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.GravitySensorRetriever;

import java.util.Date;

public class WakeUpAlarmService extends AlarmService {

    static final int UPDATE_INTERVAL = 500;
    static final float DELTA = 2.5f;
    private WakeUpAlarmService thisService = this;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    public WakeUpAlarmService() {
        super();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");
        wakeLock.acquire();

        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));

        final GravitySensorRetriever gravitySensorRetriever = new GravitySensorRetriever(this);

        runWatchDog(gravitySensorRetriever, wakeLock);

        return START_STICKY;
    }

    private void runWatchDog(final GravitySensorRetriever gravitySensorRetriever, PowerManager.WakeLock wakeLock) {
        Log.e("TEST", "EMPEZAMOS A MONITORIZAR");

        while (true)  {
            detectMovement(gravitySensorRetriever, wakeLock);

            if (mustLaunchAlarmByTime()) {
                launchAlarm(gravitySensorRetriever, wakeLock);
            }

            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void launchAlarm(GravitySensorRetriever gravitySensorRetriever, PowerManager.WakeLock wakeLock) {
        try {
            wakeLock.release();
        } catch (RuntimeException ex) {
            /*
                Pues sí, otra chapu, pero se vuelve loco con el contador de locks y lanza excepciones. Ver más detalle:
                http://stackoverflow.com/questions/12140844/java-lang-runtimeexception-wakelock-under-locked-c2dm-lib
             */
        }
        gravitySensorRetriever.stopSensorsUpdate();

        new Thread(new Runnable() {
            @Override
            public void run() {
               abstractInitiateAlarm();
            }
        }).start();

        thisService.stopSelf();
    }

    private void detectMovement(final GravitySensorRetriever gravitySensorRetriever, PowerManager.WakeLock wakeLock) {
        float[] sensorsData = gravitySensorRetriever.getSensorsData();

        Log.e("TEST", "estado if sensores: " + lastX + "; " + lastY + "; " + lastZ);
        if (lastX != 0 && lastY != 0 && lastZ != 0 ) {
            if (isOutOfinterval(sensorsData)) {
                Log.e("TEST", "Movimiento encontrado :)");
                gravitySensorRetriever.stopSensorsUpdate();
                wakeLock.release();
                thisService.stopSelf();
            }
        }

        Log.e("TEST", "Una asignacion feliz: " + sensorsData[0] + "; " + sensorsData[1] + "; " + sensorsData[2]);
        if (sensorsData != null) {
            lastX = sensorsData[0];
            lastY = sensorsData[1];
            lastZ = sensorsData[2];
        } else {
            thisService.stopSelf();
            //Lo matamos porque no disponemos de datos... :(
        }
    }

    private boolean isOutOfinterval(float[] sensorsData) {
        Log.e("TEST", "Unos sensores: " + sensorsData[0] + "; " + sensorsData[1] + "; " + sensorsData[2]);
        Log.e("TEST", "Avg sensores: " + lastX + "; " + lastY + "; " + lastZ);

        return lastX + DELTA < sensorsData[0] || lastX - DELTA > sensorsData[0] ||
                lastY + DELTA < sensorsData[1] || lastY - DELTA > sensorsData[1] ||
                lastZ + DELTA < sensorsData[2] || lastZ - DELTA > sensorsData[2];
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
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

}
