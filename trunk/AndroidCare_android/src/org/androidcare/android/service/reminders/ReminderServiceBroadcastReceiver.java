package org.androidcare.android.service.reminders;

import java.util.Calendar;

import org.androidcare.android.reminders.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ReminderServiceBroadcastReceiver extends BroadcastReceiver {

    public final static String ACTION_SCHEDULE_REMINDER = "org.androidcare.android.service.SCHEDULE_REMINDER";

    public final static String EXTRA_REMINDER = "EXTRA_REMINDER";
    public final static String EXTRA_DELAY = "EXTRA_DELAY";

    private ReminderService reminderService;

    public ReminderServiceBroadcastReceiver(ReminderService conn) {
        super();
        this.reminderService = conn;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Reminder r = (Reminder) extras.getSerializable(ReminderServiceBroadcastReceiver.EXTRA_REMINDER);
        int ms = extras.getInt(ReminderServiceBroadcastReceiver.EXTRA_DELAY);
        if(ms > 0){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, ms);
            reminderService.scheduleTo(r, cal);
        }else{
            reminderService.schedule(r);
        }
    }

}
