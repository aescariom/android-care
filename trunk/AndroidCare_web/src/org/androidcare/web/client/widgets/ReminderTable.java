package org.androidcare.web.client.widgets;

import java.util.List;

import org.androidcare.web.client.DialogBoxClose;
import org.androidcare.web.client.ReminderService;
import org.androidcare.web.client.ReminderServiceAsync;
import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.observer.Observer;
import org.androidcare.web.client.widgets.forms.ReminderForm;
import org.androidcare.web.client.widgets.forms.RemoveReminderForm;
import org.androidcare.web.shared.persistent.Reminder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;

public class ReminderTable extends FlexTable implements Observer {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
	
	private final ReminderServiceAsync reminderService = GWT
			.create(ReminderService.class);

	private List<Reminder> reminders;
	
	public ReminderTable(){
		super();
		
		this.setText(0, 0, "Title");
		this.setText(0, 1, "Description");
		
		getReminders();
	}

	public void fill(List<Reminder> rs) {

		if(this.getRowCount() > 1) {
			cleanTable();
		}
		
		reminders = rs;
		for(Reminder r : rs){
			addReminder(r);
		}
	}

	private void cleanTable() {
		int rows = this.getRowCount();
		for(int i = 1; i < rows; i++){
			this.removeRow(1);
		}
	}
	
	public void addReminder(final Reminder reminder){
		int row = this.getRowCount();
		this.setText(row, 0, reminder.getTitle());
		String description = reminder.getDescription();
		if(description.length() > 100){
			description = description.substring(0, 100) + "...";
		}
		this.setText(row, 1, description);

		Button btnLog = new Button(LocalizedConstants.log());
		btnLog.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int editIndex = reminders.indexOf(reminder);
	            displayLog(editIndex);
	        }
	      });
		this.setWidget(row, 2, btnLog);
		
		Button btnEdit = new Button(LocalizedConstants.edit());
		btnEdit.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int editIndex = reminders.indexOf(reminder);
	            editIndex(editIndex);
	        }
	      });
		this.setWidget(row, 4, btnEdit);
		
		Button btnRemove = new Button("Delete");
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
		new DialogBoxClose(LocalizedConstants.addNewReminder(), reminderForm).show();
	}
	
	protected void displayLog(int index) {
		Reminder reminder = reminders.get(index);
		ReminderLogTable reminderTable = new ReminderLogTable(reminder);
		reminderTable.addObserver(this);
		DialogBoxClose dialog = new DialogBoxClose(LocalizedConstants.displayLog(), reminderTable);
		dialog.show();
	}
	
	protected void removeReminder(int removeIndex) {
		Reminder reminder = reminders.get(removeIndex);
		RemoveReminderForm form = new RemoveReminderForm(reminder);
		form.addObserver(this);
		new DialogBoxClose(LocalizedConstants.removeReminder(), form).show();
	}
	
	public void getReminders() {

		// Then, we send the input to the server.
		reminderService.fetchReminders(
			new AsyncCallback<List<Reminder>>() {
				public void onFailure(Throwable caught) {
					Window.alert(LocalizedConstants.serverError());
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(List<Reminder> rs) {
					fill(rs);
				}
			});
	}

	@Override
	public void update() {
		getReminders();
	}
}
