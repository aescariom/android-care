package org.androidcare.web.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public final class PMF {
	private static final PersistenceManagerFactory pmfInstance =
	    JDOHelper.getPersistenceManagerFactory("transactions-optional");

	/**
	 * 
	 */
	private PMF() {}

	/**
	 * 
	 * @return
	 */
	public static PersistenceManagerFactory get() {
	    return pmfInstance;
	}
}
