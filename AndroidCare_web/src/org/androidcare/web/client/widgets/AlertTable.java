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
import org.androidcare.web.client.ui.DialogBoxClose;
import org.androidcare.web.client.util.Observer;
import org.androidcare.web.client.widgets.forms.AlertForm;
import org.androidcare.web.client.widgets.forms.RemoveAlertForm;
import org.androidcare.web.shared.persistent.Alert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Lists all the alerts of a user
 * @author Alejandro Escario MŽndez
 *
 */
public class AlertTable extends FlexTable implements Observer {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final AlertServiceAsync alertService = GWT
			.create(AlertService.class);

	private List<Alert> alerts;
	
	/**
	 * 
	 */
	public AlertTable(){
		super();
		
		this.setText(0, 0, "Title");
		this.setText(0, 1, "Description");
		
		getAlerts();
	}

	/**
	 * 
	 * @param rs
	 */
	public void fill(List<Alert> rs) {

		if(this.getRowCount() > 1) {
			cleanTable();
		}
		
		alerts = rs;
		for(Alert a : rs){
			addAlert(a);
		}
	}

	/**
	 * 
	 */
	private void cleanTable() {
		int rows = this.getRowCount();
		for(int i = 1; i < rows; i++){
			this.removeRow(1);
		}
	}
	
	/**
	 * 
	 * @param alert
	 */
	public void addAlert(final Alert alert){
		int row = this.getRowCount();
		this.setText(row, 0, alert.getTitle());
		String description = alert.getDescription();
		if(description.length() > 100){
			description = description.substring(0, 100) + "...";
		}
		this.setText(row, 1, description);

		Button btnLog = new Button(LocalizedConstants.log());
		btnLog.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int editIndex = alerts.indexOf(alert);
	            displayLog(editIndex);
	        }
	      });
		this.setWidget(row, 2, btnLog);
		
		Button btnEdit = new Button(LocalizedConstants.edit());
		btnEdit.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int editIndex = alerts.indexOf(alert);
	            editIndex(editIndex);
	        }
	      });
		this.setWidget(row, 4, btnEdit);
		
		Button btnRemove = new Button("Delete");
		btnRemove.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            int removeIndex = alerts.indexOf(alert);
	            removeAlert(removeIndex);
	        }
	      });
		this.setWidget(row, 5, btnRemove);
	}

	/**
	 * 
	 * @param editIndex
	 */
	protected void editIndex(int editIndex) {
		Alert a = alerts.get(editIndex);
		AlertForm aForm = new AlertForm(a);
		aForm.addObserver(this);
		new DialogBoxClose(LocalizedConstants.addNewAlert(), aForm).show();
	}

	/**
	 * 
	 * @param index
	 */
	protected void displayLog(int index) {
		Alert a = alerts.get(index);
		AlertLogTable aTable = new AlertLogTable(a);
		aTable.addObserver(this);
		DialogBoxClose dialog = new DialogBoxClose(LocalizedConstants.displayLog(), aTable);
		dialog.show();
	}

	/**
	 * 
	 * @param removeIndex
	 */
	protected void removeAlert(int removeIndex) {
		Alert alert = alerts.get(removeIndex);
		RemoveAlertForm form = new RemoveAlertForm(alert);
		form.addObserver(this);
		new DialogBoxClose(LocalizedConstants.removeAlert(), form).show();
	}
	
	/**
	 * 
	 */
	public void getAlerts() {

		// Then, we send the input to the server.
		alertService.fetchAlerts(
			new AsyncCallback<List<Alert>>() {
				public void onFailure(Throwable caught) {
					Window.alert("Error en el servidor!!!");
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(List<Alert> rs) {
					fill(rs);
				}
			});
	}

	/**
	 * 
	 */
	@Override
	public void update() {
		getAlerts();
	}
}
