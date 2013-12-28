package org.androidcare.web.client.module.dashboard.widgets.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.androidcare.web.client.module.dashboard.LocalizedConstants;
import org.androidcare.web.client.module.dashboard.LocalizedMessages;
import org.androidcare.web.client.module.dashboard.rpc.AlarmService;
import org.androidcare.web.client.module.dashboard.rpc.AlarmServiceAsync;
import org.androidcare.web.client.observer.ObservableForm;
import org.androidcare.web.client.widgets.DialogBoxClose;
import org.androidcare.web.shared.persistent.Alarm;

public class RemoveAlarmForm extends ObservableForm {

	private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);
	private LocalizedMessages localizedMessages = GWT.create(LocalizedMessages.class);

    Button btnProceed = new Button(localizedConstants.proceed());
    Button btnCancel = new Button(localizedConstants.cancel());
    Label lblWarn = new Label();

    VerticalPanel generalPanel = new VerticalPanel();
    HorizontalPanel buttonPanel = new HorizontalPanel();


	private final AlarmServiceAsync alertService = GWT.create(AlarmService.class);


    public RemoveAlarmForm(final Alarm alarm) {
        super();
        
        btnProceed.addStyleName("save");
        btnProceed.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				delete(alarm);
			}
        });
        
        btnCancel.addStyleName("remove");
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
        
        lblWarn.setText(localizedMessages.aboutToDeleteAlarmWarning(alarm.getName()));
        
        generalPanel.add(lblWarn);
        generalPanel.add(buttonPanel);
        this.add(generalPanel);
    }

	protected void delete(Alarm alarm) {
		btnProceed.setEnabled(false);
		btnCancel.setEnabled(false);
		alertService.deleteAlarm(alarm,
                new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        Window.alert("Error en el servidor!!!");
                        btnProceed.setEnabled(true);
                        btnCancel.setEnabled(true);
                    }

                    public void onSuccess(final Boolean result) {
                        Object d = (Object) getParent().getParent();
                        btnProceed.setEnabled(true);
                        broadcastObservers();
                        if (d instanceof DialogBoxClose) {
                            ((DialogBoxClose) d).hide();
                        }
                    }
                });
	}
}
