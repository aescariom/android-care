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

package org.androidcare.web.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * 
 * @author Alejandro Escario M�ndez
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