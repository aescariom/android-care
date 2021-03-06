package uspceu.logservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LogServiceWithoutWakelock extends Service {
	private SensorsRetriever sensorsRetriever;
	private Timer timer = new Timer();
	static final int UPDATE_INTERVAL = 10;

	@Override
	public void onCreate() {
		Toast.makeText(this, "Create service with NO wakelock", Toast.LENGTH_SHORT).show();
		sensorsRetriever = new SensorsRetriever(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		logOurStuff();
		doSomethingRepeatedly();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private void doSomethingRepeatedly() {
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				logOurStuff();
			}

		}, 0, UPDATE_INTERVAL);
	}

	private void logOurStuff() {
		StringBuilder info = new StringBuilder();

		
		info.append(" ").append(sensorsRetriever.getSensorsDataString()).append(" - TS: ").append(new Date().getTime()).append("\n");
		
		Log.d("Data", info.toString());
		LogWriter.appendLog(info.toString());
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Destroy service", Toast.LENGTH_SHORT).show();
		timer.cancel();
		sensorsRetriever.stopSensorsUpdate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}