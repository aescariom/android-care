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

	@Persistent
	private Date serverTime;

	public ReminderLog(){
		super();
		this.serverTime = new Date();
	}

	public ReminderLog(ReminderLog r){
		super();
		this.code = r.getCode();
		this.time = new Date(r.getTime().getTime());
		this.serverTime = new Date(r.getServerTime().getTime());
	}

	public Date getTime() {
		return time;
	}

	public Date getServerTime() {
		return serverTime;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setServerTime(Date time) {
		this.serverTime = time;
	}

	public ReminderStatusCode getCode() {
		return code;
	}

	public void setCode(ReminderStatusCode code) {
		this.code = code;
	}
}
