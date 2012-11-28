package org.androidcare.android.reminders;

import java.util.Calendar;
import java.util.Date;

abstract class TimeManager {

	/**
	 * returns the next date and time in which the alert should be triggered
	 * @param time
	 * @return if there is no future dates
	 */
	public static Date getNextTimeLapse(Reminder reminder, Date time) {
		time.setSeconds(0);
		if(reminder.getSince() != null){
			if(time.before(reminder.getSince().getTime()) && reminder.getRepeatPeriod() != Reminder.WEEK){ 
				//If we are before the start date, let's select the start date as the next execution time
				return reminder.getSince().getTime();
			}else if(reminder.isRepeat()){
				// let's look for the next occurrence
				switch (reminder.getEndType()){
				case Reminder.NEVER_ENDS:
					return TimeManager.getNextTimeLapseWithNoEnd(reminder, time);
				case Reminder.UNTIL_DATE:
					return TimeManager.getNextTimeLapseByDate(reminder, time);
				case Reminder.ITERATIONS:
					return TimeManager.getNextTimeLapseByIterations(reminder, time);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the next execution time based on the limitation of the number of executions
	 * @param time
	 * @return
	 */
	private static Date getNextTimeLapseByIterations(Reminder reminder, Date time) {
		switch(reminder.getRepeatPeriod()){
		case Reminder.HOUR:
			Date nextHour = TimeManager.getNextHourByTimeLapse(reminder, time);
			if(TimeManager.executedTimesUntilByHour(reminder, nextHour) < reminder.getUntilIterations()){
				return nextHour;
			}
			break;
		case Reminder.DAY:
			Date nextDay = TimeManager.getNextDayByTimeLapse(reminder, time);
			if(TimeManager.executedTimesUntilByDay(reminder, nextDay) < reminder.getUntilIterations()){
				return nextDay;
			}
			break;
		case Reminder.WEEK:
			Date nextDayOfTheWeek = TimeManager.getNextWeekDayByTimeLapse(reminder, time);
			if(TimeManager.executedTimesUntilByWeek(reminder, nextDayOfTheWeek) < reminder.getUntilIterations()){
				return nextDayOfTheWeek;
			}
			break;
		case Reminder.MONTH:
			Date nextMonth = TimeManager.getNextMonthByTimeLapse(reminder, time);
			if(TimeManager.executedTimesUntilByMonth(reminder, nextMonth) < reminder.getUntilIterations()){
				return nextMonth;
			}
			break;
		case Reminder.YEAR:
			Date nextYear = TimeManager.getNextYearByTimeLapse(reminder, time);
			if(TimeManager.executedTimesUntilByYear(reminder, nextYear) < reminder.getUntilIterations()){
				return nextYear;
			}
			break;
		}
		return null;
	}

	/**
	 * number of times that the alert has been executed (in hours)
	 * @param time
	 * @return
	 */
	private static int executedTimesUntilByHour(Reminder reminder, Date time) {
		//1 - number of milliseconds in an hour
		long factor = 1000*60*60;

		//2 - calculating the number of days in this lapse of time
		long diff = (time.getTime() - reminder.getSince().getTimeInMillis());
		int hours = (int)(diff/factor);

		return hours / reminder.getRepeatEach();
	}
	
	/**
	 * number of times that the alert has been executed (in days)
	 * @param time
	 * @return
	 */
	private static int executedTimesUntilByDay(Reminder reminder, Date time) {
		//1 - number of milliseconds in a day
		long factor = 1000*60*60*24;

		//2 - calculating the number of days in this lapse of time
		long diff = (time.getTime() - reminder.getSince().getTimeInMillis());
		int days = (int)(diff/factor);

		return days / reminder.getRepeatEach();
	}
	
	/**
	 * number of times that the alert has been executed (in weeks)
	 * @param time
	 * @return
	 */
	private static int executedTimesUntilByWeek(Reminder reminder, Date time) {
		//1 - number of milliseconds in a week
		long factor = 1000*60*60*24*7; 
		
		//2 - calculating the number of weeks in this lapse of time
		long diff = (time.getTime() - reminder.getSince().getTimeInMillis());
		int weeks = (int)(diff/factor);

		return weeks / reminder.getRepeatEach();
	}
	
	/**
	 * number of times that the alert has been executed (in months)
	 * @param time
	 * @return
	 */
	private static int executedTimesUntilByMonth(Reminder reminder, Date time) {
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);

		int diff = (time.getYear() * 12 + time.getMonth()) - (reminder.getSince().getTime().getYear() * 12 + reminder.getSince().getTime().getMonth());

		if(!TimeManager.isBeforeInYear(reminder, cTime)){
			diff++;
		}
		
		return diff / reminder.getRepeatEach();
	}
	
	/**
	 * number of times that the alert has been executed (in years)
	 * @param time
	 * @return
	 */
	private static int executedTimesUntilByYear(Reminder reminder, Date time) {
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);

		int diff = time.getYear() - reminder.getSince().getTime().getYear();

		if(!TimeManager.isBeforeInMonth(reminder, cTime)){
			diff++;
		}
		
		return diff / reminder.getRepeatEach();
	}

	/**
	 * returns the next triggering date
	 * @param time
	 * @return
	 */
	private static Date getNextTimeLapseWithNoEnd(Reminder reminder, Date time) {
		//1 - let's calculate the next ocurrence
		switch(reminder.getRepeatPeriod()){
		case Reminder.HOUR:
			Date nextHour = TimeManager.getNextHourByTimeLapse(reminder, time);
			return nextHour;
		case Reminder.DAY:
			Date nextDay = TimeManager.getNextDayByTimeLapse(reminder, time);
			return nextDay;
		case Reminder.WEEK:
			Date nextDayOfTheWeek = TimeManager.getNextWeekDayByTimeLapse(reminder, time);
			return nextDayOfTheWeek;
		case Reminder.MONTH:
			Date nextMonth = TimeManager.getNextMonthByTimeLapse(reminder, time);
			return nextMonth;
		case Reminder.YEAR:
			Date nextYear = TimeManager.getNextYearByTimeLapse(reminder, time);
			return nextYear;
		}
		return null;
	}

	/**
	 * returns the next triggering date
	 * @param time
	 * @return
	 */
	private static Date getNextTimeLapseByDate(Reminder reminder, Date time) {
		//1 - is this alert still active? or, on the other hand, the end date is in the past
		if(time.after(reminder.getUntilDate().getTime())){
			return null;
		}

		//2 - let's calculate the next ocurrence
		switch(reminder.getRepeatPeriod()){
		case Reminder.HOUR:
			Date nextHour = TimeManager.getNextHourByTimeLapse(reminder, time);
			if(nextHour.before(reminder.getUntilDate().getTime())){
				return nextHour;
			}
			break;
		case Reminder.DAY:
			Date nextDay = TimeManager.getNextDayByTimeLapse(reminder, time);
			if(nextDay.before(reminder.getUntilDate().getTime())){
				return nextDay;
			}
			break;
		case Reminder.WEEK:
			Date nextDayOfTheWeek = TimeManager.getNextWeekDayByTimeLapse(reminder, time);
			if(nextDayOfTheWeek.before(reminder.getUntilDate().getTime())){
				return nextDayOfTheWeek;
			}
			break;
		case Reminder.MONTH:
			Date nextMonth = TimeManager.getNextMonthByTimeLapse(reminder, time);
			if(nextMonth.before(reminder.getUntilDate().getTime())){
				return nextMonth;
			}
			break;
		case Reminder.YEAR:
			Date nextYear = TimeManager.getNextYearByTimeLapse(reminder, time);
			if(nextYear.before(reminder.getUntilDate().getTime())){
				return nextYear;
			}
			break;
		}
		return null;
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of months or days of the week between alarms
	 * @param time
	 * @return
	 */
	private static Date getNextYearByTimeLapse(Reminder reminder, Date time) {
		int year;

		//1 - calculating the reference times
		Date auxDate = new Date(reminder.getSince().getTimeInMillis());
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);
		
		//2 - get the difference (in months) between today and the start date
		int diff = cTime.get(Calendar.YEAR) - reminder.getSince().get(Calendar.YEAR);
		//3 - how many weeks passed since the last executed interval? 
		int aux = diff % reminder.getRepeatEach(); 
		
		if(aux == 0){
			//4 - this is an active year, let's check if we are before or after the trigger moment
			if(TimeManager.isBeforeInYear(reminder, cTime)){
				year = diff;
			}else{
				year = (diff + reminder.getRepeatEach() - aux);
			}
		}else{
			//4 - this is not an active year, let's jump to the next one
			year = (diff + reminder.getRepeatEach() - aux);
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
	private static boolean isBeforeInYear(Reminder reminder, Calendar cTime) {
		return cTime.get(Calendar.MONTH) < reminder.getSince().get(Calendar.MONTH) || 
				(cTime.get(Calendar.MONTH) == reminder.getSince().get(Calendar.MONTH) &&
					TimeManager.isBeforeInMonth(reminder, cTime));
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of months or days of the week between alarms
	 * @param time
	 * @return
	 */
	private static Date getNextMonthByTimeLapse(Reminder reminder, Date time) {
		int months, monthsAux, year;
		
		//1 - calculating the reference times
		Date auxDate = new Date(reminder.getSince().getTimeInMillis());
		Calendar cTime = Calendar.getInstance();
		cTime.setTime(time);

		//2 - get the difference (in months) between today and the start date
		int diff = (cTime.get(Calendar.YEAR) * 12 + cTime.get(Calendar.MONTH)) - 
				(reminder.getSince().get(Calendar.YEAR) * 12 + reminder.getSince().get(Calendar.MONTH));
		//3 - how many weeks passed since the last executed interval? 
		int aux = diff % reminder.getRepeatEach(); 
		
		if(aux == 0){
			//4 - this is an active month, let's check if we are before or after the trigger moment
			if(TimeManager.isBeforeInMonth(reminder, cTime)){
				months = diff;
			}else{
				months = (diff + reminder.getRepeatEach() - aux);
			}
		}else{
			//4 - this month is not active, let's jump to the next one
			months = (diff + reminder.getRepeatEach() - aux);
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
	private static boolean isBeforeInMonth(Reminder reminder, Calendar cTime) {
		return cTime.get(Calendar.DAY_OF_MONTH) < reminder.getSince().get(Calendar.DAY_OF_MONTH) ||
				(cTime.get(Calendar.DAY_OF_MONTH) == reminder.getSince().get(Calendar.DAY_OF_MONTH)  && 
					(cTime.get(Calendar.HOUR_OF_DAY) < reminder.getSince().get(Calendar.HOUR_OF_DAY) || 
						(cTime.get(Calendar.HOUR_OF_DAY) == reminder.getSince().get(Calendar.HOUR_OF_DAY) && 
								cTime.get(Calendar.MINUTE) < reminder.getSince().get(Calendar.MINUTE) )));
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of weeks or days of the week between alarms
	 * @param time
	 * @return
	 */
	private static Date getNextWeekDayByTimeLapse(Reminder reminder, Date time) {
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
		long diff = (time.getTime() - reminder.getSince().getTimeInMillis()); 
		//3 - let's get the number of weeks between those two times
		int weeks = (int)(diff/factor);
		//4 - how many weeks passed since the last executed interval? 
		int aux = weeks % reminder.getRepeatEach();
		
		//5 - check if it's the first time that the alarm will be triggered
		if(time.after(reminder.getSince().getTime())){ 
			/*
			 * the alarm could have been already triggered, because today is after the end date
			 */

			//6 - Wich is the next 'week day' that the alarm should be triggered?
			nextDay = reminder.getWeek().getDayAfterInWeek(time);
			
			/*
			 *7 - if aux is equals to 0, then we are at the beginning of the week, so we can just get the next time just by calculating the difference
			 * also, if we are before the time of the day in wich the alarm should be triggered then, the next alarm should be scheduled using the same operations
			 */
			if(nextDay < 0 || aux != 0 || 
					(nextDay == 0 && 
								(time.getHours() > reminder.getSince().get(Calendar.HOUR_OF_DAY) || 
									(time.getHours() == reminder.getSince().get(Calendar.HOUR_OF_DAY) && 
										time.getMinutes() > reminder.getSince().get(Calendar.MINUTE))))){
				weeks = reminder.getRepeatEach() - aux; 
			}else{
				/*
				 * 7 - elsewhere, we will work with the following week
				 */
				weeks = 0;
			}
			
			//8 - otherwise we are in the same week
			reference = time;
			//9 - The time of the day should be the same of the start date
			reference.setHours(reminder.getSince().get(Calendar.HOUR_OF_DAY));
			reference.setMinutes(reminder.getSince().get(Calendar.MINUTE));
		}else{
			/*
			 * this is the first time that the alarm should be triggered
			 */

			//6 - Wich is the next 'week day' that the alarm should be triggered?
			nextDay = reminder.getWeek().getDayAfterInWeek(reminder.getSince().getTime());
			
			//7 - if the nomber of days is > 0 then we are in the same week but, if it's < 0 then we have to move to the next week 
			if(nextDay < 0 ||
					(nextDay == 0 && 
						(time.getHours() > reminder.getSince().get(Calendar.HOUR_OF_DAY) || 
								(time.getHours() == reminder.getSince().get(Calendar.HOUR_OF_DAY) && 
									time.getMinutes() > reminder.getSince().get(Calendar.MINUTE))))){
				// 7 - this week has no more 'active' days, let's move to the next week
				weeks = reminder.getRepeatEach();  
			}else{ 
				// 7 - we still have work this week
				weeks = 0;
			}
			
			//8 - calculating the reference time
			reference = reminder.getSince().getTime();
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
	private static Date getNextDayByTimeLapse(Reminder reminder, Date time) {
		//1 - milliseconds in a day
		long factor = 1000*60*60*24;// d’a en ms
		//2 - getting the time lapse between the start date and today
		long diff = (time.getTime() - reminder.getSince().getTimeInMillis()); 
		//3 - let's get the number of hours between those two times
		int days = (int)(diff/factor);
		//4 - how many days passed since the last executed interval? 
		int aux = days % reminder.getRepeatEach();// calculamos los d’as del intervalo que ya han pasado 
		/*
		 * 5 - getting the number of days between the start date and the next execution time
		 * by adding to the number of days betwen the start date and the last execution time
		 * to a new whole 'repeat period'  in days
		 */
		days = (days + reminder.getRepeatEach() - aux);
		//6 - add the calculated hours to the start date
		return new Date(reminder.getSince().getTimeInMillis() + days*factor);
	}

	/**
	 * Having a reference time, this method will return the next time the alert should be triggered
	 * based on the number of hours between alarms
	 * @param time
	 * @return
	 */
	private static Date getNextHourByTimeLapse(Reminder reminder, Date time) {
		//1 - milliseconds in an hour
		long factor = 1000*60*60; 		
		//2 - getting the time lapse between the start date and today
		long diff = (time.getTime() - reminder.getSince().getTimeInMillis());
		//3 - let's get the number of hours between those two times
		int hours = (int)(diff/factor);
		//4 - how many hours have passed since the last executed interval? 
		int aux = hours % reminder.getRepeatEach();
		/*
		 * 5 - getting the number of hours between the start date and the next execution time
		 * by adding to the number of hours betwen the start date and the last execution time
		 * to a new whole 'repeat period' in hours
		 */
		hours = (hours + reminder.getRepeatEach() - aux); 
		//6 - add the calculated hours to the start date
		return new Date(reminder.getSince().getTimeInMillis() + hours*factor);
	}
}
