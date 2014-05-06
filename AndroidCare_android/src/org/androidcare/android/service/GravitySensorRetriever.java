package org.androidcare.android.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GravitySensorRetriever implements SensorEventListener, Serializable {
    private final String TAG = this.getClass().getName();
    private SensorManager sensorManager;
    private List<Sensor> deviceSensors;
    private Map<String, SensorValues> deviceSensorValues;

    public GravitySensorRetriever(Context context) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
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

    public List<Float> getGravitySensorsData() {
        List<Float> values = new ArrayList();
        for (String sensorName : deviceSensorValues.keySet()) {
            float[] sensorValues = deviceSensorValues.get(sensorName).getValues();
            if (sensorValues != null) {
                values.add(sensorValues[0]);
                values.add(sensorValues[1]);
                values.add(sensorValues[2]);
            } else {
                Log.d(TAG, "sensorValues is null");
            }
        }
        return values;
    }

    public List getSensorsDataString() {
        String sensorsString = "";

        List values = new ArrayList();
        for (String sensorName : deviceSensorValues.keySet()) {
            float[] sensorValues = deviceSensorValues.get(sensorName).getValues();
            if (sensorValues != null) {
                sensorsString += sensorName + ": " + sensorValues[0] + ", "
                        + sensorValues[1] + ", " + sensorValues[2];

                Log.d(TAG, "sensorValues is " + sensorsString);
                values.add(sensorValues);
                //values.add(sensorValues[1]);
               // values.add(sensorValues[2]);
            }
        }
      //values.add(sensorsString);
        return values;
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

    private class SensorValues {
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
            this.values = values.clone();
        }
    }
}