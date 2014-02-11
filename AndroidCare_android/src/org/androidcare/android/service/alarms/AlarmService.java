package org.androidcare.android.service.alarms;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import org.androidcare.android.R;
import org.androidcare.android.alarms.Alarm;

public class AlarmService extends Service {

    private Alarm alarm;
    private final String TAG = this.getClass().getName();

    public AlarmService(Alarm alarm) {
        super();
        this.alarm = alarm;
    }

    public AlarmService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Alarms service started");
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void notifyByEmail() {

    }

    private void notifyBySMS() {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + alarm.getPhoneNumber()));
        smsIntent.putExtra("sms_body", getString(R.string.AndroidCareAlarmWithName) + alarm.getName() + getString(R.string.triggeredWithPriority) + alarm.getAlarmSeverity());
        startActivity(smsIntent);
    }

    private void notifyByCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + alarm.getPhoneNumber()));
        startActivity(callIntent);
    }

    public void fireAlarm() {
        if (alarm.isSendSMS()) {
            notifyBySMS();
        }

        if (alarm.isInitiateCall()) {
            notifyByCall();
        }

        if (alarm.isSendEmail()) {
            notifyByEmail();
        }

    }

    public void cancelAlarm() {

    }

    public void confirmationUser() {

    }

    public void abstractInitiateAlarm() {

    }

}
