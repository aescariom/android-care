package org.androidcare.android.view.Alarm;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.androidcare.android.R;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmConfirmationView extends UIAlarmView {

    private AlarmService alarm;
    private boolean trigger;

    public UIAlarmConfirmationView(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm) {
        super(alarmWindowReceiver, alarm);
        this.alarm = alarm;

        this.trigger = true;

        inflate(alarmWindowReceiver, R.layout.alarm_ui_confirmation, this);
        alarmWindowReceiver.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button okButton = generateButtons(alarmWindowReceiver, alarm);
        startCountdown(alarmWindowReceiver, alarm, okButton);
    }

    private void startCountdown(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm, final Button okButton) {
        final String launchAlarmText = alarmWindowReceiver.getApplicationContext().getString(R.string.LaunchAlarm);

        new CountDownTimer(30 * 1000, 1 * 1000) {

            public void onTick(long millisUntilFinished) {
                okButton.setText(new StringBuilder().append(launchAlarmText).append(millisUntilFinished / 1000).append(")").toString());
            }

            public void onFinish() {
                if (trigger) {
                    alarm.fireAlarm(alarmWindowReceiver.getApplicationContext());
                    closeWindow();
                }
            }

        }.start();
    }

    private Button generateButtons(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm) {
        TextView text = (TextView) findViewById(R.id.txtAlarmTitle);
        text.setText(alarm.getAlarm().toString());

        final Button okButton = (Button) findViewById(R.id.btnAlarmOk);
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
                alarm.cancelAlarm();
                closeWindow();
            }
        });
        return okButton;
    }

    public void closeWindow() {
        Activity parent = (Activity) getContext();
        parent.finish();
    }

}