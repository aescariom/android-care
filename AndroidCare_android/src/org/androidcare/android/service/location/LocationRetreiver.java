package org.androidcare.android.service.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import org.androidcare.android.service.ConnectionServiceBroadcastReceiver;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.PushMessagesReceiver;
import org.androidcare.android.service.alarms.RedZoneAlarmService;

import java.util.List;

public class LocationRetreiver implements LocationListener {
    private static final String TAG = LocationRetreiver.class.getName();

    private PowerManager.WakeLock wakeLock;
    private LocationRetreiver thisLocationRetriever = this;

    private LocationManager locationManager;

    private Looper looper;

    private Service parentService;
    private int errors;
    private boolean lastRequestForUpdateFullfilled;

    public LocationRetreiver(Service parent) {
        this.parentService = parent;
    }

    public LocationRetreiver(Service parent, PowerManager.WakeLock wakeLock) {
        this.parentService = parent;
        this.wakeLock = wakeLock;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (parentService.getClass() == LocationService.class) {
            postData(new LocationMessage(location));
        } else if (parentService.getClass() == RedZoneAlarmService.class) {
            RedZoneAlarmService redZoneAlarm = (RedZoneAlarmService) parentService;
            redZoneAlarm.analyzeThe(location, wakeLock);
        } else {
            throw new RuntimeException("No idea what should I do with this...");
        }
        Log.i(this.getClass().getName(),
                "Location obtained and scheduled to be sent: " + location.toString());
        lastRequestForUpdateFullfilled = true;
        errors = 0;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e(TAG, "Provider status changed: " + provider + " to " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "Provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e(TAG, "Provider disabled: " + provider);
    }

    public void getLocation() {
        Log.i(TAG, "Parent class is " + parentService.getClass());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(parentService.getApplicationContext());
        final Criteria criteria = getCriteria();

        looper = getLooper();
        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) parentService.getSystemService(Context.LOCATION_SERVICE);
        lastRequestForUpdateFullfilled=false;

        List<String> providers = locationManager.getProviders(criteria, true);
        if(providers.size() > 0){
            locationManager.requestSingleUpdate(criteria, this, looper);
            Log.i(TAG, "Asking for position using the criteria");
        }else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, looper);
            Log.w(TAG, "Any provider found, using NETWORK_PROVIDER");
        }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, looper);
            Log.w(TAG, "Any provider found, using GPS PROVIDER");
        }else{
            Log.e(TAG, "Location could be retrieved because any active radio could be found");
            UpdateLocationReceiver.releaseLock();
        }
    }

    private Looper getLooper() {
        final Looper localLooper = Looper.getMainLooper();

        Log.i(TAG, "this is the LocalLooper " + localLooper);
        Handler myHandler = new Handler(localLooper);
        myHandler.postDelayed(new Runnable() {
            public void run() {
                if(!lastRequestForUpdateFullfilled){
                    locationManager.removeUpdates(thisLocationRetriever);
                    Log.w(TAG, "Location could not be obtained; we stop trying:");
                    errors++;
                    if(errors > 10){
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, thisLocationRetriever, localLooper);
                        errors = 0;
                        Log.e(TAG, "Too many consecutive errors, trying to use the GPS");
                    }else{
                        UpdateLocationReceiver.releaseLock();
                    }
                }
            }
        }, 60000);
        return localLooper;
    }

    private Criteria getCriteria() {
        // setting the criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        return criteria;
    }

    public boolean isAirplaneMode() {
        int settingValue;
        try {
            settingValue = Settings.System.getInt(
                    parentService.getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
            return settingValue != 0;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    private void postData(Message message) {
        Intent intent = new Intent(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);
        intent.putExtra(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE, message);
        parentService.getApplicationContext().sendBroadcast(intent);
    }
}
