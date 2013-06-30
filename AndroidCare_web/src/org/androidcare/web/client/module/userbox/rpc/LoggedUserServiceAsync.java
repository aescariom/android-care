package org.androidcare.web.client.module.userbox.rpc;

import org.androidcare.web.shared.module.userbox.rpc.LoggedUserMessage;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoggedUserServiceAsync {
	void getUserData(AsyncCallback<LoggedUserMessage> callback);
}
