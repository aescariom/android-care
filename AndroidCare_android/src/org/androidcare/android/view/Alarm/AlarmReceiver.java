package org.androidcare.android.view.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.service.alarms.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        AlarmService alarm = (AlarmService) bundle.getSerializable("alarm_service");
        Log.e("TEST", "ESTA ES NUESTRA ALARMA: " + alarm);
        displayAlarm(context, alarm);
    }

    private void displayAlarm(Context context, AlarmService alarm) {
        Intent intent = new Intent("ANDROID.INTENT.ACTION.MAIN");
        intent.setClass(context, AlarmWindowReceiver.class);
        intent.putExtra("alarm_service", alarm);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

}
