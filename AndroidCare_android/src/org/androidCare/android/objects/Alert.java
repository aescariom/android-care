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

package org.androidCare.android.objects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.androidCare.android.exceptions.NoDateFoundException;
import org.androidCare.android.exceptions.NoDaySelectedException;
import org.androidCare.android.miscellaneous.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Alert class (Notifications)
 * @author Alejandro Escario MŽndez
 *
 */
public class Alert implements Serializable{
	
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
	public Alert(){
	}
	
	/**
	 * creates an alert from a JSON object
	 * @param obj
	 */
	public Alert(JSONObject obj) {
		try { this.setId(Integer.parseInt(obj.getString("id"))); } catch (JSONException e) { Log.w("Parsing alert", "No id found"); }
		try { this.setTitle(obj.getString("title")); } catch (JSONException e) { Log.w("Parsing alert", "No title found"); }
		try { this.setDescription(obj.getString("description")); } catch (JSONException e) { Log.w("Parsing alert", "No description found"); }
		try { this.setRepeatEach(obj.getInt("repeatEach")); } catch (JSONException e) { Log.w("Parsing alert", "No repeatEach found"); }
		try { this.setSince(sdf.parse(obj.getString("since"))); } catch (ParseException e) { e.printStackTrace(); } catch (Exception e) { Log.w("Parsing alert", "No since found"); }
		try { this.setUntilDate(sdf.parse(obj.getString("untilDate"))); } catch (ParseException e) { e.printStackTrace(); } catch (Exception e) { Log.w("Parsing alert", "No untilDate found"); }
		try { this.setWeek(new Week(obj.getJSONArray("weekDays"))); } catch (Exception e) { Log.w("Parsing alert", "No weekDays found"); }
		try { this.setRepeat(obj.getBoolean("repeat")); } catch (Exception e) { Log.w("Parsing alert", "No repeat found"); }
		try { this.setRepeatPeriod(obj.getInt("repeatPeriod")); } catch (Exception e) { Log.w("Parsing alert", "No repeatPeriod found"); }
		try { this.untilIterations = obj.getInt("untilIterations"); } catch (Exception e) { Log.w("Parsing alert", "No untilIterations found"); }
		try { this.setEndType(obj.getInt("endType")); } catch (Exception e) { Log.w("Parsing alert", "No endType found"); }
		try { this.setRequestConfirmation(obj.getBoolean("requestConfirmation")); } catch (Exception e) { Log.w("Parsing alert", "No requestConfirmation found"); }
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
	public Alert(String title, String description, Date startTime, Date endTime, boolean repeat,
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
	public Alert(String title, String description, Date startTime, Date endTime, boolean repeat,
			int endType, int repeatPeriod, int repeatEach, int iterations) {
		this(title, description, startTime, endTime, repeat, endType, repeatPeriod, repeatEach);
		this.untilIterations = iterations;
	}

	/**
	 * string representation
	 */
	public String toString(){
		return "\n" + getTitle() + " - " + this.getSince().getTime().toString();
	}

	/**
	 * returns the next date and time in which the alert should be triggered
	 * @param time
	 * @return 
	 * @throws NoDateFoundException 
	 */
	public Date getNextTimeLapse(Date time) throws NoDateFoundException {
		time.setSeconds(0);
		if(this.getSince() != null){
			if(time.before(this.since.getTime()) && this.repeatPeriod != Constants.WEEK){ 
				//If we are before the start date, let's select the start date as the next execution time
				return this.getSince().getTime();
			}else if(isRepeat()){
				// let's look for the next ocurrence
				switch (this.getEndType()){
				case Constants.NEVER_ENDS:
					return getNextTimeLapseWithNoEnd(time);
				case Constants.UNTIL_DATE:
					return getNextTimeLapseByDate(time);
				case Constants.ITERATIONS:
					return getNextTimeLapseByIterations(time);
				default:
					throw new NoDateFoundException();
				}
			}
		}

		throw new NoDateFoundException();
	}

	/**
	 * Returns the next execution time based on the limitation of the number of executions
	 * @param time
	 * @return
	 * @throws NoDateFoundException
	 */
	private Date getNextTimeLapseByIterations(Date time) throws NoDateFoundException {
		switch(this.getRepeatPeriod()){
		case Constants.HOUR:
			Date nextHour = this.getNextHourByTimeLapse(time);
			if(this.executedTimesUntilByHour(nextHour) < this.untilIterations){
				return nextHour;
			}
			break;
		case Constants.DAY:
			Date nextDay = this.getNextDayByTimeLapse(time);
			if(this.executedTimesUntilByDay(nextDay) < this.untilIterations){
				return nextDay;
			}
			break;
		case Constants.WEEK:
			Date nextDayOfTheWeek = this.getNextWeekDayByTimeLapse(time);
			if(this.executedTimesUntilByWeek(nextDayOfTheWeek) < this.untilIterations){
				return nextDayOfTheWeek;
			}
			break;
		case Constants.MONTH:
			Date nextMonth = this.getNextMonthByTimeLapse(time);
			if(this.executedTimesUntilByMonth(nextMonth) < this.untilIterations){
				return nextMonth;
			}
			break;
		case Constants.YEAR:
			Date nextYear = this.getNextYearByTimeLapse(time);
			if(this.executedTimesUntilByYear(nextYear) < this.untilIterations){
				return nextYear;
			}
			break;
		}

		throw new NoDateFoundException();
	}

	/**
	 * number of times that the alert has been executed (in hours)
	 * @param time
	 * @return
	 */
	private int executedTimesUntilByHour(Date time) {
		//1 - number of milliseconds in an hour
		long factor = 1000*60*60;

		//2 - calculating the number of days in this lapse of time
		long diff = (time.getTime() - this.since.getTimeInMillis());
		int hours = (int)(diff/factor);

		return hours / this.repeatEach;
	}
	
	/**
	 * number of times that the alert has been executed (in days)
	 * @param time
	 * @return
	 */
	private int executedTimesUntilByDay(Date time) {
		//1 - number of milliseconds in a day
		long factor = 1000*60*60*24;

		//2 - calculating the number of days in this lapse of time
		long diff = (time.getTime() - this.since.getTimeInMillis());
		int days = (int)(diff/factor);

		return days / this.repeatEach;
	}
	
	/**
	 * number of times that the alert has been executed (in weeks)
	 * @param time
	 * @return
	 */
	private int executedTimesUntilByWeek(Date time) {
		//1 - number of milliseconds in a week
		long factor = 1000*60*60*24*7; 
		
		//2 - calculating the number of weeks in this lapse of time
		long diff = (time.getTime() - this.since.getTimeInMillis());
		int weeks = (int)(diff/factor);

		return weeks / this.repeatEach;
	}
	
	/**
	 * number of times that the alert has been executed (in months)
	 * @param time
	 * @return
	 */
	private int executedTimesUntilByMonth(Date time) {
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);

		int diff = (time.getYear() * 12 + time.getMonth()) - (this.since.getTime().getYear() * 12 + this.since.getTime().getMonth());

		if(!this.isBeforeInYear(cTime)){
			diff++;
		}
		
		return diff / this.getRepeatEach();
	}
	
	/**
	 * number of times that the alert has been executed (in years)
	 * @param time
	 * @return
	 */
	private int executedTimesUntilByYear(Date time) {
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);

		int diff = time.getYear() - this.since.getTime().getYear();

		if(!this.isBeforeInMonth(cTime)){
			diff++;
		}
		
		return diff / this.getRepeatEach();
	}

