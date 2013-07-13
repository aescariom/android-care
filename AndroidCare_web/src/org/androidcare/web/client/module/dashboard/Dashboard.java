package org.androidcare.web.client.module.dashboard;

import org.androidcare.web.client.module.dashboard.widgets.ReminderTable;
import org.androidcare.web.client.module.dashboard.widgets.UserLocationMap;
import org.androidcare.web.client.module.dashboard.widgets.forms.ReminderForm;
import org.androidcare.web.client.widgets.DialogBoxClose;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class Dashboard implements EntryPoint, ClickHandler {
	
	// this variable will allow us to work with dynamic literals
	private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);
	
	//main panels
	private TabPanel mainPanel = new TabPanel();
	private ReminderTable reminderTable = new ReminderTable();
	private UserLocationMap map = new UserLocationMap();
	
    Button btnAddReminder = new Button(localizedConstants.addNew());

	public void onModuleLoad() {

	    FlowPanel flowpanel;

	    flowpanel = new FlowPanel();
	    flowpanel.add(reminderTable);
		btnAddReminder.addClickHandler(this);
		btnAddReminder.addStyleName("new");
	    flowpanel.add(btnAddReminder);
	    mainPanel.add(flowpanel, localizedConstants.reminders());
	    	    
	    map.setSize("100%", "600px");
	    mainPanel.add(map, localizedConstants.map());
	    
	    mainPanel.selectTab(0);

	    mainPanel.setSize("100%", "400px");
	    mainPanel.addStyleName("table-center");
	    mainPanel.addSelectionHandler(new SelectionHandler<Integer>(){

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(event.getSelectedItem() == 1){
					map.getPositions();
				}
			}
	    	
	    });
	    RootPanel.get("tableContainer").add(mainPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		ReminderForm reminderForm = new ReminderForm();
		reminderForm.addObserver(reminderTable);
		new DialogBoxClose(localizedConstants.addNewReminder(), reminderForm).show();
	}
}