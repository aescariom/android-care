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

package org.androidcare.android.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Alert class (Notifications)
 * @author Alejandro Escario M�ndez
 *
 */
public class Reminder implements Serializable{
	
	public static final int HOUR = 0;
	public static final int DAY = 1;
	public static final int WEEK = 2;
	public static final int MONTH = 3;
	public static final int YEAR = 4;
		
	public static final int NEVER_ENDS = 0;
	public static final int UNTIL_DATE = 1;
	public static final int ITERATIONS = 2;
	
	//
	private static final long serialVersionUID = 5999355389212728810L;
	
	//Alert data - the same than in the web
	private int id;
	private String title;
	private String description;
	private boolean repeat;
	private Calendar since;
	private Calendar untilDate;
	private int untilIterations;
	private int iterations;
	private int endType;
	private int repeatPeriod;
	private Week week;
	private int repeatEach;
	private boolean requestConfirmation;
	
	// default date time format
	private final static DateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss 'UTC' yyyy", Locale.ENGLISH);
	
	/**
	 * default constructor
	 */
	public Reminder(){
	}
	
	/**
	 * creates an alert from a JSON object
	 * @param obj
	 */
	public Reminder(JSONObject obj) {
		try{
			
			// Mandatory fields
			this.setId(Integer.parseInt(obj.getString("id")));
			this.setTitle(obj.getString("title"));
			this.setSince(sdf.parse(obj.getString("since")));
			this.setRepeat(obj.getBoolean("repeat"));
			this.setRequestConfirmation(obj.getBoolean("requestConfirmation"));
			
			// Optional fields
			try { 
				this.setDescription(obj.getString("description")); 
			} catch (JSONException e) { 
				Log.w("Parsing alert", "No description found"); 
			}
			
			if(this.isRepeat()){
				this.setRepeatEach(obj.getInt("repeatEach")); 
				this.setEndType(obj.getInt("endType"));
				this.setUntilIterations(obj.getInt("untilIterations"));
				this.setRepeatPeriod(obj.getInt("repeatPeriod"));
				this.setUntilDate(sdf.parse(obj.getString("untilDate"))); 
				this.setWeek(new Week(obj.getJSONArray("weekDays")));
			}
		}catch (JSONException e) { 
			Log.w("Parsing alert", "Mandatory fields not found"); 
		} catch (ParseException e) {
			Log.w("Parsing alert", "Incorrect date format"); 
		}
	}

	/**
	 * custom constructor
	 * @param title
	 * @param description
	 * @param startTime
	 * @param endTime
	 * @param repeat
	 * @param endType
	 * @param repeatPeriod
	 * @param repeatEach
	 */
	public Reminder(String title, String description, Date startTime, Date endTime, boolean repeat,
			int endType, int repeatPeriod, int repeatEach) {
		this.setTitle(title);
		this.setDescription(description);
		Calendar cStart = Calendar.getInstance();
		cStart.setTime(startTime);
		cStart.set(Calendar.SECOND, 0);
		cStart.set(Calendar.MILLISECOND, 0);
		this.since = cStart;
		this.repeat = repeat;
		this.endType = endType;
		Calendar cEnd = Calendar.getInstance();
		cEnd.setTime(endTime);
		cEnd.set(Calendar.SECOND, 0);
		cEnd.set(Calendar.MILLISECOND, 0);
		this.untilDate = cEnd;
		this.repeatPeriod = repeatPeriod;
		this.repeatEach = repeatEach;
	}
	
	/**
	 * custom constructor
	 * @param title
	 * @param description
	 * @param startTime
	 * @param endTime
	 * @param repeat
	 * @param endType
	 * @param repeatPeriod
	 * @param repeatEach
	 * @param iterations
	 */
	public Reminder(String title, String description, Date startTime, Date endTime, boolean repeat,
			int endType, int repeatPeriod, int repeatEach, int iterations) {
		this(title, description, startTime, endTime, repeat, endType, repeatPeriod, repeatEach);
		this.setUntilIterations(iterations);
	}

	/**
	 * string representation
	 */
	public String toString(){
		return "\n" + getTitle() + " - " + this.getSince().getTime().toString();
	}
	
	// getters and setters
	
	/**
	 * @return the since
	 */
	public Calendar getSince() {
		return since;
	}

	/**
	 * @param since the since to set
	 */
	public void setSince(Calendar since) {
		since.set(Calendar.SECOND, 0);
		since.set(Calendar.MILLISECOND, 0);
		this.since = since;
	}

	/**
	 * @param since the since to set
	 */
	public void setSince(Date since) {
		Calendar c = Calendar.getInstance();
		c.setTime(since);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		this.since = c;
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
	 * @return the untilDate
	 */
	public Calendar getUntilDate() {
		return untilDate;
	}

	/**
	 * @param untilDate the untilDate to set
	 */
	public void setUntilDate(Calendar untilDate) {
		untilDate.set(Calendar.SECOND, 0);
		untilDate.set(Calendar.MILLISECOND, 0);
		this.untilDate = untilDate;
	}

	/**
	 * @param since the since to set
	 */
	public void setUntilDate(Date until) {
		Calendar c = Calendar.getInstance();
		c.setTime(until);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		this.untilDate = c;
	}

	/**
	 * @return the endType
	 */
	public int getEndType() {
		return endType;
	}

	/**
	 * @param endType the endType to set
	 */
	public void setEndType(int endType) {
		this.endType = endType;
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
	 * @return the week
	 */
	public Week getWeek() {
		return week;
	}

	/**
	 * @param week the week to set
	 */
	public void setWeek(Week week) {
		this.week = week;
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the iterations
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUntilIterations() {
		return untilIterations;
	}

	private void setUntilIterations(int untilIterations) {
		this.untilIterations = untilIterations;
	}
}
