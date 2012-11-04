package org.androidcare.web.client.widgets;

import java.util.List;

import org.androidcare.web.client.AlertService;
import org.androidcare.web.client.AlertServiceAsync;
import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.util.ObservableForm;
import org.androidcare.web.shared.persistent.Alert;
import org.androidcare.web.shared.persistent.AlertLog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * table that lists the logs of a selected alert
 * @author Alejandro Escario MŽndez
 *
 */
public class AlertLogTable extends ObservableForm {
    
	private Alert alert;
	private CellTable<AlertLog> table;
	private SimplePager pager;
	private AsyncDataProvider<AlertLog> provider;
	

	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final AlertServiceAsync alertService = GWT
			.create(AlertService.class);
	
    /**
     * 
     * @param a
     */
    public AlertLogTable(Alert a){
        super();

        this.alert = a;

        setUpTable();
        getLogs();
    }

    /**
     * 
     */
	private void setUpTable() {
		this.table = new CellTable<AlertLog>();
	    this.pager = new SimplePager();

		this.table.setPageSize(10);

		TextColumn<AlertLog> timeColumn = new TextColumn<AlertLog>(){
			@Override
			public String getValue(AlertLog a){
				return a.getTime().toString();
			}
		};
		TextColumn<AlertLog> codeColumn = new TextColumn<AlertLog>(){
			@Override
			public String getValue(AlertLog a){
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

	/**
	 * 
	 */
	public void getLogs() {

		final Alert alert = this.alert;
		alertService.AlertLogCount(alert,
				new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						Window.alert("Error en el servidor!!!");
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(Integer count) {
					    provider.updateRowCount(count, true);
					}
				});

		provider = new AsyncDataProvider<AlertLog>() {

			@Override
		      protected void onRangeChanged(HasData<AlertLog> display) {
		        final int start = display.getVisibleRange().getStart();
		        int length = display.getVisibleRange().getLength();
		        AsyncCallback<List<AlertLog>> callback = new AsyncCallback<List<AlertLog>>() {
		          @Override
		          public void onFailure(Throwable caught) {
		            Window.alert(caught.getMessage());
		          }
		          @Override
		          public void onSuccess(List<AlertLog> result) {
		            updateRowData(start, result);
		          }
		        };
		        // The remote service that should be implemented
		        alertService.fetchAlertLogPage(alert, start, length, callback);
		      }
		    };
		    
	    provider.addDataDisplay(table);
	 
	    pager.setDisplay(table);
	 
	}

}
