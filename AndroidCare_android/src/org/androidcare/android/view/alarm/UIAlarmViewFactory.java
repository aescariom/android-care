package org.androidcare.android.view.alarm;

import android.util.Log;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmViewFactory {

    public static UIAlarmView createUIAlarmView(AlarmWindowReceiver alarmWindowReceiver, AlarmService alarm) {
        Log.d("UIAlarmViewFactory", "Launching alarm UI");
        return new UIAlarmConfirmationView(alarmWindowReceiver, alarm);
    }

}
