package org.androidcare.android.service.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;

public class RedZoneAlarmReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Recibimos un red zone");

        Bundle bundle = intent.getExtras();
        Alarm alarm = (Alarm) bundle.getSerializable("alarm");

        Intent serviceIntent = new Intent(context, RedZoneAlarmService.class);
        serviceIntent.putExtra("alarm", alarm);

        context.startService(serviceIntent);
    }

}
