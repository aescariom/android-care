package org.androidcare.web.client;

import org.androidcare.web.client.ui.DialogBoxClose;
import org.androidcare.web.client.widgets.AlertTable;
import org.androidcare.web.client.widgets.forms.AlertForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Alejandro Escario MŽndez
 */
public class Dashboard implements EntryPoint, ClickHandler {
	
	// this variable will allow us to work with dynamic literals
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
	
	//main panels
	private VerticalPanel mainPanel = new VerticalPanel();
	private AlertTable alertTable = new AlertTable();
	
    Button btnAddAlert = new Button(LocalizedConstants.addNew());

    /**
     * main method
     */
	public void onModuleLoad() {
		
		btnAddAlert.addClickHandler(this);
		
		mainPanel.add(alertTable);
		
		mainPanel.add(btnAddAlert);

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
