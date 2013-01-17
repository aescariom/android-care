package org.androidcare.web.shared.persistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Reminder implements Serializable{

	private static final long serialVersionUID = 7505965004539903183L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;	
	@Persistent
	private String title;	
	@Persistent
	private String description;
	
	@Persistent
	private boolean repeat;	
	@Persistent
	private Date activeFrom;	
	@Persistent
	private Date activeUntil;
	@Persistent
	private Integer numerOfRepetitions;
	
	@Persistent
	private Integer endType;
	@NotPersistent
	public static final int END_TYPE_NEVER_ENDS = 0;
	@NotPersistent
	public static final int END_TYPE_UNTIL_DATE = 1;
	@NotPersistent
	public static final int END_TYPE_ITERATIONS = 2;
	
	@Persistent
	private int repeatPeriod;
	@NotPersistent
	public static final int REPEAT_PERIOD_HOUR = 0;
	@NotPersistent
	public static final int REPEAT_PERIOD_DAY = 1;
	@NotPersistent
	public static final int REPEAT_PERIOD_WEEK = 2;
	@NotPersistent
	public static final int REPEAT_PERIOD_MONTH = 3;
	@NotPersistent
	public static final int REPEAT_PERIOD_YEAR = 4;
	
	@Persistent
	private boolean[] daysOfWeekInWhichShouldTrigger;
	
	@Persistent
	private Integer repeatEachXPeriods;
	
	@Persistent
	private boolean requestConfirmation;
	
	@Persistent
	private String owner;
	
	@Persistent(mappedBy = "reminder")
	@Order(extensions = @Extension(vendorName="datanucleus",key="list-ordering", value="time desc"))
	private List<ReminderLog> log;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public Date getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(Date since) {
		this.activeFrom = since;
	}

	public Date getActiveUntil() {
		return activeUntil;
	}

	public void setActiveUntil(Date until) {
		this.activeUntil = until;
	}

	public int getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(int repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	public boolean[] getDaysOfWeekInWhichShouldTrigger() {
		if(daysOfWeekInWhichShouldTrigger == null){
			return new boolean[7];
		}else{
			return daysOfWeekInWhichShouldTrigger;
		}
	}

	public void setDaysOfWeekInWhichShouldTrigger(boolean[] weekDays) {
		this.daysOfWeekInWhichShouldTrigger = weekDays;
	}

	public int getRepeatEachXPeriods() {
		return repeatEachXPeriods;
	}

	public void setRepeatEachXPeriods(int repeatEach) {
		this.repeatEachXPeriods = repeatEach;
	}

	public boolean isRequestConfirmation() {
		return requestConfirmation;
	}

	public void setRequestConfirmation(boolean requestConfirmation) {
		this.requestConfirmation = requestConfirmation;
	}

	public Integer getEndType() {
		return endType;
	}

	public void setEndType(Integer endType) {
		this.endType = endType;
	}

	public Integer getNumerOfRepetitions() {
		return numerOfRepetitions;
	}

	public void setNumerOfRepetitions(Integer untilIterations) {
		this.numerOfRepetitions = untilIterations;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void addLog(ReminderLog log){
		if(this.log == null){
			this.log = new ArrayList<ReminderLog>();
		}
		this.log.add(log);
	}
	
	public int getLogSize(){
		if(this.log == null){
			return 0;
		}
		return this.log.size();
	}
	
	public List<ReminderLog> getLog(){
		return this.log;
	}
	//@ comentario transient?
	public void cleanForAPI() {
		owner = null;
		log = null;
	}
}
