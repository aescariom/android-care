package org.androidcare.android.view.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.service.alarms.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        AlarmService alarm = (AlarmService) bundle.getSerializable("alarm_service");
        displayAlarm(context, alarm);
    }

    private void displayAlarm(Context context, AlarmService alarm) {
        Intent intent = new Intent(context, AlarmWindowReceiver.class);
        intent.putExtra("alarm_service", alarm);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Alarm Service PendingIntent cancelled: " + e.getMessage(), e);
            e.printStackTrace();
        }

    }

}
