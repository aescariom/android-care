package org.androidcare.web.client.widgets.forms;

import org.androidcare.web.client.ReminderService;
import org.androidcare.web.client.ReminderServiceAsync;
import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.ui.DialogBoxClose;
import org.androidcare.web.client.util.ObservableForm;
import org.androidcare.web.shared.Strings;
import org.androidcare.web.shared.persistent.Reminder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RemoveReminderForm extends ObservableForm {

	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

    Button btnProceed = new Button(LocalizedConstants.proceed());
    Button btnCancel = new Button(LocalizedConstants.cancel());
    Label lblWarn = new Label(LocalizedConstants.aboutToDeleteReminderWarning());

    VerticalPanel generalPanel = new VerticalPanel();
    HorizontalPanel buttonPanel = new HorizontalPanel();
    

	private final ReminderServiceAsync alertService = GWT
			.create(ReminderService.class);


    public RemoveReminderForm(final Reminder reminder) {
        super();
        
        btnProceed.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				delete(reminder);
			}
        });
        
        btnCancel.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Object d = (Object)getParent().getParent();
				if(d instanceof DialogBoxClose){
					((DialogBoxClose)d).hide();
				}
			}
        });
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnProceed);
        
        lblWarn.setText(Strings.format(LocalizedConstants.aboutToDeleteReminderWarning(), reminder.getTitle()));
        
        generalPanel.add(lblWarn);
        generalPanel.add(buttonPanel);
        this.add(generalPanel);
    }

	protected void delete(Reminder reminder) {

		// Then, we send the input to the server.
		btnProceed.setEnabled(false);
		btnCancel.setEnabled(false);
		alertService.deleteReminder(reminder,
			new AsyncCallback<Boolean>() {
				public void onFailure(Throwable caught) {
					Window.alert("Error en el servidor!!!");
					btnProceed.setEnabled(true);
					btnCancel.setEnabled(true);
				}

				public void onSuccess(final Boolean result) {
					Object d = (Object)getParent().getParent();
					btnProceed.setEnabled(true);
					broadcastObservers();
					if(d instanceof DialogBoxClose){
						((DialogBoxClose)d).hide();
					}
				}
			});
	}
}
