package org.androidcare.android.service;

import org.androidcare.android.reminders.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ReminderServiceBroadcastReceiver extends BroadcastReceiver {

	public final static String ACTION_SCHEDULE_REMINDER = "org.androidcare.android.service.SCHEDULE_REMINDER";

	public final static String EXTRA_REMINDER = "EXTRA_REMINDER";
	
	private ReminderService reminderService;

	public ReminderServiceBroadcastReceiver(ReminderService conn){
		super();
		this.reminderService = conn;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		Reminder r = (Reminder)extras.getSerializable(ReminderServiceBroadcastReceiver.EXTRA_REMINDER);
		this.reminderService.schedule(r);
	}

}
