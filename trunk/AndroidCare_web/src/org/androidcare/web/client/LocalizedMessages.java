package org.androidcare.web.client;

import com.google.gwt.i18n.client.Messages;

public interface LocalizedMessages extends Messages {
	/**
	 * Warnings
	 */
	@DefaultMessage("You are about to delete the reminder titled \"{0}\". This action can not be undone. Do you want to delete it anyway?")
	String aboutToDeleteReminderWarning(String title);
}
