package org.androidcare.web.client.module.dashboard.widgets.forms;

import java.util.Date;


import com.google.gwt.user.client.rpc.AsyncCallback;

import org.androidcare.web.client.module.dashboard.LocalizedConstants;
import org.androidcare.web.client.module.dashboard.rpc.ReminderService;
import org.androidcare.web.client.module.dashboard.rpc.ReminderServiceAsync;
import org.androidcare.web.client.module.dashboard.rpc.UploadReminderPhotoService;
import org.androidcare.web.client.module.dashboard.rpc.UploadReminderPhotoServiceAsync;
import org.androidcare.web.client.observer.ObservableForm;
import org.androidcare.web.client.widgets.DateTimeBox;
import org.androidcare.web.client.widgets.DaysOfTheWeek;
import org.androidcare.web.client.widgets.DialogBoxClose;
import org.androidcare.web.shared.persistent.Reminder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReminderForm extends ObservableForm {
	private static final int TITLE_ROW = 0;
	private static final int DESCRIPTION_ROW = 1;
	private static final int REPEAT_ROW = 2;
	private static final int SINCE_ROW = 3;
	private static final int UNTIL_ROW = 4;
	private static final int REPEAT_EACH_ROW = 5;
	private static final int DAYS_ROW = 6;
	private static final int CONFIRMATION_ROW = 7;
	private static final int UPLOAD_ROW = 8;
	private static final int BASIC_ADVANCED_ROW = 9;
	private static final int SEND_ROW = 10;
	
	private static final String grpName = "until";
	
	//Localizer
	private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);
	
	//Container
	private Grid grid = new Grid(SEND_ROW + 1, 2);
	
	private Hyperlink lblBasicAdvanced = new Hyperlink();
	
	//Form fields
	private Label lblTitle = new Label(localizedConstants.title());
	private TextBox txtTitle = new TextBox();
	private TextBox txtId = new TextBox();
    
    private Label lblDescription = new Label(localizedConstants.description());
    private TextArea txtDescription = new TextArea();

    private Label lblSince = new Label(localizedConstants.since());
    private DateTimeBox txtSince = new DateTimeBox();

    private Label lblUpload = new Label(localizedConstants.photo());
    private FileUpload fupPhoto = new FileUpload();
    private Image imgLoading = new Image("./images/loading_small.gif");
    private Button btnDeletePhoto = new Button(localizedConstants.delete());
    private Panel pnlPhoto = new HorizontalPanel();
    private Button btnPhoto = new Button();
    private Image imgPhoto = new Image();
    
    private Label lblUntil = new Label(localizedConstants.until());
    private RadioButton rdbUntilDate = new RadioButton(grpName, localizedConstants.date());
    private RadioButton rdbUntilIterations = new RadioButton(grpName, localizedConstants.after());
    private TextBox txtUntilIterations = new TextBox();
    private Label lblIterations = new Label(localizedConstants.iterations());
    private RadioButton rdbUntilNever = new RadioButton(grpName, localizedConstants.never());
    private DateTimeBox txtUntil = new DateTimeBox(false);

    private ListBox ddlRepeatPeriod = new ListBox();
    
    private Label lblWeekDays = new Label(localizedConstants.weekDays());
    private DaysOfTheWeek pnlDaysOfTheWeek = new DaysOfTheWeek();
    
    private Label lblRepeat = new Label(localizedConstants.repeat());
    private CheckBox chkRepeat = new CheckBox();

    private Label lblRepeatEach = new Label(localizedConstants.repeatEach());
    private Label lblRepeatEachPeriod = new Label("");
    private ListBox ddlRepeatEach = new ListBox();
    
    private Label lblRequestConfirmation = new Label(localizedConstants.requestConfirmation());
    private CheckBox chkRequestConfirmation = new CheckBox();
    
    private Button submit = new Button(localizedConstants.submit());
    
    //Persistent data
    private Reminder reminder;
    private boolean basicMode = true;
    
	//Create a remote service proxy to talk to the server-side Greeting service.
	private final ReminderServiceAsync alertService = GWT.create(ReminderService.class);
	private UploadReminderPhotoServiceAsync rpc = GWT.create(UploadReminderPhotoService.class);

    public ReminderForm() {
    	super();
    	reminder = null;
        setUpReminderForm();
    }
    
    public ReminderForm(Reminder reminder){
        super();
        this.reminder = reminder;
        setUpReminderForm();
    }
    
    protected void setUpReminderForm(){

    	setAction("/reminderUpload");
    	setEncoding(FormPanel.ENCODING_MULTIPART);
    	setMethod(FormPanel.METHOD_POST);
    	
    	addSubmitHandler(new SubmitHandler() {
            public void onSubmit(SubmitEvent event) {
            	ReminderForm.this.imgLoading.setVisible(true);
            }
    	});
    	
    	this.addSubmitCompleteHandler(new SubmitCompleteHandler() {
            public void onSubmitComplete(SubmitCompleteEvent event) {
            	closeForm();
            	ReminderForm.this.imgLoading.setVisible(false);
            }
    	});
        
        addItemsToRepeatEach(30);
        setUpRepeatPeriod();
        
        addItemsToGrid();
        setFormValues();

        setWidget(grid);
    }

	private void setFormValues() {
		if(reminder != null){ // we have an alert to be loaded
			setReminderValues(reminder);
		}else{ // let's configure the default behavior
			Reminder reminder = new Reminder();
			// let's repeat the task every day since now by default
			reminder.setRepeat(true);
			reminder.setActiveFrom(new Date());
			reminder.setRepeatPeriod(Reminder.REPEAT_PERIOD_DAY);
			reminder.setRepeatEachXPeriods(1);
			reminder.setRequestConfirmation(true);
			reminder.setEndType(Reminder.END_TYPE_NEVER_ENDS);
			setReminderValues(reminder);
		}
	}

	private void setReminderValues(Reminder reminder) {
		txtTitle.setValue(reminder.getTitle());
		txtDescription.setValue(reminder.getDescription());
		chkRepeat.setValue(reminder.isRepeat());
		txtSince.setValue(reminder.getActiveFrom());
		if(reminder.getActiveUntil() != null){
			rdbUntilDate.setValue(true);
			untilManager();
			txtUntil.setValue(reminder.getActiveUntil());
		}else if(reminder.getNumerOfRepetitions() != null){
			rdbUntilIterations.setValue(true);
			untilManager();
			txtUntilIterations.setValue(String.valueOf(reminder.getNumerOfRepetitions()));
		}else{
			rdbUntilNever.setValue(true);
			untilManager();
		}
		String period = String.valueOf(reminder.getRepeatPeriod());
		for(int i = 0; i < ddlRepeatPeriod.getItemCount(); i++){
			if(ddlRepeatPeriod.getValue(i).compareToIgnoreCase(period) == 0){
				ddlRepeatPeriod.setSelectedIndex(i);
				break;
			}
		}
		String each = String.valueOf(reminder.getRepeatEachXPeriods());
		for(int i = 0; i < ddlRepeatEach.getItemCount(); i++){
			if(ddlRepeatEach.getValue(i).compareToIgnoreCase(each) == 0){
				ddlRepeatEach.setSelectedIndex(i);
				break;
			}
		}
		if(reminder.getBlobKey() != null && reminder.getBlobKey() != ""){
	        imgPhoto.setUrl("./api/reminderPhoto?id=" + reminder.getBlobKey());
	        imgPhoto.setVisible(true);
	        btnDeletePhoto.setVisible(true);
		}else{
			imgPhoto.setVisible(false);
	        btnDeletePhoto.setVisible(false);
		}
		pnlDaysOfTheWeek.setValue(reminder.getDaysOfWeekInWhichShouldTrigger());
		chkRequestConfirmation.setValue(reminder.isRequestConfirmation());
	
	    showHideRepeatRows();
	    showHideWeekDays();
	    setRepeatEachPeriod();
	}

	private void setUpRepeatPeriod() {
		ddlRepeatPeriod.addItem(localizedConstants.hour(), String.valueOf(Reminder.REPEAT_PERIOD_HOUR));
		ddlRepeatPeriod.addItem(localizedConstants.day(), String.valueOf(Reminder.REPEAT_PERIOD_DAY));
		ddlRepeatPeriod.addItem(localizedConstants.week(), String.valueOf(Reminder.REPEAT_PERIOD_WEEK));
		ddlRepeatPeriod.addItem(localizedConstants.month(), String.valueOf(Reminder.REPEAT_PERIOD_MONTH));
		ddlRepeatPeriod.addItem(localizedConstants.year(), String.valueOf(Reminder.REPEAT_PERIOD_YEAR));

		setRepeatEachPeriod();
		
		ddlRepeatPeriod.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				setRepeatEachPeriod();
				ddlRepeatEach.setSelectedIndex(0);
			}			
		});
	}

	private void setRepeatEachPeriod() {
		lblRepeatEachPeriod.setText(ddlRepeatPeriod.getItemText(ddlRepeatPeriod.getSelectedIndex()));
	}

	private void addItemsToRepeatEach(int iterations) {
		ddlRepeatEach.clear();
		for(int i = 1; i <= iterations; i++){
			ddlRepeatEach.addItem(String.valueOf(i), String.valueOf(i));
		}
	}

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
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(rdbUntilIterations);
        rdbUntilIterations.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
		        untilManager();
			}
        });
        horizontalPanel.add(txtUntilIterations);
        horizontalPanel.add(lblIterations);
        grdUntil.setWidget(1, 0, horizontalPanel);
        horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(rdbUntilDate);
        rdbUntilDate.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
		        untilManager();
			}
        });
        horizontalPanel.add(txtUntil);
        grdUntil.setWidget(2, 0, horizontalPanel);
        grid.setWidget(UNTIL_ROW, 1, grdUntil);

        untilManager();
        
        ddlRepeatPeriod.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				showHideWeekDays();
			}
        	
        });
        showHideWeekDays();
        
        grid.setWidget(DAYS_ROW, 0, lblWeekDays);
        grid.setWidget(DAYS_ROW, 1, pnlDaysOfTheWeek);
        
        grid.setWidget(REPEAT_EACH_ROW, 0, lblRepeatEach);
        HorizontalPanel pnlRepeatEach = new HorizontalPanel();
        pnlRepeatEach.add(ddlRepeatEach);
        pnlRepeatEach.add(ddlRepeatPeriod);
        grid.setWidget(REPEAT_EACH_ROW, 1, pnlRepeatEach);
        
        grid.setWidget(CONFIRMATION_ROW, 0, lblRequestConfirmation);
        grid.setWidget(CONFIRMATION_ROW, 1, chkRequestConfirmation);
    
        submit.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				sendForm();
			}
        	
        });
        submit.addStyleName("save");

        grid.setWidget(UPLOAD_ROW, 0, lblUpload);
        fupPhoto.setName("photo");
        pnlPhoto.add(fupPhoto);
        pnlPhoto.add(imgLoading);
        imgLoading.setVisible(false);
        imgPhoto.setHeight("50px");
        btnPhoto.removeStyleName("gwt-Button");
        btnPhoto.addStyleName("transparent");
        btnPhoto.addClickHandler(new ClickHandler(){
			int height = 0;
			int width = 0;

			@Override
			public void onClick(ClickEvent event) {
				VerticalPanel vp = new VerticalPanel();
				final Image img = new Image(imgPhoto.getUrl());
				vp.add(img);
				final DialogBoxClose dialog = new DialogBoxClose("", vp);
				dialog.show();
				img.addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						changeImageDimensions(img);
						dialog.autoCenter();
					}
				});
				img.addLoadHandler(new LoadHandler(){

					@Override
					public void onLoad(LoadEvent event) {
						width = img.getWidth();
						height = img.getHeight();
						changeImageDimensions(img);
						dialog.autoCenter();
					}
					
				});
			}
			
			private void changeImageDimensions(Image img){
				if(img.getWidth() == width && img.getHeight() == height){
					double imgRatio = (double)img.getWidth() / img.getHeight();
					double windowRatio = (double)Window.getClientWidth() / Window.getClientHeight();
					if(imgRatio < windowRatio){
						int h = Window.getClientHeight() - 60;
						int w = (int)(imgRatio * h);
						img.setSize(w + "px", h + "px");
					}else{
						int w = Window.getClientWidth() - 20;
						int h = (int)((double)w / imgRatio);
						img.setSize(w + "px", h + "px");							
					}
				}else{
					img.setSize(width + "px", height + "px");
				}
			}
        	
        });
		btnPhoto.getElement().appendChild(imgPhoto.getElement());
        pnlPhoto.add(btnPhoto);
        btnDeletePhoto.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				btnDeletePhoto.setEnabled(false);
				alertService.deleteReminderPhoto(reminder,
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								Window.alert(localizedConstants.serverError());
								btnDeletePhoto.setEnabled(true);
							}

							public void onSuccess(final Boolean reminder) {
								if(reminder){
									btnDeletePhoto.setVisible(false);
									imgPhoto.setVisible(false);
									broadcastObservers();
								}
								btnDeletePhoto.setEnabled(true);
							}
						});
			}});
        btnDeletePhoto.addStyleName("remove");
        pnlPhoto.add(btnDeletePhoto);
        getFormUrl();
        grid.setWidget(UPLOAD_ROW, 1, pnlPhoto);
        
        grid.setWidget(SEND_ROW, 0, submit);
        
        //basic-advanced
        this.lblBasicAdvanced.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            	toggleBasicAdvanced();
            }
        });
        
		this.basicMode = false;
		toggleBasicAdvanced();
		lblBasicAdvanced.setStyleName("hyperlink_style_label");
        grid.setWidget(BASIC_ADVANCED_ROW, 1, lblBasicAdvanced);

    	txtId.setName("reminderId");
    	txtId.setVisible(false);
        grid.setWidget(BASIC_ADVANCED_ROW, 0, txtId);
	}

	private void getFormUrl() {
		rpc.getBlobStoreUrl(new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(localizedConstants.serverError());
			}

			@Override
			public void onSuccess(String result) {
				ReminderForm.this.setAction(result);
			}
			
		});
	}

	private void toggleBasicAdvanced() {
		grid.getRowFormatter().setVisible(REPEAT_ROW, basicMode);
		grid.getRowFormatter().setVisible(UNTIL_ROW, basicMode);
		grid.getRowFormatter().setVisible(DAYS_ROW, basicMode);
		grid.getRowFormatter().setVisible(REPEAT_EACH_ROW, basicMode);
		grid.getRowFormatter().setVisible(CONFIRMATION_ROW, basicMode);
		this.basicMode = !this.basicMode;
		if(this.basicMode){
			this.lblBasicAdvanced.setText(localizedConstants.toggleToAdvanced());
		}else{
			this.lblBasicAdvanced.setText(localizedConstants.toggleToBasic());
		}
		showHideRepeatRows();
		showHideWeekDays();
		
		if(this.getParent() != null){
			DialogBoxClose dialog = (DialogBoxClose)this.getParent().getParent();
			dialog.autoCenter();
		}
	}

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

	private void sendForm() {
		
		Reminder reminder = new Reminder();
		try{
			if(this.reminder != null){
				reminder.setId(this.reminder.getId());
			}else{
				reminder.setId(-1L);
			}
			reminder.setTitle(txtTitle.getText());
			reminder.setDescription(txtDescription.getText());
			reminder.setRepeat(chkRepeat.getValue());
			reminder.setRepeatPeriod(Integer.valueOf(ddlRepeatPeriod.getValue(ddlRepeatPeriod.getSelectedIndex())));
			reminder.setDaysOfWeekInWhichShouldTrigger(pnlDaysOfTheWeek.getDaysArray());
			reminder.setActiveFrom(txtSince.getDate());
			if(txtUntilIterations.getText().trim().compareToIgnoreCase("") != 0){
				reminder.setNumerOfRepetitions(Integer.parseInt(txtUntilIterations.getText()));
			}else if(txtUntil.getDate() != null){
				reminder.setActiveUntil(txtUntil.getDate());
			}
			reminder.setRepeatEachXPeriods(Integer.valueOf(ddlRepeatEach.getValue(ddlRepeatEach.getSelectedIndex())));
			reminder.setRequestConfirmation(chkRequestConfirmation.getValue());
		}catch(Exception ex){
			ex.printStackTrace();
			return;
		}
		// Then, we send the input to the server.
		submit.setEnabled(false);
		alertService.saveReminder(reminder,
				new AsyncCallback<Reminder>() {
					public void onFailure(Throwable caught) {
						Window.alert("Error en el servidor!!!");
						submit.setEnabled(true);
					}

					public void onSuccess(final Reminder reminder) {
						txtId.setText(reminder.getId().toString());
						
						if(fupPhoto.getFilename().length() == 0){
							closeForm();
						}else{
							ReminderForm.this.uploadImage();
						}
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

	private void uploadImage() {
		this.submit();
	}

	private void showHideRepeatRows(){
		boolean show = chkRepeat.getValue() && !this.basicMode;
		grid.getRowFormatter().setVisible(UNTIL_ROW, show);
		grid.getRowFormatter().setVisible(REPEAT_EACH_ROW, show);
		if(show){
			showHideWeekDays();
		}else{
			grid.getRowFormatter().setVisible(DAYS_ROW, false);
		}

		if(show){
			lblSince.setText(localizedConstants.since());
		}else{
			lblSince.setText(localizedConstants.dateTime());
		}
	}

	private void showHideWeekDays(){
		int item = Integer.parseInt(ddlRepeatPeriod.getValue(ddlRepeatPeriod.getSelectedIndex()));
		if(item == Reminder.REPEAT_PERIOD_WEEK && !this.basicMode){
			grid.getRowFormatter().setVisible(DAYS_ROW, true);
		}else{
			grid.getRowFormatter().setVisible(DAYS_ROW, false);
		}
	}
}
