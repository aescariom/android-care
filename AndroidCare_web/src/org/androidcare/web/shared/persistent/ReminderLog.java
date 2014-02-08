package org.androidcare.web.shared.persistent;

import com.google.appengine.api.datastore.Key;
import org.androidcare.web.shared.ReminderStatusCode;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Date;

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
	private long time;

	@Persistent
	private long serverTime;

	public ReminderLog(){
		super();
		this.serverTime = new Date().getTime();
	}

	public ReminderLog(ReminderLog r){
		super();
		code = r.getCode();
		time = r.getTime().getTime();
		serverTime = r.getServerTime().getTime();
	}

	public Date getTime() {
		return new Date(time);
	}

	public Date getServerTime() {
		return new Date(serverTime);
	}

	public void setTime(Date time) {
		this.time = time.getTime();
	}

	public void setServerTime(Date time) {
		this.serverTime = time.getTime();
	}

	public ReminderStatusCode getCode() {
		return code;
	}

	public void setCode(ReminderStatusCode code) {
		this.code = code;
	}
}
