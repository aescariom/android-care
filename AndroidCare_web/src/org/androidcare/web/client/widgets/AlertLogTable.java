/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.web.client.widgets;

import java.util.List;

import org.androidcare.web.client.AlertService;
import org.androidcare.web.client.AlertServiceAsync;
import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.util.ObservableForm;
import org.androidcare.web.shared.persistent.Alert;
import org.androidcare.web.shared.persistent.AlertLog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * table that lists the logs of a selected alert
 * @author Alejandro Escario MŽndez
 *
 */
public class AlertLogTable extends ObservableForm {
    
	private Alert alert;
	private FlexTable table;
	

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
		this.table = new FlexTable();

		this.table.setText(0, 0, "Time");
		this.table.setText(0, 1, "Code");
		
		this.setWidget(table);
	}

	/**
	 * 
	 */
	public void getLogs() {

		// Then, we send the input to the server.
		alertService.fetchAlertLogs(this.alert,
			new AsyncCallback<List<AlertLog>>() {
				public void onFailure(Throwable caught) {
					Window.alert("Error en el servidor!!!");
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(List<AlertLog> rs) {
					fill(rs);
				}
			});
	}
	
	/**
	 * 
	 * @param log
	 */
	public void addLogEntry(AlertLog log){
		int row = this.table.getRowCount();
		this.table.setText(row, 0, log.getTime().toString());
		this.table.setText(row, 1, log.getCode().toString());
	}

	/**
	 * 
	 * @param rs
	 */
	private void fill(List<AlertLog> rs) {
		for(AlertLog log : rs){
			addLogEntry(log);
		}
	}

}
