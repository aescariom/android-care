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
