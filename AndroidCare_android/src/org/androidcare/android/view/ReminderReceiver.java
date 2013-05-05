package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

/**
 * Handles the events related to Reminders
 */
public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {        
        Bundle bundle = intent.getExtras();
        Reminder reminder = (Reminder) bundle.getSerializable("reminder");
        displayDialog(context, reminder);
        Log.i("ReminderReceiver", reminder.toString());
    }

    private void displayDialog(Context ctx, Reminder reminder) {
        // 1 - setting up the intent
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(ctx, ReminderDialogReceiver.class);
        intent.putExtra("reminder", reminder);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 2 - displaying the activity
        ctx.startActivity(intent);
    }
}
