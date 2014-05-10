package org.androidcare.android.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.alarms.AlarmService;
import org.androidcare.android.view.alarm.AlarmReceiver;
import org.androidcare.android.view.reminder.ReminderReceiver;

public class SpecificWarningActivityLauncher extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Request received specific warning");

        Bundle bundle = intent.getExtras();
        String type = (String) bundle.getSerializable("type");

        if ("alarm".equals(type)) {
            Log.d(TAG, "Launching alarm window");

            AlarmService alarm = (AlarmService) bundle.getSerializable("displayable");
            Intent intentGenerated = new Intent(context, AlarmReceiver.class);
            intentGenerated.putExtra("alarm", alarm);

            context.sendBroadcast(intentGenerated);

        } else if ("reminder".equals(type)) {
            Log.d(TAG, "Launching reminder window");

            Reminder reminder = (Reminder) bundle.getSerializable("displayable");
            Intent intentGenerated = new Intent(context, ReminderReceiver.class);
            intentGenerated.setData(Uri.parse("androidCare://" + reminder.getId() + ".- " + reminder.getTitle()));

            intentGenerated.putExtra("reminder", reminder);

            context.sendBroadcast(intentGenerated);
        } else {
            Log.e(TAG, "No type found");
        }


    }

}
