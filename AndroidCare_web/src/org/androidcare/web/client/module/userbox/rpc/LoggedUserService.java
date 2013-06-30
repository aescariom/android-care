package org.androidcare.web.client.module.userbox.rpc;

import org.androidcare.web.shared.module.userbox.rpc.LoggedUserMessage;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("users")
public interface LoggedUserService extends RemoteService {

	LoggedUserMessage getUserData();
}
