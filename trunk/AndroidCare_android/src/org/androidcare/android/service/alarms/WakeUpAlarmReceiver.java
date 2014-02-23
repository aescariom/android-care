package org.androidcare.android.service.alarms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;

public class WakeUpAlarmReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Recibimos un wakeup");

        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) bundle.getSerializable("alarm");

        Intent windowIntent = new Intent(context, WakeUpAlarmService.class);
        windowIntent.putExtra("alarm", alarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, windowIntent, PendingIntent.FLAG_ONE_SHOT);

        try {
            pendingIntent.send();
            Log.e("TEST", "ESTA ALARMA LA RECIBIMOS Y REENVIAMOS " + alarm);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
