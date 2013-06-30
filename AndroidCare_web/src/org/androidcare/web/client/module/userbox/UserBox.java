package org.androidcare.web.client.module.userbox;

import org.androidcare.web.client.internacionalization.LocalizedConstants;
import org.androidcare.web.client.module.userbox.LocalizedMessages;
import org.androidcare.web.client.module.userbox.rpc.LoggedUserService;
import org.androidcare.web.client.module.userbox.rpc.LoggedUserServiceAsync;
import org.androidcare.web.shared.module.userbox.rpc.LoggedUserMessage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class UserBox  implements EntryPoint {

	private final LoggedUserServiceAsync service = GWT.create(LoggedUserService.class);
	
	// this variable will allow us to work with dynamic literals
	private LocalizedConstants localizedConstants = GWT.create(LocalizedConstants.class);

	FlowPanel flowpanel = new FlowPanel();

	public void onModuleLoad() {
		RootPanel.get("userContainer").add(flowpanel);
		getUserData();
		
	}
	
	protected void getUserData() {

		// Then, we send the input to the server.
		service.getUserData(
			new AsyncCallback<LoggedUserMessage>() {
				public void onFailure(Throwable caught) {
					Window.alert(localizedConstants.serverError());
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(LoggedUserMessage msg) {
					Label lbl = new Label("Welcome " + msg.user);
					flowpanel.add(lbl);
					HTML link = new HTML("<a style='float:right;' href=\"" + msg.logoutUrl + GWT.getHostPageBaseURL() +  "\">Logout</a>");
					flowpanel.add(link);
				}
			});
	}
}
