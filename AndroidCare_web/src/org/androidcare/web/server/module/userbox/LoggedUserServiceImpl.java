package org.androidcare.web.server.module.userbox;

import org.androidcare.web.client.module.userbox.rpc.LoggedUserService;
import org.androidcare.web.shared.module.userbox.rpc.LoggedUserMessage;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class LoggedUserServiceImpl extends RemoteServiceServlet implements
		LoggedUserService {

	@Override
	public LoggedUserMessage getUserData() {
		LoggedUserMessage result = new LoggedUserMessage();

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null){
			result.user = user.getNickname();
		}
		
		result.logoutUrl = userService.createLogoutURL("");
		return result;
	}
}
