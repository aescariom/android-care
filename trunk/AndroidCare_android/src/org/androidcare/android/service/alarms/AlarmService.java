package org.androidcare.android.service.alarms;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import org.androidcare.android.R;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.ConnectionServiceBroadcastReceiver;
import org.androidcare.android.service.Message;
import org.androidcare.android.view.alarm.AlarmReceiver;

import java.io.Serializable;

public class AlarmService extends Service implements Serializable {

    private Alarm alarm;
    private final String TAG = this.getClass().getName();
    private String smsAlarmStartText = "";
    private String smsAlarmMiddleText = "";

    public AlarmService(Alarm alarm) {
        super();
        this.alarm = alarm;
    }

    public AlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.smsAlarmStartText = getString(R.string.SMSAlarmStartText) + " ";
        this.smsAlarmMiddleText = " " + getString(R.string.SMSAlarmMiddleText) + " ";
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void notifyByEmail(Context ctx) {
        Log.e("TEST", "Enviamos correo");
        postData(ctx, new SendEmailMessage(this.alarm));
    }

    private void postData(Context ctx, Message message) {
        Intent intent = new Intent(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);
        intent.putExtra(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE, message);
        ctx.sendBroadcast(intent);
    }

    private void notifyBySMS(Context ctx) {
        String destination = alarm.getPhoneNumber().trim();
        String message = new StringBuilder().append(this.smsAlarmStartText).append(alarm.getName())
                .append(this.smsAlarmMiddleText).append(alarm.getAlarmSeverity()).toString();

        Log.e("TEST", "Enviamos SMS a " + destination + ". Mensaje: " + message);
        /*
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(alarm.getPhoneNumber().trim(), null, message , null, null);
        */
    }

    private void notifyByCall(Context ctx) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + alarm.getPhoneNumber().trim()));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ctx.startActivity(callIntent);
    }

    public void fireAlarm(Context ctx) {
        if (alarm.isSendSMS()) {
            notifyBySMS(ctx);
        }

        if (alarm.isSendEmail()) {
            notifyByEmail(ctx);
        }

        if (alarm.isInitiateCall()) {
            notifyByCall(ctx);
        }
    }

    public void cancelAlarm() {
//Comentario no entiendo el prop�sito de este m�todo
    }

    public void confirmationUser() {
        Intent warningIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        warningIntent.putExtra("alarm_service", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, warningIntent, PendingIntent.FLAG_ONE_SHOT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void abstractInitiateAlarm() {

    }

    public Alarm getAlarm() {
        return this.alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

}
