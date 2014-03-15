package uspceu.logservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LogService extends Service {
	private PowerManager.WakeLock wakeLock;
	private String activity;
	private final String ACTIVITY = "Activity";
	private LocationRetriever locationRetriever;
	private SensorsRetriever sensorsRetriever;
	private Timer timer = new Timer();
	static final int UPDATE_INTERVAL = 1000;

	@Override
	public void onCreate() {
		Toast.makeText(this, "Create service", Toast.LENGTH_SHORT).show();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");

		locationRetriever = new LocationRetriever(this);
		sensorsRetriever = new SensorsRetriever(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			activity = extras.getString(ACTIVITY);
		}
		wakeLock.acquire();

		logLocation();
		doSomethingRepeatedly();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private void doSomethingRepeatedly() {
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				logLocation();
			}

		}, 0, UPDATE_INTERVAL);
	}

	private void logLocation() {
		Location location = locationRetriever.getLocation();
		String info;
		
		if (location != null) {
			info = location.toString();
		} else {
			info = "Location: null";
		}
		
		info += " "
				+ sensorsRetriever.getSensorsDataString() + "Activity: "
				+ activity;
		
		Log.d("Data", info);
		LogWriter.appendLog(info);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Destroy service", Toast.LENGTH_SHORT).show();
		timer.cancel();
		locationRetriever.stopLocationUpdate();
		sensorsRetriever.stopSensorsUpdate();
		wakeLock.release();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}