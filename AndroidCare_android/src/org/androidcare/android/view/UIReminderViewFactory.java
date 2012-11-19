package org.androidcare.android.view;

import org.androidcare.android.util.Reminder;

import android.content.Context;

public interface UIReminderViewFactory {
	public UIReminderView createView(Context context, Reminder reminder);
}
