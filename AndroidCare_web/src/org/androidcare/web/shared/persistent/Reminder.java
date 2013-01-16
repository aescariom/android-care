package org.androidcare.web.shared.persistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.*;

@PersistenceCapable
//@Comentario tenemos que  cambiar nombres tanto a la clase Alert por Reminder, como a los campos
//para usar los mismos nombres que  en la aplicación android
public class Reminder implements Serializable{

	private static final long serialVersionUID = 1284613212687698L;

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
	private Date since;
	
	@Persistent
	private Integer endType;
	
	@Persistent
	private Date untilDate;
	@Persistent
	private Integer untilIterations;
	
	@Persistent
	private Integer iterations;
	
	@Persistent
	private int repeatPeriod;
	
	@Persistent
	private boolean[] weekDays;
	
	@Persistent
	private int repeatEach;
	
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

	public Date getSince() {
		return since;
	}

	public void setSince(Date since) {
		this.since = since;
	}

	public Date getUntilDate() {
		return untilDate;
	}

	public void setUntilDate(Date until) {
		this.untilDate = until;
	}

	public int getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(int repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	public boolean[] getWeekDays() {
		if(weekDays == null){
			return new boolean[7];
		}else{
			return weekDays;
		}
	}

	public void setWeekDays(boolean[] weekDays) {
		this.weekDays = weekDays;
	}

	public int getRepeatEach() {
		return repeatEach;
	}

	public void setRepeatEach(int repeatEach) {
		this.repeatEach = repeatEach;
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

	public Integer getUntilIterations() {
		return untilIterations;
	}

	public void setUntilIterations(Integer untilIterations) {
		this.untilIterations = untilIterations;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
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

	public void cleanForAPI() {
		owner = null;
		log = null;
	}
}
