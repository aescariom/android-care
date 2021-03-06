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
import org.androidcare.android.service.alarms.receivers.GreenZoneAlarmReceiver;
import org.androidcare.android.service.location.LocationRetreiver;
import org.androidcare.android.service.location.LocationService;

import java.util.Calendar;
import java.util.List;

public class GreenZoneAlarmService extends AlarmService {

    private static final String TAG = GreenZoneAlarmService.class.getName();
    static final int UPDATE_INTERVAL = 1 * 60 * 1000;
    private boolean heWasInside = true;

    public GreenZoneAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "Green zone alarm service started");

        final GreenZoneAlarmService alarmService = this;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelook " + TAG);
        wakeLock.acquire();
        Log.i(TAG, "Lock taken");
        Log.d(TAG, "Value of intent " + intent);

        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) bundle.getSerializable("alarm");
        super.setAlarm(alarm);
        this.heWasInside = (Boolean) bundle.getSerializable("heWasInside");

        if (alarm.runnable) {
            alarm.runnable = false;
            Log.d(TAG, "Launching location retriever");
            LocationRetreiver locationRetreiver = new LocationRetreiver(alarmService, wakeLock);
            runWatchDog(locationRetreiver);
        }

        return result;
    }

    private void runWatchDog(LocationRetreiver locationRetreiver) {
        Log.i(TAG, "Green zone monitor started");

        locationRetreiver.getLocation();
        scheduleNextLaunch();
    }

    private void scheduleNextLaunch() {
        Intent intent = new Intent(GreenZoneAlarmReceiver.ACTION_TRIGGER_GREENZONE_SENSOR);
        intent.putExtra("alarm", getAlarm());
        intent.putExtra("heWasInside", heWasInside);
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

                if (!pointIsInPolygon(crossings)) {
                    //Outside zone
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    intent.putExtra("timeUpdate", 1);
                    startService(intent);
                    if (heWasInside) {
                        //going out
                        launchAlarm(wakeLock);
                    } else {
                        //still out
                    }
                    heWasInside = false;
                } else {
                    //Inside zone
                    if (heWasInside) {
                        //still in
                    } else {
                        //going in
                        Intent intent = new Intent(getApplicationContext(), LocationService.class);
                        startService(intent);
                    }
                    heWasInside = true;
                }
            } else {
                Log.w(TAG, "Points for alarm not found");
            }
        } else {
            Log.w(TAG, "Alarm not found");
        }
    }

    private void launchAlarm(PowerManager.WakeLock wakeLock) {
        abstractInitiateAlarm();
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
