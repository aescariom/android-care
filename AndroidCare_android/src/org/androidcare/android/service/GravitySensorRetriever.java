package org.androidcare.android.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.Serializable;

public class GravitySensorRetriever implements SensorEventListener, Serializable {
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorValues deviceSensorValues;

    public GravitySensorRetriever(Context context) {

        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (sensor != null) {
            registerSensorsListener();
        } else {
            throw new RuntimeException("Gravity Sensor not found");
        }
    }

    public void registerSensorsListener() {
            deviceSensorValues = new SensorValues(sensor);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public float[] getSensorsData() {
        return deviceSensorValues.getValues();
    }

    public void stopSensorsUpdate() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        deviceSensorValues.setValues(event.values);
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