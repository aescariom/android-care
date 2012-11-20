package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.content.Context;

public class UIReminderViewFactory {
	public static UIReminderView createView(Context context, Reminder reminder){
		// TODO cuando tengamos más clases, hacer que se decida en función del reminder y sus parámetros
		return new UIReminderBasicView(context, reminder);
	}
}
