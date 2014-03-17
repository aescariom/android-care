package uspceu.logservice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorsRetriever implements SensorEventListener {
	private SensorManager sensorManager;
	private List<Sensor> deviceSensors;
	private Map<String, SensorValues> deviceSensorValues;

	public SensorsRetriever(Context context) {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		//deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        deviceSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
		deviceSensorValues = new HashMap<String, SensorsRetriever.SensorValues>();

		registerSensorsListener();
	}

	public void registerSensorsListener() {
		for (Sensor sensor : deviceSensors) {
			deviceSensorValues.put(sensor.getName(), new SensorValues(sensor));
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	public String getSensorsDataString() {
		String sensorsString = "";

		for (String sensorName : deviceSensorValues.keySet()) {
			float[] sensorValues = deviceSensorValues.get(sensorName)
					.getValues();
			if (sensorValues != null)
				sensorsString += "\n" + sensorName + ": " + sensorValues[0] + ", "
						+ sensorValues[1] + ", " + sensorValues[2];
		}

		return sensorsString;
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
			this.values = values;
		}
	}
}