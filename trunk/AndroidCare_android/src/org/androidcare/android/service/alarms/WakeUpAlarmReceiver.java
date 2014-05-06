package org.androidcare.android.service.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;

public class WakeUpAlarmReceiver extends BroadcastReceiver {

    public final static String ACTION_TRIGGER_WAKEUP_SENSOR = "org.androidcare.android.service.TRIGGER_WAKEUP_SENSOR";
    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Recibimos un wakeup");

        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) bundle.getSerializable("alarm");

        Intent serviceIntent = new Intent(context, WakeUpAlarmService.class);
        serviceIntent.putExtra("alarm", alarm);

        context.startService(serviceIntent);
    }
}