package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

public class UIReminderViewFactory {
    public static UIReminderView createReminderView(ReminderDialogReceiver context, Reminder reminder) {
        return new UIReminderBasicSliderView(context, reminder);
        //return new UIReminderBasicView(context, reminder);
    }
}
