package org.androidcare.web.client.module.dashboard.widgets.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.androidcare.web.client.module.dashboard.LocalizedConstants;
import org.androidcare.web.client.module.dashboard.rpc.AlarmService;
import org.androidcare.web.client.module.dashboard.rpc.AlarmServiceAsync;
import org.androidcare.web.client.observer.ObservableForm;
import org.androidcare.web.client.widgets.DialogBoxClose;
import org.androidcare.web.shared.AlarmSeverity;
import org.androidcare.web.shared.persistent.Alarm;

public class AlarmForm extends ObservableForm {

    private static final int SEVERITY_LEVEL_ROW = 0;
    private static final int PHONE_NUMBER_ROW = 1;
    private static final int EMAIL_ROW = 2;
    private static final int MAKE_CALL_ROW = 3;
    private static final int SEND_SMS_ROW = 4;
    private static final int SEND_EMAIL_ROW = 5;

    private static final int SEND_ROW = 7;

    //Localizer
    private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);

    //Container
    private Grid grid = new Grid(SEND_ROW + 1, 2);

    //Form fields
    private Label lblSeverityLevel = new Label(localizedConstants.severityLevel());
    private ListBox ddlSeverityLevel = new ListBox();
    private TextBox txtId = new TextBox();

    private Label lblPhoneNumber = new Label(localizedConstants.phoneNumber());
    private TextBox txtPhoneNumber = new TextBox();

    private Label lblEmail = new Label(localizedConstants.email());
    private TextBox txtEmail = new TextBox();

    private Label lblMakeCall = new Label(localizedConstants.makeCall());
    private CheckBox chkMakeCall = new CheckBox();

    private Label lblSendSMS = new Label(localizedConstants.sendSMS());
    private CheckBox chkSendSMS = new CheckBox();

    private Label lblSendEmail = new Label(localizedConstants.sendEmail());
    private CheckBox chkSendEmail = new CheckBox();

    private Button submit = new Button(localizedConstants.submit());

    private final AlarmServiceAsync alarmService = GWT.create(AlarmService.class);

    //Persistent data
    private Alarm alarm;

    public AlarmForm() {
        super();
        this.alarm = null;
        setUpAlarmForm();
    }

    public AlarmForm(Alarm alarm){
        super();
        this.alarm = alarm;
        setUpAlarmForm();
    }

    private void setUpAlarmForm() {
        addItemsToGrid();
        setFormValues();
        setWidget(grid);
        generateSeverityList();
    }

    private void setFormValues() {
        if (alarm == null) {
            Alarm alarm = new Alarm();

            alarm.setAlarmSeverity(AlarmSeverity.WARNING);
            alarm.setPhoneNumber("");
            alarm.setEmailAddress("");

            alarm.initiateCallOnAlarm(false);
            alarm.sendSMSOnAlarm(false);
            alarm.sendEmailOnAlarm(false);
        }
        setAlarmValues(alarm);
    }

    private void setAlarmValues(Alarm alarm) {
        if (alarm != null) {
            txtPhoneNumber.setValue(alarm.getPhoneNumber());
            txtEmail.setValue(alarm.getEmailAddress());
            chkMakeCall.setValue(alarm.getInitiateCall());
            chkSendSMS.setValue(alarm.getSendSMS());
            chkSendEmail.setValue(alarm.getSendEmail());
        }
    }


    private void addItemsToGrid() {
        grid.setWidget(SEVERITY_LEVEL_ROW, 0, lblSeverityLevel);
        ddlSeverityLevel.setWidth("400px");
        grid.setWidget(SEVERITY_LEVEL_ROW, 1, ddlSeverityLevel);

        grid.setWidget(PHONE_NUMBER_ROW, 0, lblPhoneNumber);
        txtPhoneNumber.setWidth("400px");
        txtPhoneNumber.setMaxLength(13);
        grid.setWidget(PHONE_NUMBER_ROW, 1, txtPhoneNumber);

        grid.setWidget(EMAIL_ROW, 0, lblEmail);
        txtEmail.setWidth("400px");
        txtEmail.setMaxLength(140);
        grid.setWidget(EMAIL_ROW, 1, txtEmail);

        grid.setWidget(MAKE_CALL_ROW, 0, lblMakeCall);
        grid.setWidget(MAKE_CALL_ROW, 1, chkMakeCall);

        grid.setWidget(SEND_SMS_ROW, 0, lblSendSMS);
        grid.setWidget(SEND_SMS_ROW, 1, chkSendSMS);

        grid.setWidget(SEND_EMAIL_ROW, 0, lblSendEmail);
        grid.setWidget(SEND_EMAIL_ROW, 1, chkSendEmail);

        submit.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                sendForm();
            }

        });

        submit.addStyleName("save");

        grid.setWidget(SEND_ROW, 0, submit);

        txtId.setName("alarmId");
        txtId.setVisible(false);
    }

    private void generateSeverityList() {
        ddlSeverityLevel.addItem(localizedConstants.info(), String.valueOf(AlarmSeverity.INFO));
        ddlSeverityLevel.addItem(localizedConstants.warning(), String.valueOf(AlarmSeverity.WARNING));
        ddlSeverityLevel.addItem(localizedConstants.severe(), String.valueOf(AlarmSeverity.SEVERE));
        ddlSeverityLevel.addItem(localizedConstants.verySevere(), String.valueOf(AlarmSeverity.VERY_SEVERE));
    }

    private void sendForm() {

        Alarm alarm = new Alarm();
        alarm.setId(-1L);

        alarm.setAlarmSeverity(AlarmSeverity.getAlarmOf(ddlSeverityLevel.getValue(ddlSeverityLevel.getSelectedIndex())));
        alarm.setPhoneNumber(txtPhoneNumber.getValue());
        alarm.setEmailAddress(txtEmail.getValue());
        alarm.initiateCallOnAlarm(chkMakeCall.getValue());
        alarm.sendSMSOnAlarm(chkSendSMS.getValue());
        alarm.sendEmailOnAlarm(chkSendEmail.getValue());
        alarm.logInServerOnAlarm(true);

        alarmService.saveAlarm(alarm, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                Window.alert("Error en el servidor!!!");
            }

            @Override
            public void onSuccess(Void result) {
                closeForm();
            }
        });
    }

    private void closeForm() {
        Object d = (Object)getParent().getParent();
        submit.setEnabled(true);

        broadcastObservers();

        if(d instanceof DialogBoxClose){
            ((DialogBoxClose)d).hide();
        }
    }
}
