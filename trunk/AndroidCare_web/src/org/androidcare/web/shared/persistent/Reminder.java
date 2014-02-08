package org.androidcare.web.shared.persistent;

import javax.jdo.annotations.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private long activeFrom;
	@Persistent
	private long activeUntil;
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
	private Integer repeatPeriod;
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
	private Boolean[] daysOfWeekInWhichShouldTrigger;
	
	@Persistent
	private Integer repeatEachXPeriods;
	
	@Persistent
	private Boolean requestConfirmation;
	
	@Persistent
	private String owner;

	@Persistent(mappedBy = "reminder")
	@Order(extensions = @Extension(vendorName="datanucleus",key="list-ordering", value="time desc"))
	private List<ReminderLog> log;
	
	@Persistent
	private String blobKey;


	public Reminder() {}
	
	public Reminder(Reminder reminder) {
		id = reminder.getId();
		title = reminder.getTitle();
		description = reminder.getDescription();
		repeat = reminder.isRepeat();
		activeFrom = new Date(reminder.getActiveFrom().getTime()). getTime();
		//@comentario me parece muy raro este  condicional no ser� reminder.getActiveUntil() != null
		if(activeUntil != 0){
			activeUntil = new Date(reminder.getActiveUntil().getTime()).getTime();
		}
		numerOfRepetitions = reminder.getNumerOfRepetitions();
		endType = reminder.getEndType();
		repeatPeriod = reminder.getRepeatPeriod();
		daysOfWeekInWhichShouldTrigger = reminder.getDaysOfWeekInWhichShouldTrigger();
		repeatEachXPeriods = reminder.getRepeatEachXPeriods();
		requestConfirmation = reminder.isRequestConfirmation();
		owner = reminder.getOwner();
		blobKey = reminder.getBlobKey();
	}

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
		return new Date(activeFrom);
	}

	public void setActiveFrom(Date since) {
		this.activeFrom = since.getTime();
	}

	public Date getActiveUntil() {
		return new Date(activeUntil);
	}

	public void setActiveUntil(Date until) {
		this.activeUntil = until.getTime();
	}

	public int getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(int repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	public Boolean[] getDaysOfWeekInWhichShouldTrigger() {
		if(daysOfWeekInWhichShouldTrigger == null){
			return new Boolean[7];
		}else{
			return daysOfWeekInWhichShouldTrigger;
		}
	}

	public void setDaysOfWeekInWhichShouldTrigger(Boolean[] weekDays) {
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
		if(log == null){
			return 0;
		}
		return log.size();
	}
	
	public List<ReminderLog> getLog(){
		return log;
	}

	public void setBlobKey(String keyString) {
		this.blobKey = keyString;
	}
	
	public String getBlobKey(){
		return blobKey;
	}
}
