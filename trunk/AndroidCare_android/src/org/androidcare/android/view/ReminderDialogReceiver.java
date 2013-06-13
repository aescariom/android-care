package org.androidcare.android.view;

import org.androidcare.android.R;
import org.androidcare.android.reminders.Reminder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

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