	/**
	 * returns the next triggering date
	 * @param time
	 * @return
	 * @throws NoDateFoundException
	 */
	private Date getNextTimeLapseWithNoEnd(Date time) throws NoDateFoundException {
		//1 - let's calculate the next ocurrence
		switch(this.getRepeatPeriod()){
		case Constants.HOUR:
			Date nextHour = this.getNextHourByTimeLapse(time);
			return nextHour;
		case Constants.DAY:
			Date nextDay = this.getNextDayByTimeLapse(time);
			return nextDay;
		case Constants.WEEK:
			Date nextDayOfTheWeek = this.getNextWeekDayByTimeLapse(time);
			return nextDayOfTheWeek;
		case Constants.MONTH:
			Date nextMonth = this.getNextMonthByTimeLapse(time);
			return nextMonth;
		case Constants.YEAR:
			Date nextYear = this.getNextYearByTimeLapse(time);
			return nextYear;
		}

		throw new NoDateFoundException();

	}

	/**
	 * returns the next triggering date
	 * @param time
	 * @return
	 * @throws NoDateFoundException
	 */
	private Date getNextTimeLapseByDate(Date time) throws NoDateFoundException {
		//1 - is this alert still active? or, on the other hand, the end date is in the past
		if(this.getUntilDate() == null || time.after(this.getUntilDate().getTime())){
			throw new NoDateFoundException();
		}

		//2 - let's calculate the next ocurrence
		switch(this.getRepeatPeriod()){
		case Constants.HOUR:
			Date nextHour = this.getNextHourByTimeLapse(time);
			if(nextHour.before(this.getUntilDate().getTime())){
				return nextHour;
			}
			break;
		case Constants.DAY:
			Date nextDay = this.getNextDayByTimeLapse(time);
			if(nextDay.before(this.getUntilDate().getTime())){
				return nextDay;
			}
			break;
		case Constants.WEEK:
			Date nextDayOfTheWeek = this.getNextWeekDayByTimeLapse(time);
			if(nextDayOfTheWeek.before(this.getUntilDate().getTime())){
				return nextDayOfTheWeek;
			}
			break;
		case Constants.MONTH:
			Date nextMonth = this.getNextMonthByTimeLapse(time);
			if(nextMonth.before(this.getUntilDate().getTime())){
				return nextMonth;
			}
			break;
		case Constants.YEAR:
			Date nextYear = this.getNextYearByTimeLapse(time);
			if(nextYear.before(this.getUntilDate().getTime())){
				return nextYear;
			}
			break;
		}

		throw new NoDateFoundException();
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of months or days of the week between alarms
	 * @param time
	 * @return
	 */
	private Date getNextYearByTimeLapse(Date time) {
		int year;

		//1 - calculating the reference times
		Date auxDate = new Date(this.getSince().getTimeInMillis());
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);
		
		//2 - get the difference (in months) between today and the start date
		int diff = cTime.get(Calendar.YEAR) - this.since.get(Calendar.YEAR);
		//3 - how many weeks passed since the last executed interval? 
		int aux = diff % this.getRepeatEach(); 
		
		if(aux == 0){
			//4 - this is an active year, let's check if we are before or after the trigger moment
			if(this.isBeforeInYear(cTime)){
				year = diff;
			}else{
				year = (diff + this.getRepeatEach() - aux);
			}
		}else{
			//4 - this is not an active year, let's jump to the next one
			year = (diff + this.getRepeatEach() - aux);
		}
		
		//5 - adding years to the start date
		year += auxDate.getYear();
		
		auxDate.setYear(year);
		
		return auxDate;
	}

