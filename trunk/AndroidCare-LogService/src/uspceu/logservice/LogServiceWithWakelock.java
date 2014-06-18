package uspceu.logservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogServiceWithWakelock extends Service implements AnySensorListener {
	private PowerManager.WakeLock wakeLock;
    private boolean destroy = false;
    private List<Point> pointsAcl = new ArrayList();

    @Override
	public void onCreate() {
        Log.d("Service", "create");
		Toast.makeText(this, "Create service with wakelock", Toast.LENGTH_SHORT).show();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand");
		wakeLock.acquire();
        new AnySensorRetriever(this, this, wakeLock, Sensor.TYPE_LINEAR_ACCELERATION);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Destroy service", Toast.LENGTH_SHORT).show();
        destroy = true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override
    public void onChangeSensorAcl(float[] values, PowerManager.WakeLock lock, AnySensorRetriever retriever) {
        if (destroy) {
            retriever.unregister();

            process();

            wakeLock.release();
            super.onDestroy();
        } else {
            long millis = (new Date()).getTime();

            pointsAcl.add(new Point(millis, values[0], values[1], values[2]));
        }
    }

    private void process() {
        long millis = 0;

        long absZero = pointsAcl.get(0).getTimestamp();

        for (int i = 0; i < pointsAcl.size() - 2; i++) {

            Point currentPointAcl = pointsAcl.get(i);
            Point nextPointAcl = pointsAcl.get(i + 1);

            long xAcl0 = currentPointAcl.getTimestamp() - absZero;
            long xAcl1 = nextPointAcl.getTimestamp() - absZero;

            while (xAcl0 <= millis && millis <= xAcl1) {
                double newPointAX = f(millis, xAcl0, xAcl1, currentPointAcl.getPointX(), nextPointAcl.getPointX());
                double newPointAY = f(millis, xAcl0, xAcl1, currentPointAcl.getPointY(), nextPointAcl.getPointY());
                double newPointAZ = f(millis, xAcl0, xAcl1, currentPointAcl.getPointZ(), nextPointAcl.getPointZ());

                StringBuilder builder = new StringBuilder();
                builder.append(millis).append(" ").append(newPointAX).append(" ").append(newPointAY).append(" ").append(newPointAZ);

                //Log.d("DATA", builder.toString());
                LogWriter.appendLog(builder.toString());

                millis += 10;
            }

        }
    }

    public double f(long longx, long longx0, long longx1, double y0, double y1) {
        double x0 = (double) longx0;
        double x1 = (double) longx1;
        double x  = (double) longx;

        return ((y1 - y0) / (x1 - x0)) * (x - x0) + y0;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}