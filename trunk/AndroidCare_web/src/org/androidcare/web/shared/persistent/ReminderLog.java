package org.androidcare.web.shared.persistent;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.androidcare.web.shared.*;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ReminderLog implements Serializable {

	private static final long serialVersionUID = 1594770176852645458L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private transient Key key;
	
	@Persistent
	private Reminder reminder;
	
	@Persistent
	private ReminderStatusCode code;
	
	@Persistent
	private Date time;

	public ReminderLog(){
		super();
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public ReminderStatusCode getCode() {
		return code;
	}

	public void setCode(ReminderStatusCode code) {
		this.code = code;
	}
}
