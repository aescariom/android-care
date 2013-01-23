package org.androidcare.web.client.rpc;

import java.util.List;

import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReminderServiceAsync {
	void saveReminder(Reminder reminder, AsyncCallback<Reminder> callback)
			throws IllegalArgumentException;

	void fetchReminders(AsyncCallback<List<Reminder>> callback);
	
	void deleteReminder(Reminder reminder, AsyncCallback<Boolean> callback);

	void fetchReminderLogs(Reminder reminder, AsyncCallback<List<ReminderLog>> asyncCallback);

	void fetchReminderLogPage(Reminder reminder, int start, int length, AsyncCallback<List<ReminderLog>> callback);

	void ReminderLogCount(Reminder reminder, AsyncCallback<Integer> asyncCallback);
}
