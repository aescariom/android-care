package org.androidcare.web.client.module.dashboard.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import org.androidcare.web.client.module.dashboard.LocalizedConstants;
import org.androidcare.web.client.module.dashboard.rpc.AlarmService;
import org.androidcare.web.client.module.dashboard.rpc.AlarmServiceAsync;
import org.androidcare.web.client.module.dashboard.widgets.forms.AlarmForm;
import org.androidcare.web.client.module.dashboard.widgets.forms.RemoveAlarmForm;
import org.androidcare.web.client.observer.Observer;
import org.androidcare.web.client.widgets.DialogBoxClose;
import org.androidcare.web.shared.persistent.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmsTable extends FlexTable implements Observer{

    private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);
    private AlarmServiceAsync alarmService = GWT.create(AlarmService.class);

    private List<Alarm> alarms = new ArrayList();

    private Image imgLoading = new Image("./images/loading_small.gif");

    public AlarmsTable() {
        super();
        getAlarms();
    }

    private void getAlarms() {
        removeAllRows();
        this.setWidget(0, 0, imgLoading);
        this.addStyleName("flexTable");

        alarmService.getActiveAlarms(new AsyncCallback<List<Alarm>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(localizedConstants.serverError());
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<Alarm> result) {
                fill(result);
            }

        });
    }

    private void fill(List<Alarm> alarms) {
        this.removeAllRows();

        this.setText(0, 0, localizedConstants.alarmName());
        this.setRowFormatter(new RowFormatter());

        this.alarms = alarms;
        for(Alarm alarm : alarms){
            addAlarm(alarm);
        }
        applyDataRowStyles();
    }

    private void applyDataRowStyles() {
        HTMLTable.RowFormatter rf = this.getRowFormatter();

        rf.addStyleName(0, "flexTable-title");
        for (int row = 1; row < this.getRowCount(); ++row) {
            rf.addStyleName(row, "flexTable-row");
        }
    }

    private void addAlarm(final Alarm alarm) {
        int row = this.getRowCount();
        this.setText(row, 0, alarm.getName());

        Button btnEdit = new Button(localizedConstants.edit());
        btnEdit.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int editIndex = alarms.indexOf(alarm);
                editIndex(editIndex);
            }
        });

        btnEdit.addStyleName("edit");
        this.getCellFormatter().addStyleName(row, 2, "flexTable-button");
        this.setWidget(row, 2, btnEdit);

        Button btnRemove = new Button(localizedConstants.delete());
        btnRemove.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int removeIndex = alarms.indexOf(alarm);
                removeReminder(removeIndex);
            }
        });
        btnRemove.addStyleName("remove");
        this.getCellFormatter().addStyleName(row, 3, "flexTable-button");
        this.setWidget(row, 3, btnRemove);
    }

    private void editIndex(int editIndex) {
        Alarm alarm = alarms.get(editIndex);
        AlarmForm alarmForm = new AlarmForm(alarm);
        alarmForm.addObserver(this);
        new DialogBoxClose(localizedConstants.editAlarm(), alarmForm).show();
    }

    private void removeReminder(int removeIndex) {
        Alarm alarm = alarms.get(removeIndex);
        RemoveAlarmForm remove = new RemoveAlarmForm(alarm);
        remove.addObserver(this);
        new DialogBoxClose(localizedConstants.removeAlarm(), remove).show();
    }

    @Override
    public void update() {
        getAlarms();
    }
}
