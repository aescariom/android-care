package org.androidcare.android.service;

import java.util.Calendar;

import org.androidcare.android.service.reminders.GetRemindersMessage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefreshRemindersReceiver extends BroadcastReceiver {

    public final static String ACTION_GET_REMINDERS = "org.androidcare.android.service.GET_REMINDERS";

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectionService.getInstance().pushMessage(new GetRemindersMessage());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1); //+1 For Next day (24 hours or so...)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(RefreshRemindersReceiver.ACTION_GET_REMINDERS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

}
