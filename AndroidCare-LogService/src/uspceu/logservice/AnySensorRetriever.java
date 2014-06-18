package uspceu.logservice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;

public class AnySensorRetriever implements SensorEventListener{
    private final String TAG = this.getClass().getName();

    private AnySensorListener subscribedSensor;
    private SensorManager sensorManager;
    private List<Sensor> deviceSensors;
    private PowerManager.WakeLock lock;

    public AnySensorRetriever(AnySensorListener sensorListener, Context ctx, PowerManager.WakeLock lock, int type) {
        this.subscribedSensor = sensorListener;
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = sensorManager.getSensorList(type);

        this.lock = lock;

        registerSensorsListener();
    }

    public void unregister() {
        Log.d(TAG, "Unregistering sensors Listener");

        sensorManager.unregisterListener(this);
        subscribedSensor = null;
    }

    public void registerSensorsListener() {
        for (Sensor sensor : deviceSensors) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "Sensor registered");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (subscribedSensor != null) {
            subscribedSensor.onChangeSensorAcl(event.values, lock, this);
        }
    }
}