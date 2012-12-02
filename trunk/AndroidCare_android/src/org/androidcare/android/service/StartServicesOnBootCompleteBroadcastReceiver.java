package org.androidcare.android.service;

import org.androidcare.android.service.reminders.ReminderService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Invoked by android.intent.action.BOOT_COMPLETED
 * 
 */
public class StartServicesOnBootCompleteBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ReminderService.class);
        context.startService(i);
    }
}