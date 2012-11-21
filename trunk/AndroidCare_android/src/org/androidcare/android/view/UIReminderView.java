package org.androidcare.android.view;

import org.androidcare.android.reminders.NoDateFoundException;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.ConnectionServiceBroadcastReceiver;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.ReminderLogMessage;
import org.androidcare.android.service.ReminderService;
import org.androidcare.android.service.ReminderServiceBroadcastReceiver;
import org.androidcare.common.ReminderStatusCode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

public abstract class UIReminderView extends RelativeLayout {
	
	protected Reminder reminder;

	public UIReminderView(Context context, Reminder reminder) {
		super(context);
		this.reminder = reminder;
		reschedule(reminder);
	}
	
	public void performed(){
		 postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DONE));
	}
	
	public void notPerformed(){
		 postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_IGNORED));
	}
	
	public void delayed(){
		
	}
	
	public void displayed(){
		 postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DISPLAYED));
	}
	
	public void finish(){
		Activity parent = (Activity)getContext();
		parent.finish();
	}
	
	protected Window getWindow(){
		Activity parent = (Activity)getContext();
		return parent.getWindow();
	}

	protected void reschedule(Reminder r) {
		Intent intent = new Intent(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);
		intent.putExtra(ReminderServiceBroadcastReceiver.EXTRA_REMINDER, r);
		this.getContext().sendBroadcast(intent);
	}

	protected void postData(Message message) {
		Intent intent = new Intent(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);
		intent.putExtra(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE, message);
		this.getContext().sendBroadcast(intent);
	}
}
