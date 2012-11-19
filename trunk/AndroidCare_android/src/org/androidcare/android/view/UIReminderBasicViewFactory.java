package org.androidcare.android.view;

import org.androidcare.android.util.Reminder;

import android.content.Context;

public class UIReminderBasicViewFactory implements UIReminderViewFactory {

	public UIReminderView createView(Context context, Reminder reminder) {
		return new UIReminderBasicView(context, reminder);
	}

}
