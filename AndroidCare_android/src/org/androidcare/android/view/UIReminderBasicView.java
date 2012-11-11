package org.androidcare.android.view;

import org.androidcare.android.R;
import org.androidcare.android.service.ReminderLogMessage;
import org.androidcare.android.util.Reminder;
import org.androidcare.common.ReminderStatusCode;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class UIReminderBasicView extends UIReminderView{

	protected Button btnPerformed;
	protected Button btnNotPerformed;
	protected TextView lblTitle;
	protected TextView lblDescription;
	
	public UIReminderBasicView(Context context, Reminder reminder) {
		super(context, reminder);
		
		inflate(context, R.layout.basic_reminder_ui, this);
		
		//2 - turning on the screen, display the activity over the locked screen, keeping the screen on, and unlocking the keyboard
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		btnPerformed = (Button) findViewById(R.id.btnOk);
		btnPerformed.setText(R.string.ok);
		btnPerformed.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				performed();
				finish();
			}
			
		});
		
		btnNotPerformed = (Button) findViewById(R.id.btnCancel);
		btnNotPerformed.setText(R.string.cancel);
		btnNotPerformed.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				notPerformed();
				finish();
			}
			
		});
		
		lblTitle = (TextView) findViewById(R.id.txtReminderTitle);
		lblTitle.setText(this.reminder.getTitle());
		
		lblDescription = (TextView) findViewById(R.id.txtReminderDescription);
		lblTitle.setText(this.reminder.getDescription());

		//5 - notifying
		postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DISPLAYED));
	}
}
