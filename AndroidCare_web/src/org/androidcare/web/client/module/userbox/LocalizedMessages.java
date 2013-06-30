package org.androidcare.web.client.module.userbox;

import com.google.gwt.i18n.client.Messages;

public interface LocalizedMessages extends Messages {

	/**
	 * labels
	 */
	@DefaultMessage("Welcome {0}")
	String loggedInAs(String name);
}