	/**
	 * Determines if we are before or after a 'moment' (day, hour, minute) inside a year 
	 * @param cTime
	 * @return
	 */
	private boolean isBeforeInYear(Calendar cTime) {
		return cTime.get(Calendar.MONTH) < this.since.get(Calendar.MONTH) || 
				(cTime.get(Calendar.MONTH) == this.since.get(Calendar.MONTH) &&
					this.isBeforeInMonth(cTime));
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of months or days of the week between alarms
	 * @param time
	 * @return
	 */
	private Date getNextMonthByTimeLapse(Date time) {
		int months, monthsAux, year;
		
		//1 - calculating the reference times
		Date auxDate = new Date(this.getSince().getTimeInMillis());
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);

		//2 - get the difference (in months) between today and the start date
		int diff = (cTime.get(Calendar.YEAR) * 12 + cTime.get(Calendar.MONTH)) - (this.since.get(Calendar.YEAR) * 12 + this.since.get(Calendar.MONTH));
		//3 - how many weeks passed since the last executed interval? 
		int aux = diff % this.getRepeatEach(); 
		
		if(aux == 0){
			//4 - this is an active month, let's check if we are before or after the trigger moment
			if(this.isBeforeInMonth(cTime)){
				months = diff;
			}else{
				months = (diff + this.getRepeatEach() - aux);
			}
		}else{
			//4 - this month is not active, let's jump to the next one
			months = (diff + this.getRepeatEach() - aux);
		}
		
		//5 - calculating the new month and year
		monthsAux = auxDate.getMonth() + months;
		year = auxDate.getYear() + monthsAux / 12;
		months = monthsAux % 12;
		
		//6 - setting up the next date
		auxDate.setMonth(months);
		auxDate.setYear(year);
		
		return auxDate;
	}

