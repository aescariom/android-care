package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.app.Activity;
import android.os.Bundle;

/*
 * @author Alejandro Escario MŽndez
 *
 */
public class ReminderDialogReceiver extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        Reminder reminder = (Reminder) bundle.getSerializable("reminder");
        UIReminderView view = UIReminderViewFactory.createReminderView(this, reminder);
        this.setContentView(view);
    }
}