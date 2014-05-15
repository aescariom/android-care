package org.androidcare.android.service.alarms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.alarms.WakeUpAlarmService;

import java.io.Serializable;

public class WakeUpAlarmReceiver extends BroadcastReceiver implements Serializable {

    public final static String ACTION_TRIGGER_WAKEUP_SENSOR = "org.androidcare.android.service.TRIGGER_WAKEUP_SENSOR";
    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Recibimos un wakeup");

        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) bundle.getSerializable("alarm");
        Log.d(TAG, "Alarm data @ WakeUpAlarmReceiver " + alarm.getName() + " (" + alarm.getAlarmStartTime().getHours() + ":" + alarm.getAlarmStartTime().getMinutes() +
                " - " + alarm.getAlarmEndTime().getHours() + ":" + alarm.getAlarmEndTime().getMinutes() + ")");

        Intent serviceIntent = new Intent(context, WakeUpAlarmService.class);
        serviceIntent.putExtra("alarm", alarm);

        context.startService(serviceIntent);
    }
}