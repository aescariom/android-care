package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.app.Activity;
import android.content.Context;

public class UIReminderViewFactory {
    public static UIReminderView createReminderView(ReminderDialogReceiver context, Reminder reminder) {
        return new UIReminderBasicView(context, reminder);
    }
}
