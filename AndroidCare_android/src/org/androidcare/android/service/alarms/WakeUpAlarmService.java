package org.androidcare.android.service.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.service.GravitySensorRetriever;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WakeUpAlarmService extends AlarmService {

    private Timer timer = new Timer();
    static final int UPDATE_INTERVAL = 50;
    static final float DELTA = 2.5f;
    private WakeUpAlarmService thisService = this;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;
    private String TAG = this.getClass().getName();

    private GravitySensorRetriever sensorsRetriever;

    @Override
    public void onCreate() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        sensorsRetriever = new GravitySensorRetriever(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logOurStuff();
        doSomethingRepeatedly();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                logOurStuff();
            }

        }, 0, UPDATE_INTERVAL);
    }

    private void logOurStuff() {
        //Log.d(TAG, "RAW data: " + sensorsRetriever.getSensorsDataString());
        List gravitySensorsData = sensorsRetriever.getSensorsDataString();
        //Log.d(TAG, "primera posicion " + gravitySensorsData.get(0));
        float[] arrayDeSensores = (float[]) gravitySensorsData.get(0);
        Log.e("TEST", "Una asignacion feliz: " + arrayDeSensores[0] + "; " + arrayDeSensores[1] + "; " + arrayDeSensores[2]);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        sensorsRetriever.stopSensorsUpdate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   /*

   public WakeUpAlarmService() {
        super();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "Starting service");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");
        wakeLock.acquire();

        gravitySensorRetriever = new GravitySensorRetriever(this);

        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));

        runWatchDog(wakeLock);

        return result;
    }

    private void runWatchDog(final PowerManager.WakeLock wakeLock) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logOurStuff();
                detectMovement(wakeLock);
                if (mustLaunchAlarmByTime()) {
                    launchAlarm(wakeLock);
                }
            }
        }, 0, UPDATE_INTERVAL);
    }

    private void detectMovement(PowerManager.WakeLock wakeLock) {
        float[] sensorsData = sensorsRetriever.getGravitySensorsData();

        Log.e("TEST", "estado if sensores: " + lastX + "; " + lastY + "; " + lastZ);
        if (lastX != 0 && lastY != 0 && lastZ != 0 ) {
            if (isOutOfinterval(sensorsData)) {
                Log.e("TEST", "Movimiento encontrado :)");
                sensorsRetriever.stopSensorsUpdate();

                if(wakeLock.isHeld()){
                    wakeLock.release();
                }

                unregisterReceiver(new RedZoneAlarmReceiver());

                thisService.stopSelf();
            }
        }

        if (sensorsData != null) {
            Log.e("TEST", "Una asignacion feliz: " + sensorsData[0] + "; " + sensorsData[1] + "; " + sensorsData[2]);
            lastX = sensorsData[0];
            lastY = sensorsData[1];
            lastZ = sensorsData[2];
        } else {
            thisService.stopSelf();
            Log.w(TAG, "No data available");
        }
    }

    private boolean isOutOfinterval(float[] sensorsData) {
        Log.e("TEST", "Unos sensores: " + sensorsData[0] + "; " + sensorsData[1] + "; " + sensorsData[2]);
        Log.e("TEST", "Avg sensores: " + lastX + "; " + lastY + "; " + lastZ);

        return lastX + DELTA < sensorsData[0] || lastX - DELTA > sensorsData[0] ||
                lastY + DELTA < sensorsData[1] || lastY - DELTA > sensorsData[1] ||
                lastZ + DELTA < sensorsData[2] || lastZ - DELTA > sensorsData[2];
    }

    @Override
    public void onDestroy() {
        gravitySensorRetriever.stopSensorsUpdate();

        Log.d(TAG, "Stopping service");
        super.onDestroy();
    }

    private void runWatchDog(final PowerManager.WakeLock wakeLock) {
        Log.e(TAG, "Wake up monitor started");
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

            }

        }, 0, UPDATE_INTERVAL);

    }

    private void launchAlarm(PowerManager.WakeLock wakeLock) {
        gravitySensorRetriever.stopSensorsUpdate();

        new Thread(new Runnable() {
            @Override
            public void run() {
               abstractInitiateAlarm();
            }
        }).start();

        thisService.stopSelf();

        if(wakeLock.isHeld()){
            wakeLock.release();
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
    */
}
