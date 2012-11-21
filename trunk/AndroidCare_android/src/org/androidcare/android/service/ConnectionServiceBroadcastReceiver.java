package org.androidcare.android.service;

import org.androidcare.android.reminders.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ConnectionServiceBroadcastReceiver extends BroadcastReceiver {

	public final static String ACTION_POST_MESSAGE = "org.androidcare.android.service.POST_MESSAGE";

	public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	
	private ConnectionService connectionService;

	public ConnectionServiceBroadcastReceiver(ConnectionService conn){
		super();
		this.connectionService = conn;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		Message msg = (Message)extras.getSerializable(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE);
		this.connectionService.pushLowPriorityMessage(msg);
	}

}
