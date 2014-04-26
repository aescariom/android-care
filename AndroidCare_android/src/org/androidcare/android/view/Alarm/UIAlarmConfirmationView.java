package org.androidcare.android.view.Alarm;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import org.androidcare.android.R;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmConfirmationView extends UIAlarmView {
//Comentario ¡ojo! Estás redefiniendo la variable aquí, el padre ya tiene una igual
    private AlarmService alarm;
    private boolean trigger;

    public UIAlarmConfirmationView(final AlarmWindowReceiver alarmWindowReceiver, final AlarmService alarm) {
        super(alarmWindowReceiver, alarm);
        //comentario si realmente hace falta la alarma, ya las almacenado en la llamada super
        this.alarm = alarm;

        this.trigger = true;

        inflate(alarmWindowReceiver, R.layout.alarm_ui_confirmation, this);
        alarmWindowReceiver.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button okButton = generateButtons(alarmWindowReceiver, alarm);
        startCountdown(alarmWindowReceiver, alarm, okButton);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        startPlayingSoundAndVibration();
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

    protected Window getWindow() {
        Activity parent = (Activity) getContext();
        return parent.getWindow();
    }

    public void closeWindow() {
        stopPlayingSoundAndVibration();
        Activity parent = (Activity) getContext();
        parent.finish();
    }

}