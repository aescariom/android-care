package org.androidcare.web.client;

import org.androidcare.web.client.ui.DialogBoxClose;
import org.androidcare.web.client.widgets.AlertTable;
import org.androidcare.web.client.widgets.UserLocationMap;
import org.androidcare.web.client.widgets.forms.AlertForm;

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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Alejandro Escario MŽndez
 */
public class Dashboard implements EntryPoint, ClickHandler {
	
	// this variable will allow us to work with dynamic literals
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
	
	//main panels
	private TabPanel mainPanel = new TabPanel();
	private AlertTable alertTable = new AlertTable();
	private UserLocationMap map = new UserLocationMap();
	
    Button btnAddAlert = new Button(LocalizedConstants.addNew());

    /**
     * main method
     */
	public void onModuleLoad() {
		
		btnAddAlert.addClickHandler(this);

	    FlowPanel flowpanel;

	    flowpanel = new FlowPanel();
	    flowpanel.add(alertTable);
	    flowpanel.add(btnAddAlert);
	    mainPanel.add(flowpanel, "Reminders");
	    	    
	    map.setSize("100%", "500px");
	    mainPanel.add(map, "Map");
	    
	    mainPanel.selectTab(0);

	    mainPanel.setSize("100%", "500px");
	    mainPanel.addStyleName("table-center");
	    mainPanel.addSelectionHandler(new SelectionHandler<Integer>(){

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(event.getSelectedItem() == 1){
					// when the map tab is shown...
					// this will remove the gray areas of the map area
					map.centerMap();
				}
			}
	    	
	    });
	    RootPanel.get().add(mainPanel);

	}

	/**
	 * defines the behaviour of the button 'add alert'
	 */
	@Override
	public void onClick(ClickEvent event) {
		AlertForm aForm = new AlertForm();
		aForm.addObserver(alertTable);
		new DialogBoxClose(LocalizedConstants.addNewAlert(), aForm).show();
	}
}
