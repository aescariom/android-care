package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.content.Context;

public class UIReminderViewFactory {
    public static UIReminderView createReminderView(Context context, Reminder reminder) {
        return new UIReminderBasicView(context, reminder);
    }
}
