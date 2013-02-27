package org.androidcare.android.service.reminders;

import java.util.Calendar;

import org.androidcare.android.service.ConnectionService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefreshRemindersReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectionService.getInstance().pushMessage(new GetRemindersMessage());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 24);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RefreshRemindersReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

}
