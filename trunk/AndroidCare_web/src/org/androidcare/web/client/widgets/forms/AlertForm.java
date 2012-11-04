package org.androidcare.web.client.widgets.forms;

import java.util.Date;

import org.androidcare.web.client.AlertService;
import org.androidcare.web.client.AlertServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.ui.DialogBoxClose;
import org.androidcare.web.client.util.ObservableForm;
import org.androidcare.web.client.widgets.forms.panels.DateTimeBox;
import org.androidcare.web.client.widgets.forms.panels.DaysOfTheWeek;
import org.androidcare.web.shared.PeriodOfTime;
import org.androidcare.web.shared.persistent.Alert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public class AlertForm extends ObservableForm{
	private static final int TITLE_ROW = 0;
	private static final int DESCRIPTION_ROW = 1;
	private static final int REPEAT_ROW = 2;
	private static final int SINCE_ROW = 3;
	private static final int UNTIL_ROW = 4;
	private static final int REPEAT_PERIOD_ROW = 5;
	private static final int DAYS_ROW = 6;
	private static final int REPEAT_EACH_ROW = 7;
	private static final int CONFIRMATION_ROW = 8;
	//private static final int BRIEF_ROW = 9;
	private static final int BASIC_ADVANCED_ROW = 10;
	private static final int SEND_ROW = 11;
	
	private static final String grpName = "until";
	
	//Localizer
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
	
	//Container
	private Grid grid = new Grid(SEND_ROW + 1, 2);
	
	private Hyperlink lblBasicAdvanced = new Hyperlink();
	
	//Form fields
	private Label lblTitle = new Label(LocalizedConstants.title());
	private TextBox txtTitle = new TextBox();
    
    private Label lblDescription = new Label(LocalizedConstants.description());
    private TextArea txtDescription = new TextArea();

    private Label lblSince = new Label(LocalizedConstants.since());
    private DateTimeBox txtSince = new DateTimeBox();
    
    private Label lblUntil = new Label(LocalizedConstants.until());
    private RadioButton rdbUntilDate = new RadioButton(grpName, LocalizedConstants.date());
    private RadioButton rdbUntilIterations = new RadioButton(grpName, LocalizedConstants.after());
    private TextBox txtUntilIterations = new TextBox();
    private Label lblIterations = new Label(LocalizedConstants.iterations());
    private RadioButton rdbUntilNever = new RadioButton(grpName, LocalizedConstants.never());
    private DateTimeBox txtUntil = new DateTimeBox(false);

    private Label lblRepeatPeriod = new Label(LocalizedConstants.repeatPeriod());
    private ListBox ddlRepeatPeriod = new ListBox();
    
    private Label lblWeekDays = new Label(LocalizedConstants.weekDays());
    private DaysOfTheWeek pnlDaysOfTheWeek = new DaysOfTheWeek();
    
    private Label lblRepeat = new Label(LocalizedConstants.repeat());
    private CheckBox chkRepeat = new CheckBox();

    private Label lblRepeatEach = new Label(LocalizedConstants.repeatEach());
    private Label lblRepeatEachPeriod = new Label("");
    private ListBox ddlRepeatEach = new ListBox();
    
    private Label lblRequestConfirmation = new Label(LocalizedConstants.requestConfirmation());
    private CheckBox chkRequestConfirmation = new CheckBox();
    
    private Button submit = new Button(LocalizedConstants.submit());
    
    //Persistent data
    private Alert alert;
    private boolean basicMode = true;
    
	//Create a remote service proxy to talk to the server-side Greeting service.
	private final AlertServiceAsync alertService = GWT.create(AlertService.class);

	/**
	 * 
	 */
    public AlertForm() {
    	super();
    	this.alert = null;
        setUpAlertForm();
    }
    
    /**
     * 
     * @param a
     */
    public AlertForm(Alert a){
        super();
        this.alert = a;
        setUpAlertForm();
    }
    
    /**
     * 
     */
    public void setUpAlertForm(){
        addItemsToRepeatEach(30);
        setUpRepeatPeriod();
        
        addItemsToGrid();
        setFormValues();

        setWidget(grid);
    }

    /**
     * 
     */
	private void setFormValues() {
		if(alert != null){ // we have an alert to be loaded
			this.setAlertValues(this.alert);
		}else{ // let's configure the default behavior
			Alert a = new Alert();
			// let's repeat the task every day since now by default
			a.setRepeat(true);
			a.setSince(new Date());
			a.setRepeatPeriod(PeriodOfTime.DAY);
			a.setRepeatEach(1);
			a.setRequestConfirmation(true);
			a.setEndType(PeriodOfTime.NEVER_ENDS);
			this.setAlertValues(a);
		}
	}

	/**
	 * 
	 * @param alert
	 */
	private void setAlertValues(Alert alert) {
		txtTitle.setValue(alert.getTitle());
		txtDescription.setValue(alert.getDescription());
		chkRepeat.setValue(alert.isRepeat());
		txtSince.setValue(alert.getSince());
		if(alert.getUntilDate() != null){
			rdbUntilDate.setValue(true);
			untilManager();
			txtUntil.setValue(alert.getUntilDate());
		}else if(alert.getUntilIterations() != null){
			rdbUntilIterations.setValue(true);
			untilManager();
			txtUntilIterations.setValue(String.valueOf(alert.getUntilIterations()));
		}else{
			rdbUntilNever.setValue(true);
			untilManager();
		}
		String period = String.valueOf(alert.getRepeatPeriod());
		for(int i = 0; i < ddlRepeatPeriod.getItemCount(); i++){
			if(ddlRepeatPeriod.getValue(i).compareToIgnoreCase(period) == 0){
				ddlRepeatPeriod.setSelectedIndex(i);
				break;
			}
		}
		String each = String.valueOf(alert.getRepeatEach());
		for(int i = 0; i < ddlRepeatEach.getItemCount(); i++){
			if(ddlRepeatEach.getValue(i).compareToIgnoreCase(each) == 0){
				ddlRepeatEach.setSelectedIndex(i);
				break;
			}
		}
		pnlDaysOfTheWeek.setValue(alert.getWeekDays());
		chkRequestConfirmation.setValue(alert.isRequestConfirmation());
	
	    showHideRepeatRows();
	    showHideWeekDays();
	}

	/**
	 * 
	 */
	private void setUpRepeatPeriod() {
		ddlRepeatPeriod.addItem(LocalizedConstants.hour(), String.valueOf(PeriodOfTime.HOUR));
		ddlRepeatPeriod.addItem(LocalizedConstants.day(), String.valueOf(PeriodOfTime.DAY));
		ddlRepeatPeriod.addItem(LocalizedConstants.week(), String.valueOf(PeriodOfTime.WEEK));
		ddlRepeatPeriod.addItem(LocalizedConstants.month(), String.valueOf(PeriodOfTime.MONTH));
		ddlRepeatPeriod.addItem(LocalizedConstants.year(), String.valueOf(PeriodOfTime.YEAR));

		setRepeatEachPeriod();
		
		ddlRepeatPeriod.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				setRepeatEachPeriod();
				ddlRepeatEach.setSelectedIndex(0);
			}			
		});
	}

	/**
	 * 
	 */
	private void setRepeatEachPeriod() {
		lblRepeatEachPeriod.setText(ddlRepeatPeriod.getItemText(ddlRepeatPeriod.getSelectedIndex()));
	}

	/**
	 * 
	 * @param iterations
	 */
	private void addItemsToRepeatEach(int iterations) {
		ddlRepeatEach.clear();
		for(int i = 1; i <= iterations; i++){
			ddlRepeatEach.addItem(String.valueOf(i), String.valueOf(i));
		}
	}

	/**
	 * 
	 */
	private void addItemsToGrid() {		
        grid.setWidget(TITLE_ROW, 0, lblTitle);
        txtTitle.setWidth("400px");
        txtTitle.setMaxLength(50);
        grid.setWidget(TITLE_ROW, 1, txtTitle);
        
        grid.setWidget(DESCRIPTION_ROW, 0, lblDescription);
        txtDescription.setWidth("400px");
        txtDescription.setHeight("100px");
        grid.setWidget(DESCRIPTION_ROW, 1, txtDescription);
        
        grid.setWidget(REPEAT_ROW, 0, lblRepeat);
        showHideRepeatRows();
        chkRepeat.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				showHideRepeatRows();
			}
        	
        });
        grid.setWidget(REPEAT_ROW, 1, chkRepeat);
        
        grid.setWidget(SINCE_ROW, 0, lblSince);
        grid.setWidget(SINCE_ROW, 1, txtSince);
        
        grid.setWidget(UNTIL_ROW, 0, lblUntil);
        Grid grdUntil = new Grid(3, 1);
        grdUntil.setWidget(0, 0, rdbUntilNever);
        rdbUntilNever.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
		        untilManager();
			}
        });
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(rdbUntilIterations);
        rdbUntilIterations.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
		        untilManager();
			}
        });
        hp.add(txtUntilIterations);
        hp.add(lblIterations);
        grdUntil.setWidget(1, 0, hp);
        hp = new HorizontalPanel();
        hp.add(rdbUntilDate);
        rdbUntilDate.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
		        untilManager();
			}
        });
        hp.add(txtUntil);
        grdUntil.setWidget(2, 0, hp);
        grid.setWidget(UNTIL_ROW, 1, grdUntil);

        untilManager();
        
        grid.setWidget(REPEAT_PERIOD_ROW, 0, lblRepeatPeriod);
        ddlRepeatPeriod.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				showHideWeekDays();
			}
        	
        });
        showHideWeekDays();
        grid.setWidget(REPEAT_PERIOD_ROW, 1, ddlRepeatPeriod);
        
        grid.setWidget(DAYS_ROW, 0, lblWeekDays);
        grid.setWidget(DAYS_ROW, 1, pnlDaysOfTheWeek);
        
        grid.setWidget(REPEAT_EACH_ROW, 0, lblRepeatEach);
        HorizontalPanel pnlRepeatEach = new HorizontalPanel();
        pnlRepeatEach.add(ddlRepeatEach);
        pnlRepeatEach.add(lblRepeatEachPeriod);
        grid.setWidget(REPEAT_EACH_ROW, 1, pnlRepeatEach);
        
        grid.setWidget(CONFIRMATION_ROW, 0, lblRequestConfirmation);
        grid.setWidget(CONFIRMATION_ROW, 1, chkRequestConfirmation);
    
        submit.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				sendForm();
			}
        	
        });
        grid.setWidget(SEND_ROW, 0, submit);
        
        //basic-advanced

		lblBasicAdvanced.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(final ClickEvent event) {
				toggleBasicAdvanced();
			}
			
		});
		this.basicMode = false;
		toggleBasicAdvanced();
		lblBasicAdvanced.setStyleName("hyperlink_style_label");
        grid.setWidget(BASIC_ADVANCED_ROW, 1, lblBasicAdvanced);
	}
	
	/**
	 * 
	 */
	protected void toggleBasicAdvanced() {
		grid.getRowFormatter().setVisible(REPEAT_ROW, basicMode);
		grid.getRowFormatter().setVisible(UNTIL_ROW, basicMode);
		grid.getRowFormatter().setVisible(REPEAT_PERIOD_ROW, basicMode);
		grid.getRowFormatter().setVisible(DAYS_ROW, basicMode);
		grid.getRowFormatter().setVisible(REPEAT_EACH_ROW, basicMode);
		grid.getRowFormatter().setVisible(CONFIRMATION_ROW, basicMode);
		this.basicMode = !this.basicMode;
		if(this.basicMode){
			this.lblBasicAdvanced.setText(LocalizedConstants.toggleToAdvanced());
		}else{
			this.lblBasicAdvanced.setText(LocalizedConstants.toggleToBasic());
		}
		showHideRepeatRows();
		showHideWeekDays();
	}

	/**
	 * 
	 */
	private void untilManager() {
		if(rdbUntilDate.getValue()){
			txtUntil.setValue(new Date());
			txtUntil.setEnabled(true);
			txtUntilIterations.setEnabled(false);
			txtUntilIterations.setText("");
		}else if(rdbUntilIterations.getValue()){
			txtUntil.setValue(null);
			txtUntil.setEnabled(false);
			txtUntilIterations.setEnabled(true);
			txtUntilIterations.setText("30");
		}else{ // never or none selected
			rdbUntilNever.setValue(true);
			txtUntil.setValue(null);
			txtUntil.setEnabled(false);
			txtUntilIterations.setEnabled(false);
			txtUntilIterations.setText("");
		}
	}

	/**
	 * 
	 */
	protected void sendForm() {
		
		Alert alert = new Alert();
		try{
			if(this.alert != null){
				alert.setId(this.alert.getId());
			}else{
				alert.setId(-1L);
			}
			alert.setTitle(txtTitle.getText());
			alert.setDescription(txtDescription.getText());
			alert.setRepeat(chkRepeat.getValue());
			alert.setRepeatPeriod(Integer.valueOf(ddlRepeatPeriod.getValue(ddlRepeatPeriod.getSelectedIndex())));
			alert.setWeekDays(pnlDaysOfTheWeek.getDaysArray());
			alert.setSince(txtSince.getDate());
			if(txtUntilIterations.getText().trim().compareToIgnoreCase("") != 0){
				alert.setUntilIterations(Integer.parseInt(txtUntilIterations.getText()));
			}else if(txtUntil.getDate() != null){
				alert.setUntilDate(txtUntil.getDate());
			}
			alert.setUntilDate(txtUntil.getDate());
			alert.setRepeatEach(Integer.valueOf(ddlRepeatEach.getValue(ddlRepeatEach.getSelectedIndex())));
			alert.setRequestConfirmation(chkRequestConfirmation.getValue());
		}catch(Exception ex){
			return;
		}
		// Then, we send the input to the server.
		submit.setEnabled(false);
		alertService.saveAlert(alert,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						Window.alert("Error en el servidor!!!");
						submit.setEnabled(true);
					}

					public void onSuccess(final String result) {
						Object d = (Object)getParent().getParent();
						submit.setEnabled(true);
						
						broadcastObservers();
						
						if(d instanceof DialogBoxClose){
							((DialogBoxClose)d).hide();
						}
					}
				});
	}

	/**
	 * 
	 */
	private void showHideRepeatRows(){
		boolean show = chkRepeat.getValue() && !this.basicMode;
		grid.getRowFormatter().setVisible(UNTIL_ROW, show);
		grid.getRowFormatter().setVisible(REPEAT_PERIOD_ROW, show);
		grid.getRowFormatter().setVisible(REPEAT_EACH_ROW, show);
		if(show){
			showHideWeekDays();
		}else{
			grid.getRowFormatter().setVisible(DAYS_ROW, false);
		}
		
		if(show){
			lblSince.setText(LocalizedConstants.since());
		}else{
			lblSince.setText(LocalizedConstants.dateTime());
		}
	}
	
	/**
	 * 
	 */
	private void showHideWeekDays(){
		int item = Integer.parseInt(ddlRepeatPeriod.getValue(ddlRepeatPeriod.getSelectedIndex()));
		if(item == PeriodOfTime.WEEK && !this.basicMode){
			grid.getRowFormatter().setVisible(DAYS_ROW, true);
		}else{
			grid.getRowFormatter().setVisible(DAYS_ROW, false);
		}
	}
}
