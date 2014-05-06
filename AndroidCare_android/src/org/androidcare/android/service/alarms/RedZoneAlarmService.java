package org.androidcare.android.service.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.alarms.GeoPoint;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.alarms.receivers.RedZoneAlarmReceiver;
import org.androidcare.android.service.location.LocationRetreiver;

import java.util.Calendar;
import java.util.List;

public class RedZoneAlarmService extends AlarmService {

    private static final String TAG = RedZoneAlarmService.class.getName();
    static final int UPDATE_INTERVAL = 10 * 1000;
    private DatabaseHelper databaseHelper = null;

    private RedZoneAlarmReceiver redZoneAlarmReceiver =
            new RedZoneAlarmReceiver();

    public RedZoneAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Red zone alarm service started");

        final RedZoneAlarmService alarmService = this;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelook " + TAG);
        wakeLock.acquire();
        Log.i(TAG, "Lock taken");

        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));

        runWatchDog(new LocationRetreiver(alarmService, wakeLock));

        return result;
    }

    private void runWatchDog(LocationRetreiver locationRetreiver) {
        Log.i(TAG, "Red zone monitor started");

        locationRetreiver.getLocation();
        scheduleNextLaunch();
    }

    private void scheduleNextLaunch() {
        Intent intent = new Intent(RedZoneAlarmReceiver.ACTION_TRIGGER_REDZONE_SENSOR);
        intent.putExtra("alarm", getAlarm());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + UPDATE_INTERVAL);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        Log.d(TAG, "Next launch programed @ " + calendar);
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
        if (alarm != null) {
            List<GeoPoint> points = alarm.getGeoPoints(getApplicationContext());
            if (points != null && points.size() > 0) {
                Log.d(TAG, points.size() + " points found");
                for (int i = 0; i < points.size(); i++) {
                    GeoPoint firstGeoPoint = getFirstGeoPoint(i, points);
                    GeoPoint secondGeoPoint = getSecondGeoPoint(i, points);

                    if (rayCrossesSegment(location, firstGeoPoint, secondGeoPoint)) {
                        crossings++;
                    }
                }

                Log.i(TAG, "alarm " + alarm.getName() + " crosses " + crossings);


                if (pointIsInPolygon(crossings)) {
                    launchAlarm(wakeLock);
                }
            } else {
                Log.w(TAG, "Points for alarm not found");
            }
        } else {
            Log.w(TAG, "Alarm not found");
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
        return i < (points.size() - 1) ? points.get(i + 1) : points.get(0);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping service");
        super.onDestroy();
    }

}
