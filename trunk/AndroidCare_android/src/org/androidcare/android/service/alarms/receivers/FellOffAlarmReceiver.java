package org.androidcare.android.service.alarms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.alarms.FellOffAlarmService;

public class FellOffAlarmReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Recibimos un Fell off");

        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) bundle.getSerializable("alarm");

        Intent serviceIntent = new Intent(context, FellOffAlarmService.class);
        serviceIntent.putExtra("alarm", alarm);

        context.startService(serviceIntent);
    }
}
