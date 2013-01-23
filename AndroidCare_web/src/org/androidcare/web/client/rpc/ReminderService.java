package org.androidcare.web.client.rpc;

import java.util.List;

import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("reminder")
public interface ReminderService extends RemoteService {
	Reminder saveReminder(Reminder reminder);

	List<Reminder> fetchReminders();
	
	Boolean deleteReminder(Reminder reminder);

	List<ReminderLog> fetchReminderLogs(Reminder reminder);

	List<ReminderLog> fetchReminderLogPage(Reminder reminder, int start, int length);

	int ReminderLogCount(Reminder reminder);
}
