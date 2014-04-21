package org.androidcare.android.service.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.GravitySensorRetriever;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class WakeUpAlarmService extends AlarmService {

    private PowerManager.WakeLock wakeLock;
    private GravitySensorRetriever gravitySensorRetriever;
    private Timer timer = new Timer();
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
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");

        gravitySensorRetriever = new GravitySensorRetriever(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        wakeLock.acquire();

        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));

        runWatchDog();

        return START_STICKY;
    }

    private void runWatchDog() {
        detectMovement();

        if (mustLaunchAlarmByTime()) {
            Log.e("TEST", "LANZAMOS ALARMA");
            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    abstractInitiateAlarm();
                }
            }).start();
            */
            thisService.stopSelf();
        }
    }

    private void detectMovement() {
        Log.e("TEST", "EMPEZAMOS A MONITORIZAR");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                float[] sensorsData = gravitySensorRetriever.getSensorsData();

                if (lastX != 0 && lastY != 0 && lastZ != 0 ) {
                    if (isOutOfinterval(sensorsData)) {
                        Log.e("TEST", "Movimiento encontrado :)");
                        thisService.stopSelf();
                    }
                }

                if (sensorsData != null) {
                    lastX = sensorsData[0];
                    lastY = sensorsData[1];
                    lastZ = sensorsData[2];
                }
            }

            private boolean isOutOfinterval(float[] sensorsData) {
                Log.e("TEST", "Unos sensores: " + sensorsData[0] + "; " + sensorsData[1] + "; " + sensorsData[2]);
                Log.e("TEST", "Avg sensores: " + lastX + "; " + lastY + "; " + lastZ);

                return lastX + DELTA < sensorsData[0] || lastX - DELTA > sensorsData[0] ||
                        lastY + DELTA < sensorsData[1] || lastY - DELTA > sensorsData[1] ||
                        lastZ + DELTA < sensorsData[2] || lastZ - DELTA > sensorsData[2];
            }

        }, 0, UPDATE_INTERVAL);
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
    public void onDestroy() {
        timer.cancel();
        gravitySensorRetriever.stopSensorsUpdate();
        wakeLock.release();
    }

    @Override
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

}
