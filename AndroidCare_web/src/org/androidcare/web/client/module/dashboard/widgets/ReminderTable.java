package org.androidcare.web.client.module.dashboard.widgets;

import java.util.List;

import org.androidcare.web.client.module.dashboard.LocalizedConstants;
import org.androidcare.web.client.module.dashboard.rpc.ReminderService;
import org.androidcare.web.client.module.dashboard.rpc.ReminderServiceAsync;
import org.androidcare.web.client.module.dashboard.widgets.forms.ReminderForm;
import org.androidcare.web.client.module.dashboard.widgets.forms.RemoveReminderForm;
import org.androidcare.web.client.observer.Observer;
import org.androidcare.web.client.widgets.DialogBoxClose;
import org.androidcare.web.shared.persistent.Reminder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

public class ReminderTable extends FlexTable implements Observer {
	
	private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);
	
	private final ReminderServiceAsync reminderService = GWT.create(ReminderService.class);

	private List<Reminder> reminders;
	
    private Image imgLoading = new Image("./images/loading_small.gif");
	
	public ReminderTable(){
		super();
		getReminders();
	}
	
	protected void getReminders() {
		this.removeAllRows();
		this.setWidget(0, 0, imgLoading);

		// Then, we send the input to the server.
		reminderService.fetchReminders(
			new AsyncCallback<List<Reminder>>() {
				public void onFailure(Throwable caught) {
					Window.alert(localizedConstants.serverError());
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(List<Reminder> reminders) {
					fill(reminders);
				}
			});
	}

	protected void fill(List<Reminder> reminders) {

		this.removeAllRows();

		this.setText(0, 0, localizedConstants.title());
		this.setText(0, 1, localizedConstants.description());
		
		this.reminders = reminders;
		for(Reminder reminder : reminders){
			addReminder(reminder);
		}
	}
	
	protected void addReminder(final Reminder reminder){
		int row = this.getRowCount();
		this.setText(row, 0, reminder.getTitle());
		String description = reminder.getDescription();
		if(description.length() > 100){
			description = description.substring(0, 100) + "...";
		}
		this.setText(row, 1, description);

		Button btnLog = new Button(localizedConstants.log());
		btnLog.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int editIndex = reminders.indexOf(reminder);
	            displayLog(editIndex);
	        }
	      });
		this.setWidget(row, 2, btnLog);
		
		Button btnEdit = new Button(localizedConstants.edit());
		btnEdit.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int editIndex = reminders.indexOf(reminder);
	            editIndex(editIndex);
	        }
	      });
		this.setWidget(row, 4, btnEdit);
		
		Button btnRemove = new Button(localizedConstants.delete());
		btnRemove.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int removeIndex = reminders.indexOf(reminder);
	            removeReminder(removeIndex);
	        }
	      });
		this.setWidget(row, 5, btnRemove);
	}

	protected void editIndex(int editIndex) {
		Reminder reminder = reminders.get(editIndex);
		ReminderForm reminderForm = new ReminderForm(reminder);
		reminderForm.addObserver(this);
		new DialogBoxClose(localizedConstants.addNewReminder(), reminderForm).show();
	}
	
	protected void displayLog(int index) {
		Reminder reminder = reminders.get(index);
		ReminderLogTable reminderTable = new ReminderLogTable(reminder);
		DialogBoxClose dialog = new DialogBoxClose(localizedConstants.displayLog(), reminderTable);
		dialog.show();
	}
	
	protected void removeReminder(int removeIndex) {
		Reminder reminder = reminders.get(removeIndex);
		RemoveReminderForm form = new RemoveReminderForm(reminder);
		form.addObserver(this);
		new DialogBoxClose(localizedConstants.removeReminder(), form).show();
	}

	@Override
	public void update() {
		getReminders();
	}
}
