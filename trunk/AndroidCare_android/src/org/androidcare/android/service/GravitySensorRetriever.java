package org.androidcare.android.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GravitySensorRetriever implements SensorEventListener, Serializable {
    private SensorManager sensorManager;
    private List<Sensor> deviceSensors;
    private Map<String, SensorValues> deviceSensorValues;

    public GravitySensorRetriever(Context context) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        //deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        deviceSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        deviceSensorValues = new HashMap<String, GravitySensorRetriever.SensorValues>();

        registerSensorsListener();
    }

    public void registerSensorsListener() {
        for (Sensor sensor : deviceSensors) {
            deviceSensorValues.put(sensor.getName(), new SensorValues(sensor));

            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public float[] getSensorsData() {
        return deviceSensorValues.get("Gravity").getValues();
    }

    public void stopSensorsUpdate() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorValues sensorValues = deviceSensorValues.get(event.sensor
                .getName());
        sensorValues.setValues(event.values);
    }

    private class SensorValues implements Serializable {
        private Sensor sensor;
        private float[] values;

        public SensorValues(Sensor sensor) {
            this.sensor = sensor;
        }

        public Sensor getSensor() {
            return sensor;
        }

        public float[] getValues() {
            return values;
        }

        public void setValues(float[] values) {
            this.values = values;
        }
    }
}