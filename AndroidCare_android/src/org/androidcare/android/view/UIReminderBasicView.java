package org.androidcare.android.view;

import org.androidcare.android.R;
import org.androidcare.android.util.Reminder;
import org.androidcare.common.ReminderStatusCode;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UIReminderBasicView extends UIReminderView{

	protected Button btnPerformed;
	protected Button btnNotPerformed;
	protected TextView lblTitle;
	protected TextView lblDescription;
	protected LinearLayout wrapper;
	
	public UIReminderBasicView(Context context, Reminder reminder) {
		super(context, reminder);
		
		//2 - turning on the screen, display the activity over the locked screen, keeping the screen on, and unlocking the keyboard
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		this.wrapper = new LinearLayout(context);
		this.wrapper.setOrientation(LinearLayout.VERTICAL);

		btnPerformed = new Button(context);
		btnPerformed.setText(R.string.ok);
		btnPerformed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				performed();
				finish();
			}
			
		});
		
		btnNotPerformed = new Button(context);
		btnNotPerformed.setText(R.string.cancel);
		btnNotPerformed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				notPerformed();
				finish();
			}
			
		});
		
		lblTitle = new TextView(context);
		lblTitle.setText(this.reminder.getTitle());
		
		lblDescription = new TextView(context);
		lblTitle.setText(this.reminder.getDescription());

		this.wrapper.addView(lblTitle);
		this.wrapper.addView(lblDescription);
		
		this.wrapper.addView(btnPerformed);
		this.wrapper.addView(btnNotPerformed);
		
		this.addView(this.wrapper);

		//5 - notifying
		postData(ReminderStatusCode.ALERT_DISPLAYED);
	}
}
