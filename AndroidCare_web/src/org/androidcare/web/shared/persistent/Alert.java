package org.androidcare.web.shared.persistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.*;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
@PersistenceCapable
public class Alert implements Serializable{

	/**
	 * 
	 */
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
	
	// end detection type (never, date,...)
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
	
	@Persistent(mappedBy = "alert")
	@Order(extensions = @Extension(vendorName="datanucleus",key="list-ordering", value="time desc"))
	private List<AlertLog> log;

	/**
	 * @return the in
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @param repeat the repeat to set
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return the since
	 */
	public Date getSince() {
		return since;
	}

	/**
	 * @param since the since to set
	 */
	public void setSince(Date since) {
		this.since = since;
	}

	/**
	 * @return the until
	 */
	public Date getUntilDate() {
		return untilDate;
	}

	/**
	 * @param until the until to set
	 */
	public void setUntilDate(Date until) {
		this.untilDate = until;
	}

	/**
	 * @return the repeatPeriod
	 */
	public int getRepeatPeriod() {
		return repeatPeriod;
	}

	/**
	 * @param repeatPeriod the repeatPeriod to set
	 */
	public void setRepeatPeriod(int repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	/**
	 * @return the weekDays
	 */
	public boolean[] getWeekDays() {
		if(weekDays == null){
			return new boolean[7];
		}else{
			return weekDays;
		}
	}

	/**
	 * @param weekDays the weekDays to set
	 */
	public void setWeekDays(boolean[] weekDays) {
		this.weekDays = weekDays;
	}

	/**
	 * @return the repeatEach
	 */
	public int getRepeatEach() {
		return repeatEach;
	}

	/**
	 * @param repeatEach the repeatEach to set
	 */
	public void setRepeatEach(int repeatEach) {
		this.repeatEach = repeatEach;
	}

	/**
	 * @return the requestConfirmation
	 */
	public boolean isRequestConfirmation() {
		return requestConfirmation;
	}

	/**
	 * @param requestConfirmation the requestConfirmation to set
	 */
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
	
	public void addLog(AlertLog log){
		if(this.log == null){
			this.log = new ArrayList<AlertLog>();
		}
		this.log.add(log);
	}
	
	public int getLogSize(){
		if(this.log == null){
			return 0;
		}
		return this.log.size();
	}
	
	public List<AlertLog> getLog(){
		return this.log;
	}

	public void cleanForAPI() {
		owner = null;
		log = null;
	}
}
