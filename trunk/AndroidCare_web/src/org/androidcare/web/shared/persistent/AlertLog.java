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

package org.androidcare.web.shared.persistent;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.androidcare.web.shared.*;

import com.google.appengine.api.datastore.Key;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
@PersistenceCapable
public class AlertLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private transient Key key;
	
	@Persistent
	private Alert alert;
	
	@Persistent
	private AlertStatusCode code;
	
	@Persistent
	private Date time;

	public AlertLog(){
		super();
	}
	
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the code
	 */
	public AlertStatusCode getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(AlertStatusCode code) {
		this.code = code;
	}
}
