package org.androidcare.android.reminders;

/**
 * This exception will specify which was the problem while trying to
 * get a valid date
 * @author Alejandro Escario M�ndez
 *
 */
public class NoDateFoundException extends Exception {
	
	protected String message;

	public NoDateFoundException(String str) {
		this.message = str;
	}

}
