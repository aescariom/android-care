package org.androidcare.web.client.widgets;

import java.util.List;

import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.observer.ObservableForm;
import org.androidcare.web.client.rpc.ReminderService;
import org.androidcare.web.client.rpc.ReminderServiceAsync;
import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class ReminderLogTable extends FormPanel {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
    
	private Reminder reminder;
	private CellTable<ReminderLog> table;
	private SimplePager pager;
	private AsyncDataProvider<ReminderLog> provider;
	
	private DateTimeFormat format = DateTimeFormat.getFormat("HH:mm:ss dd/MM/yyyy");
	
	private final ReminderServiceAsync reminderService = GWT.create(ReminderService.class);
	
    public ReminderLogTable(Reminder reminder){
        super();

        this.reminder = reminder;

        setUpTable();
        getLogs();
    }

    private void setUpTable() {
		table = new CellTable<ReminderLog>();
	    pager = new SimplePager();

		table.setPageSize(10);

		TextColumn<ReminderLog> timeColumn = new TextColumn<ReminderLog>(){
			@Override
			public String getValue(ReminderLog a){
				return format.format(a.getTime());
			}
		};
		TextColumn<ReminderLog> codeColumn = new TextColumn<ReminderLog>(){
			@Override
			public String getValue(ReminderLog reminderLog){
				LocaleInfo loc = com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale();
				return reminderLog.getCode().getDescription(loc.getLocaleName());
			}
		};
		TextColumn<ReminderLog> serverTimeColumn = new TextColumn<ReminderLog>(){
			@Override
			public String getValue(ReminderLog a){
				if(a.getServerTime() == null) return "";
				return format.format(a.getServerTime());
			}
		};

		table.addColumn(codeColumn, "Action");
		table.addColumn(timeColumn, "Time");
		table.addColumn(serverTimeColumn, "Server time");
		
	    VerticalPanel vp = new VerticalPanel();
	    vp.add(table);
	    vp.add(pager);
		setWidget(vp);
	}

	public void getLogs() {

		final Reminder reminder = this.reminder;
		reminderService.ReminderLogCount(reminder,
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
		        reminderService.fetchReminderLogPage(reminder, start, length, new AsyncCallback<List<ReminderLog>>() {
			          @Override
			          public void onFailure(Throwable caught) {
			            Window.alert(caught.getMessage());
			          }
			          @Override
			          public void onSuccess(List<ReminderLog> result) {
			            updateRowData(start, result);
			          }
			        });
		      }
		    };
		    
	    provider.addDataDisplay(table);
	 
	    pager.setDisplay(table);
	 
	}

}
