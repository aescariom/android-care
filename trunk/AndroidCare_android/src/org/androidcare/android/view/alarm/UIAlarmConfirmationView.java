package org.androidcare.android.view.alarm;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.androidcare.android.R;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmConfirmationView extends UIAlarmView {
    private boolean trigger;

    public UIAlarmConfirmationView(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm) {
        super(alarmWindowReceiver);

        this.trigger = true;

        inflate(alarmWindowReceiver, R.layout.alarm_ui_confirmation, this);
        alarmWindowReceiver.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        generateButtons(alarmWindowReceiver, alarm);
    }

    private void generateButtons(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm) {
        TextView text = (TextView) findViewById(R.id.txtAlarmTitle);
        text.setText(alarm.getAlarm().toString());

        Button okButton = (Button) findViewById(R.id.btnAlarmOk);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.fireAlarm(alarmWindowReceiver.getApplicationContext());
                closeWindow();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.btnAlarmCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trigger = false;
                alarm.cancelAlarm(alarmWindowReceiver.getBaseContext());
                closeWindow();
            }
        });
    }

    public void closeWindow() {
        Activity parent = (Activity) getContext();
        parent.finish();
    }

}