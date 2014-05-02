package org.androidcare.android.service.alarms;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.alarms.GeoPoint;
import org.androidcare.android.service.location.LocationRetreiver;

import java.util.List;

public class RedZoneAlarmService extends AlarmService {

    private static final String TAG = RedZoneAlarmService.class.getName();
    static final int UPDATE_INTERVAL = 10 * 1000;

    public RedZoneAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        final RedZoneAlarmService alarmService = this;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelook " + TAG);
        wakeLock.acquire();
        Log.i(TAG, "Lock taken");

        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));
        new Thread() {
            public void run() {
                runWatchDog(new LocationRetreiver(alarmService, wakeLock));
            }
        }.start();

        return START_STICKY;
    }

    private void runWatchDog(LocationRetreiver locationRetreiver) {
        Log.i(TAG, "Red zone monitor started");

        while (true)  {
            Log.i(TAG, "Question");
            locationRetreiver.getLocation();
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

    public void analyzeThe(Location location, PowerManager.WakeLock wakeLock) {
        /* Ray-casting algorith
           http://en.wikipedia.org/wiki/Point_in_polygon#Ray_casting_algorithm */

        int crossings = 0;

        Alarm alarm = getAlarm();
        List<GeoPoint> points = alarm.getGeoPoints();
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                GeoPoint firstGeoPoint = getFirstGeoPoint(i, points);
                GeoPoint secondGeoPoint = getSecondGeoPoint(i, points);

                if (rayCrossesSegment(location, firstGeoPoint, secondGeoPoint)) {
                    crossings++;
                }
            }

            Log.i(TAG, "User @ " + location + " crosses " + crossings);

            if (pointIsInPolygon(crossings)) {
                launchAlarm(wakeLock);
            }
        } else {
            Log.w(TAG, "Points for alarm not found");
        }
    }

    private void launchAlarm(PowerManager.WakeLock wakeLock) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                abstractInitiateAlarm();
            }
        }).start();

        if(wakeLock.isHeld()){
            wakeLock.release();
        }
    }

    private boolean pointIsInPolygon(int crossings) {
        return crossings % 2 == 1;
    }

    private boolean rayCrossesSegment(Location location, GeoPoint firstGeoPoint, GeoPoint secondGeoPoint) {
        double locationX = location.getLongitude();
        double locationY = location.getLatitude();
        double firstGeoPointX = firstGeoPoint.getLongitude();
        double firstGeoPointY = firstGeoPoint.getLatitude();
        double secondGeoPointX = secondGeoPoint.getLongitude();
        double secondGeoPointY = secondGeoPoint.getLatitude();

        double candidateY = evaluateForY(firstGeoPointX, firstGeoPointY, secondGeoPointX, secondGeoPointY, locationX);

        if (candidateY < locationY) {
            return false;
        }

        return pointBelongToSegment(locationX, candidateY, firstGeoPointX, firstGeoPointY, secondGeoPointX, secondGeoPointY);

    }

    private boolean pointBelongToSegment(double locationX, double candidateY, double firstGeoPointX, double firstGeoPointY,
                                         double secondGeoPointX, double secondGeoPointY) {

        return (((secondGeoPointX >= locationX && locationX >= firstGeoPointX) &&
                (secondGeoPointY >= candidateY && candidateY >= firstGeoPointY)) ||
                ((firstGeoPointX >= locationX && locationX >= secondGeoPointX) &&
                (firstGeoPointY >= candidateY && candidateY >= secondGeoPointY)) ||
                ((secondGeoPointX >= locationX && locationX >= firstGeoPointX) &&
                (firstGeoPointY >= candidateY && candidateY >= secondGeoPointY)) ||
                ((firstGeoPointX >= locationX && locationX >= secondGeoPointX) &&
                (secondGeoPointY >= candidateY && candidateY >= firstGeoPointY)));
    }

    private double evaluateForY(double firstGeoPointX, double firstGeoPointY, double secondGeoPointX, double secondGeoPointY,
                                double x) {
        return ((secondGeoPointY - firstGeoPointY) / (secondGeoPointX - firstGeoPointX)) *
                (x -firstGeoPointX) + firstGeoPointY;
    }

    private GeoPoint getFirstGeoPoint(int i, List<GeoPoint> points) {
        return points.get(i);
    }

    private GeoPoint getSecondGeoPoint(int i, List<GeoPoint> points) {
        return i < points.size() ? points.get(i) : points.get(0);
    }
}
