package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.content.Context;

public class UIReminderViewFactory {
	public static UIReminderView createView(Context context, Reminder reminder){
		// TODO cuando tengamos m�s clases, hacer que se decida en funci�n del reminder y sus par�metros
		return new UIReminderBasicView(context, reminder);
	}
}
