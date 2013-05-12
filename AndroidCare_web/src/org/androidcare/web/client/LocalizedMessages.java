package org.androidcare.web.client;

import com.google.gwt.i18n.client.Messages;
//@Comentario para que esta clase? Porque no ponerlo en la otra? O tienes intención de añadir aquí más cosas?
//Por cierto, arregle el problema de encoding por el que éstos se leía mal en el servidor
public interface LocalizedMessages extends Messages {
	/**
	 * Warnings
	 */
	@DefaultMessage("You are about to delete the reminder titled \"{0}\". This action can not be undone. Do you want to delete it anyway?")
	String aboutToDeleteReminderWarning(String title);
}
