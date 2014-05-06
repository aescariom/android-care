package org.androidcare.android.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;
import org.androidcare.android.service.alarms.GravitySensorListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GravitySensorRetriever implements SensorEventListener{
    private final String TAG = this.getClass().getName();

    private GravitySensorListener subscribedSensor;

    private SensorManager sensorManager;
    private List<Sensor> deviceSensors;
    private PowerManager.WakeLock lock;

    public GravitySensorRetriever(GravitySensorListener sensorListener, PowerManager.WakeLock lock) {
        this.subscribedSensor = sensorListener;
        sensorManager = (SensorManager) sensorListener.getContext().getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);

        this.lock = lock;

        registerSensorsListener();
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
        subscribedSensor = null;
    }

    public void registerSensorsListener() {
        for (Sensor sensor : deviceSensors) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "Sensor changed");
        if (subscribedSensor != null) {
            subscribedSensor.onChangeSensor(event.values, lock, this);
        }
    }
}