	/**
	 * Determines if we are before or after a 'moment' (day, hour, minute) inside a month 
	 * @param cTime
	 * @return
	 */
	private boolean isBeforeInMonth(Calendar cTime) {
		return cTime.get(Calendar.DAY_OF_MONTH) < this.since.get(Calendar.DAY_OF_MONTH) ||
				(cTime.get(Calendar.DAY_OF_MONTH) == this.since.get(Calendar.DAY_OF_MONTH)  && 
					(cTime.get(Calendar.HOUR_OF_DAY) < this.since.get(Calendar.HOUR_OF_DAY) || 
						(cTime.get(Calendar.HOUR_OF_DAY) == this.since.get(Calendar.HOUR_OF_DAY) && cTime.get(Calendar.MINUTE) < this.since.get(Calendar.MINUTE) )));
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of weeks or days of the week between alarms
	 * @param time
	 * @return
	 * @throws NoDateFoundException
	 */
	private Date getNextWeekDayByTimeLapse(Date time) throws NoDateFoundException {
		/*
		 * NOTES:
		 * 		- the first day of the week is monday
		 */
		int nextDay = 0;		
		Date reference = null;

		//1 - milliseconds in a day and in a whole week
		long dayFactor = 1000*60*60*24;	
		long factor = 1000*60*60*24*7; 
		//2 - getting the time lapse between the start date and today
		long diff = (time.getTime() - this.getSince().getTimeInMillis()); 
		//3 - let's get the number of weeks between those two times
		int weeks = (int)(diff/factor);
		//4 - how many weeks passed since the last executed interval? 
		int aux = weeks % this.getRepeatEach();
		
		//5 - check if it's the first time that the alarm will be triggered
		if(time.after(this.since.getTime())){ 
			/*
			 * the alarm could have been already triggered, because today is after the end date
			 */

			//6 - Wich is the next 'week day' that the alarm should be triggered?
			try{
				nextDay = this.getWeek().getDayAfter(time);
			} catch(NoDaySelectedException ex) {
				//There aren't any week days selected, so we can't continue
				throw new NoDateFoundException();
			}
			
			/*
			 *7 - if aux is equals to 0, then we are at the beginning of the week, so we can just get the next time just by calculating the difference
			 * also, if we are before the time of the day in wich the alarm should be triggered then, the next alarm should be scheduled using the same operations
			 */
			if(nextDay < 0 || aux != 0 || 
					(nextDay == 0 && 
								(time.getHours() > this.since.get(Calendar.HOUR_OF_DAY) || 
									(time.getHours() == this.since.get(Calendar.HOUR_OF_DAY) && time.getMinutes() > this.since.get(Calendar.MINUTE))))){
				weeks = this.getRepeatEach() - aux; 
			}else{
				/*
				 * 7 - elsewhere, we will work with the following week
				 */
				weeks = 0;
			}
			
			//8 - otherwise we are in the same week
			reference = time;
			//9 - The time of the day should be the same of the start date
			reference.setHours(this.since.get(Calendar.HOUR_OF_DAY));
			reference.setMinutes(this.since.get(Calendar.MINUTE));
		}else{
			/*
			 * this is the first time that the alarm should be triggered
			 */

			//6 - Wich is the next 'week day' that the alarm should be triggered?
			try{
				nextDay = this.getWeek().getDayAfter(this.since.getTime());
			} catch(NoDaySelectedException ex) {
				//There aren't any week days selected, so we can't continue
				throw new NoDateFoundException();
			}
			
			//7 - if the nomber of days is > 0 then we are in the same week but, if it's < 0 then we have to move to the next week 
			if(nextDay < 0 ||
					(nextDay == 0 && 
						(time.getHours() > this.since.get(Calendar.HOUR_OF_DAY) || 
								(time.getHours() == this.since.get(Calendar.HOUR_OF_DAY) && time.getMinutes() > this.since.get(Calendar.MINUTE))))){
				// 7 - this week has no more 'active' days, let's move to the next week
				weeks = this.getRepeatEach();  
			}else{ 
				// 7 - we still have work this week
				weeks = 0;
			}
			
			//8 - calculating the reference time
			reference = this.since.getTime();
		}
		//9/10 - getting the next execution time
		return new Date(reference.getTime() + weeks*factor + nextDay*dayFactor);
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of days between alarms
	 * @param time
	 * @return
	 */
	private Date getNextDayByTimeLapse(Date time) {
		//1 - milliseconds in a day
		long factor = 1000*60*60*24;// d’a en ms
		//2 - getting the time lapse between the start date and today
		long diff = (time.getTime() - this.getSince().getTimeInMillis()); 
		//3 - let's get the number of hours between those two times
		int days = (int)(diff/factor);
		//4 - how many days passed since the last executed interval? 
		int aux = days % this.getRepeatEach();// calculamos los d’as del intervalo que ya han pasado 
		/*
		 * 5 - getting the number of days between the start date and the next execution time
		 * by adding to the number of days betwen the start date and the last execution time
		 * to a new whole 'repeat period'  in days
		 */
		days = (days + this.getRepeatEach() - aux);
		//6 - add the calculated hours to the start date
		return new Date(this.getSince().getTimeInMillis() + days*factor);
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of hours between alarms
	 * @param time
	 * @return
	 */
	private Date getNextHourByTimeLapse(Date time) {
		//1 - milliseconds in an hour
		long factor = 1000*60*60; 		
		//2 - getting the time lapse between the start date and today
		long diff = (time.getTime() - this.since.getTimeInMillis());
		//3 - let's get the number of hours between those two times
		int hours = (int)(diff/factor);
		//4 - how many hours have passed since the last executed interval? 
		int aux = hours % this.getRepeatEach();
		/*
		 * 5 - getting the number of hours between the start date and the next execution time
		 * by adding to the number of hours betwen the start date and the last execution time
		 * to a new whole 'repeat period' in hours
		 */
		hours = (hours + this.getRepeatEach() - aux); 
		//6 - add the calculated hours to the start date
		return new Date(this.getSince().getTimeInMillis() + hours*factor);
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
}
