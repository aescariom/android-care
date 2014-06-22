package org.androidcare.android.service.alarms;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import org.androidcare.android.R;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.preferences.PreferencesActivity;
import org.androidcare.android.service.ConnectionServiceBroadcastReceiver;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.ServiceManager;
import org.androidcare.android.service.alarms.messages.SendEmailMessage;
import org.androidcare.android.view.UserWarningReceiver;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AlarmService extends Service implements Serializable {

    private Alarm alarm;
    private final String TAG = this.getClass().getName();
    private String smsAlarmStartText = "";
    private String smsAlarmMiddleText = "";
    private boolean notificationPending = true;
    private final Lock notificationLock = new ReentrantLock();

    public AlarmService(Alarm alarm) {
        super();
        notificationPending = true;
        this.alarm = alarm;
    }

    public AlarmService() {
        super();
        notificationPending = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.smsAlarmStartText = getString(R.string.SMSAlarmStartText) + " ";
        this.smsAlarmMiddleText = " " + getString(R.string.SMSAlarmMiddleText) + " ";
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyByEmail(Context ctx) {
        Log.d(TAG, "Send mail");
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

        Log.d("TEST", "Send SMS to " + destination + ". Message: " + message);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(alarm.getPhoneNumber().trim(), null, message , null, null);
    }

    private void notifyByCall(Context ctx) {
        Log.d(TAG, "Starts a call");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + alarm.getPhoneNumber().trim()));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ctx.startActivity(callIntent);
    }

    public void fireAlarm(Context ctx) {
        notificationLock.lock();
        if (notificationPending) {
            notificationPending = false;

            Log.d(TAG, "Fire alarm");
            if (alarm.isSendSMS()) {
                notifyBySMS(ctx);
            }

            if (alarm.isSendEmail()) {
                notifyByEmail(ctx);
            }

            if (alarm.isInitiateCall()) {
                openMainWindow(ctx);
                notifyByCall(ctx);
            } else {
                closeWindow(ctx);
            }
        }
        notificationLock.unlock();
    }

    private void openMainWindow(Context ctx) {
        Intent mainWindow = new Intent(ctx, PreferencesActivity.class);
        mainWindow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(mainWindow);
    }

    public void cancelAlarm(Context ctx) {
        closeWindow(ctx);
    }

    public void confirmationUser() {
        Intent warningIntent = new Intent(getApplicationContext(), UserWarningReceiver.class);
        warningIntent.putExtra("type", "alarm");
        warningIntent.putExtra("displayable", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, warningIntent, PendingIntent.FLAG_ONE_SHOT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void closeWindow(Context ctx) {
        openMainWindow(ctx);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);

        ServiceManager.stopSecondaryServices(ctx);
        ServiceManager.startAllServices(ctx);
    }

    public void abstractInitiateAlarm() {}

    public Alarm getAlarm() {
        return this.alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

}
