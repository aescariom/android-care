package org.androidcare.android.view.Alarm;

import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.androidcare.android.R;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmConfirmationView extends UIAlarmView {

    private AlarmService alarm;

    public UIAlarmConfirmationView(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm) {
        super(alarmWindowReceiver, alarm);
        this.alarm = alarm;

        inflate(alarmWindowReceiver, R.layout.alarm_ui_confirmation, this);
        alarmWindowReceiver.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView text = (TextView) findViewById(R.id.txtAlarmTitle);
        text.setText(alarm.getAlarm().toString());

        final Button okButton = (Button) findViewById(R.id.btnAlarmOk);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.fireAlarm(alarmWindowReceiver.getApplicationContext());
            }
        });

        Button cancelButton = (Button) findViewById(R.id.btnAlarmCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.cancelAlarm();
            }
        });

        final String launchAlarmText = alarmWindowReceiver.getApplicationContext().getString(R.string.LaunchAlarm);

        new CountDownTimer(30 * 1000, 1 * 1000) {

            public void onTick(long millisUntilFinished) {
                okButton.setText(new StringBuilder().append(launchAlarmText).append(millisUntilFinished / 1000).append(")").toString());
            }

            public void onFinish() {
                alarm.fireAlarm(alarmWindowReceiver.getApplicationContext());
            }

        }.start();
    }


}