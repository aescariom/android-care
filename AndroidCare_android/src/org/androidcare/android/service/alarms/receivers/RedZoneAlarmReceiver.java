package org.androidcare.android.service.alarms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.alarms.GreenZoneAlarmService;

import java.io.Serializable;

public class RedZoneAlarmReceiver extends BroadcastReceiver implements Serializable {

    public final static String ACTION_TRIGGER_REDZONE_SENSOR = "org.androidcare.android.service.TRIGGER_REDZONE_SENSOR";
    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting red zone alarm receiver");
        Bundle bundle = intent.getExtras();
        Alarm alarm= (Alarm) bundle.getSerializable("alarm");

            Intent serviceIntent = new Intent(context, GreenZoneAlarmService.class);
            serviceIntent.putExtra("alarm", alarm);

            context.startService(serviceIntent);
    }
}
