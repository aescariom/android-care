package org.androidcare.android.view.Alarm;

import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.TextView;
import org.androidcare.android.R;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmConfirmationView extends UIAlarmView {

    private AlarmService alarm;

    public UIAlarmConfirmationView(AlarmWindowReceiver alarmWindowReceiver, AlarmService alarm) {
        super(alarmWindowReceiver, alarm);
        Log.e("TEST", "Pintamos la ventana de confirmacion");
        this.alarm = alarm;

        inflate(alarmWindowReceiver, R.layout.alarm_ui_confirmation, this);
        alarmWindowReceiver.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView text = (TextView) findViewById(R.id.txtAlarmTitle);
        text.setText(alarm.getAlarm().toString());
    }


}