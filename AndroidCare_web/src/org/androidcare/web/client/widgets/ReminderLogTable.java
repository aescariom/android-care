package org.androidcare.web.client.widgets;

import java.util.List;

import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.observer.ObservableForm;
import org.androidcare.web.client.rpc.ReminderService;
import org.androidcare.web.client.rpc.ReminderServiceAsync;
import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class ReminderLogTable extends ObservableForm {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
    
	private Reminder reminder;
	private CellTable<ReminderLog> table;
	private SimplePager pager;
	private AsyncDataProvider<ReminderLog> provider;
	
	private final ReminderServiceAsync reminderService = GWT
			.create(ReminderService.class);
	
    public ReminderLogTable(Reminder r){
        super();

        this.reminder = r;

        setUpTable();
        getLogs();
    }

    private void setUpTable() {
		this.table = new CellTable<ReminderLog>();
	    this.pager = new SimplePager();

		this.table.setPageSize(10);

		TextColumn<ReminderLog> timeColumn = new TextColumn<ReminderLog>(){
			@Override
			public String getValue(ReminderLog a){
				return a.getTime().toString();
			}
		};
		TextColumn<ReminderLog> codeColumn = new TextColumn<ReminderLog>(){
			@Override
			public String getValue(ReminderLog a){
				return a.getCode().toString();
			}
		};

		this.table.addColumn(timeColumn, "Time");
		this.table.addColumn(codeColumn, "Action");
		
	    VerticalPanel vp = new VerticalPanel();
	    vp.add(table);
	    vp.add(pager);
		this.setWidget(vp);
	}

	public void getLogs() {

		final Reminder alert = this.reminder;
		reminderService.ReminderLogCount(alert,
				new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						Window.alert(LocalizedConstants.serverError());
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(Integer count) {
					    provider.updateRowCount(count, true);
					}
				});

		provider = new AsyncDataProvider<ReminderLog>() {

			@Override
		      protected void onRangeChanged(HasData<ReminderLog> display) {
		        final int start = display.getVisibleRange().getStart();
		        int length = display.getVisibleRange().getLength();
		        AsyncCallback<List<ReminderLog>> callback = new AsyncCallback<List<ReminderLog>>() {
		          @Override
		          public void onFailure(Throwable caught) {
		            Window.alert(caught.getMessage());
		          }
		          @Override
		          public void onSuccess(List<ReminderLog> result) {
		            updateRowData(start, result);
		          }
		        };
		        // The remote service that should be implemented
		        reminderService.fetchReminderLogPage(alert, start, length, callback);
		      }
		    };
		    
	    provider.addDataDisplay(table);
	 
	    pager.setDisplay(table);
	 
	}

}
