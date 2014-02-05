package org.androidcare.web.client.module.dashboard.widgets.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import org.androidcare.web.client.widgets.TimeBox;
import org.androidcare.web.shared.AlarmSeverity;
import org.androidcare.web.shared.persistent.Alarm;

public class AlarmForm extends ObservableForm {

    private static final int SEVERITY_LEVEL_ROW = 0;
    private static final int ALARM_NAME_ROW = SEVERITY_LEVEL_ROW + 1;
    private static final int START_TIME_ROW = ALARM_NAME_ROW + 1;
    private static final int END_TIME_ROW = START_TIME_ROW + 1;
    private static final int PHONE_NUMBER_ROW = END_TIME_ROW + 1;
    private static final int EMAIL_ROW = PHONE_NUMBER_ROW + 1;
    private static final int MAKE_CALL_ROW = EMAIL_ROW + 1;
    private static final int SEND_SMS_ROW = MAKE_CALL_ROW + 1;
    private static final int SEND_EMAIL_ROW = SEND_SMS_ROW + 1;

    private static final int SEND_ROW = SEND_EMAIL_ROW + 2;

    //Localizer
    private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);

    //Container
    private Grid grid = new Grid(SEND_ROW + 1, 2);

    //Form fields
    private Label lblName = new Label(localizedConstants.alarmName());
    private TextBox txtName = new TextBox();

    private Label lblSeverityLevel = new Label(localizedConstants.severityLevel());
    private ListBox ddlSeverityLevel = new ListBox();
    private TextBox txtId = new TextBox();

    private Label lblStartTime = new Label(localizedConstants.startTime());
    private TimeBox txtStartTime = new TimeBox();

    private Label lblEndTime = new Label(localizedConstants.endTime());
    private TimeBox txtEndTime = new TimeBox();

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

        Alarm alarm = this.alarm;

        if (alarm == null) {
            alarm = new Alarm();

            alarm.setAlarmSeverity(AlarmSeverity.INFO);
            alarm.setPhoneNumber("");
            alarm.setEmailAddress("");

            alarm.initiateCallOnAlarm(false);
            alarm.sendSMSOnAlarm(false);
            alarm.sendEmailOnAlarm(true);
        }

        setAlarmValues(alarm);
    }

    private void setAlarmValues(Alarm alarm) {
        if (alarm != null) {
            ddlSeverityLevel.setSelectedIndex(alarm.getAlarmSeverity().getId());
            txtName.setValue(alarm.getName());
            txtStartTime.setValue(alarm.getAlarmStartTime());
            txtEndTime.setValue(alarm.getAlarmEndTime());
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

        ddlSeverityLevel.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String selectedValue = ddlSeverityLevel.getValue(ddlSeverityLevel.getSelectedIndex());

                AlarmSeverity severity = AlarmSeverity.getAlarmOf(selectedValue);
                if(severity == AlarmSeverity.SEVERE || severity == AlarmSeverity.VERY_SEVERE) {
                    chkMakeCall.setValue(true);
                    chkSendSMS.setValue(true);
                }
            }
        });

        grid.setWidget(ALARM_NAME_ROW, 0, lblName);
        txtName.setWidth("400px");
        grid.setWidget(ALARM_NAME_ROW, 1, txtName);

        grid.setWidget(START_TIME_ROW, 0, lblStartTime);
        txtStartTime.setWidth("400px");
        grid.setWidget(START_TIME_ROW, 1, txtStartTime);

        grid.setWidget(END_TIME_ROW, 0, lblEndTime);
        txtEndTime.setWidth("400px");
        grid.setWidget(END_TIME_ROW, 1, txtEndTime);

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
        chkSendEmail.setValue(true);
        grid.setWidget(SEND_EMAIL_ROW, 1, chkSendEmail);

        submit.addClickHandler(new ClickHandler() {

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

        alarm.setName(txtName.getText());
        alarm.setAlarmSeverity(AlarmSeverity.getAlarmOf(ddlSeverityLevel.getValue(ddlSeverityLevel.getSelectedIndex())));
        alarm.setAlarmStartTime(txtStartTime.getValue());
        alarm.setAlarmEndTime(txtEndTime.getValue());
        alarm.setPhoneNumber(txtPhoneNumber.getValue());
        alarm.setEmailAddress(txtEmail.getValue());
        alarm.initiateCallOnAlarm(chkMakeCall.getValue());
        alarm.sendSMSOnAlarm(chkSendSMS.getValue());
        alarm.sendEmailOnAlarm(chkSendEmail.getValue());
        alarm.logInServerOnAlarm(true);

        if (this.alarm != null) {
            alarmService.deleteAlarm(this.alarm, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Error en el servidor modificando la alarma");
                }

                @Override
                public void onSuccess(Boolean result) {

                }
            });
        }

        alarmService.saveAlarm(alarm, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error en el servidor!");
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
