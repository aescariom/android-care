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

package org.androidcare.web.client.widgets.forms;

import org.androidcare.web.client.AlertService;
import org.androidcare.web.client.AlertServiceAsync;
import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.ui.DialogBoxClose;
import org.androidcare.web.client.util.ObservableForm;
import org.androidcare.web.shared.Strings;
import org.androidcare.web.shared.persistent.Alert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public class RemoveAlertForm extends ObservableForm {

	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

    Button btnProceed = new Button(LocalizedConstants.proceed());
    Button btnCancel = new Button(LocalizedConstants.cancel());
    Label lblWarn = new Label(LocalizedConstants.aboutToDeleteAlertWarning());

    VerticalPanel generalPanel = new VerticalPanel();
    HorizontalPanel buttonPanel = new HorizontalPanel();
    

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final AlertServiceAsync alertService = GWT
			.create(AlertService.class);

	/**
	 * 
	 * @param alert
	 */
    public RemoveAlertForm(final Alert alert) {
        super();
        
        btnProceed.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				delete(alert);
			}
        });
        
        btnCancel.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Object d = (Object)getParent().getParent();
				if(d instanceof DialogBoxClose){
					((DialogBoxClose)d).hide();
				}
			}
        });
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnProceed);
        
        lblWarn.setText(Strings.format(LocalizedConstants.aboutToDeleteAlertWarning(), alert.getTitle()));
        
        generalPanel.add(lblWarn);
        generalPanel.add(buttonPanel);
        this.add(generalPanel);
    }
    
    /**
     * 
     * @param alert
     */
	protected void delete(Alert alert) {

		// Then, we send the input to the server.
		btnProceed.setEnabled(false);
		btnCancel.setEnabled(false);
		alertService.deleteAlert(alert,
			new AsyncCallback<Boolean>() {
				public void onFailure(Throwable caught) {
					Window.alert("Error en el servidor!!!");
					btnProceed.setEnabled(true);
					btnCancel.setEnabled(true);
				}

				public void onSuccess(final Boolean result) {
					Object d = (Object)getParent().getParent();
					btnProceed.setEnabled(true);
					broadcastObservers();
					if(d instanceof DialogBoxClose){
						((DialogBoxClose)d).hide();
					}
				}
			});
	}
}
