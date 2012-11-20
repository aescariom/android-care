package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.app.Activity;
import android.os.Bundle;

/**
 * An Alert dialog receiver will display the title and description of the alert.
 * It will also send (sometimes) information to the server
 * @author Alejandro Escario MŽndez
 *
 */
public class ReminderDialogReceiver extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//1 - getting relevant information
		Bundle b = getIntent().getExtras();
		Reminder reminder = (Reminder) b.getSerializable("reminder");
		
		//2 - setting the view
		UIReminderView view  = UIReminderViewFactory.createView(this, reminder);
		
		this.setContentView(view);
	}
}