package org.androidcare.android.service.alarms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.alarms.Alarm;

public class WakeUpAlarmService extends AlarmService {

    public WakeUpAlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TEST", "Enviamos la ventana");
        int result = super.onStartCommand(intent, flags, startId);
        Bundle bundle = intent.getExtras();
        super.setAlarm((Alarm) bundle.getSerializable("alarm"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                abstractInitiateAlarm();
            }
        }).start();

        return result;
    }

    @Override
    public void abstractInitiateAlarm() {
        confirmationUser();
    }

}
