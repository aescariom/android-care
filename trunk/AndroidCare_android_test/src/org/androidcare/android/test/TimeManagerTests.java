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

package org.androidcare.android.test;

import java.util.Date;

import org.androidcare.android.reminders.DaysOfWeekInWhichShouldTrigger;
import org.androidcare.android.reminders.Reminder;

import junit.framework.TestCase;

public class TimeManagerTests extends TestCase {

	/**************************************************************
	 **************************************************************
	 * 
	 * 				Reminders that shouldn't be scheduled
	 * 
	 **************************************************************
	 **************************************************************/

	public void test_pastDeadline(){
		Date now = new Date(112, 4, 3, 12, 43);
		
		Date start = new Date(112, 4, 2, 18, 38);
		Date end = new Date(112, 4, 3, 12, 31);
		
		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		Date nextTime = a.getNextTimeLapse(now);
		assertNull(nextTime);
	}
	
	public void test_doNotRepeat_pastDeadline(){
		Date now = new Date(112, 4, 3, 12, 43);
		
		Date start = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", start);
		Date nextTime = a.getNextTimeLapse(now);
		assertNull(nextTime);
	}
	
	public void test_doNotRepeat_beforeDeadline(){
		Date now = new Date(112, 4, 3, 12, 43);
		
		Date start = new Date(112, 4, 4, 18, 38);
		
		Reminder a = new Reminder("", "", start);
		Date nextTime = a.getNextTimeLapse(now);
		assertEquals(start, nextTime);
	}
	
	/**************************************************************
	 **************************************************************
	 * 
	 * 				Reminders that should be scheduled 
	 * 
	 **************************************************************
	 **************************************************************/
	

	public void test_beforeStart(){
		Date now = new Date(112, 4, 3, 12, 31);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		assertEquals(start, nextTime);
	}
	
	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_HOUR
	 * 			UNTIL:			END_TYPE_UNTIL_DATE
	 **************************************************************/

	public void test_eachOneHour_untilDate_afterStartDate(){
		Date now = new Date(112, 4, 3, 20, 37);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_eachFiveHours_untilDate_afterStartDate(){
		Date now = new Date(112, 4, 3, 20, 37);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 5);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 23, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_eachOneHour_untilDate_afterStartDate_anotherDay(){
		Date now = new Date(112, 4, 3, 23, 40);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 4, 0, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_eachOneHour_untilDate_afterStartDate_triggerTime(){
		Date now = new Date(112, 4, 3, 23, 38);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 24, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_eachOneHour_untilDate_afterStartDate_oneMinuteAfter(){
		Date now = new Date(112, 4, 3, 19, 39);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result, nextTime);
	}
	
	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_DAY
	 * 			UNTIL:			END_TYPE_UNTIL_DATE
	 **************************************************************/

	public void test_repeatEachDay_untilDate(){
		Date now = new Date(112, 4, 4, 18, 39);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_repeatEachThreeDays_untilDate(){
		Date now = new Date(112, 4, 4, 18, 39);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 6, 18, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_repeatEachDay_untilDate_nextMonth(){
		Date now = new Date(112, 4, 31, 23, 40);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_WEEK
	 * 			UNTIL:			END_TYPE_UNTIL_DATE
	 **************************************************************/
	
	public void test_repeatEachWeek_untilDate_beforeStart(){
		Date now = new Date(112, 4, 2, 12, 31);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miércoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextTime);
	}
	
	public void test_repeatEachWeek_untilDate(){
		Date now = new Date(112, 4, 4, 12, 31); // viernes
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, true, false, false, false, false, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}	

	public void test_repeatEachWeek_untilDate_nextIterationNextWeek(){
		Date now = new Date(112, 4, 3, 12, 31); // jueves
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEachThreeWeeks_untilDate_nextIterationNextWeek(){
		Date now = new Date(112, 4, 11, 12, 31); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 3);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEachWeek_untilDate_afterStart_startWeekNotTrigger(){
		Date now = new Date(112, 4, 11, 19, 31); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}
	
	public void test_repeatEachWeek_untilDate_afterStart_hasBeenExecutedRightNow(){
		Date now = new Date(112, 4, 11, 18, 38); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 11, 18, 38);
		assertEquals(result, nextWeek);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_MONTH
	 * 			UNTIL:			END_TYPE_UNTIL_DATE
	 **************************************************************/
	
	public void test_repeatEachMonth_untilDate_afterStart_changingYear(){
		Date now = new Date(112, 11, 31, 23, 40);
		Date end = new Date(120, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(113, 0, 3, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEachMonth_untilDate_afterStart(){
		Date now = new Date(112, 5, 1, 18, 31);
		Date end = new Date(112, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEachMonth_untilDate_afterStart_minuteAfter(){
		Date now = new Date(112, 5, 1, 18, 39);
		Date end = new Date(112, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 6, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Month_untilDate_afterStart_minuteAfter(){
		Date now = new Date(112, 5, 1, 18, 39);
		Date end = new Date(112, 9, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 7, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_YEAR
	 * 			UNTIL:			END_TYPE_UNTIL_DATE
	 **************************************************************/
	
	public void test_repeatEachYear_untilDate_afterStart_minuteBeforeTrigger(){
		Date now = new Date(114, 4, 1, 18, 31);
		Date end = new Date(130, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(114, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEachYear_untilDate_afterStart_minuteAfterTrigger(){
		Date now = new Date(114, 4, 1, 18, 39);
		Date end = new Date(130, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Year_untilDate_afterStart(){
		Date now = new Date(114, 4, 1, 17, 39);
		Date end = new Date(130, 9, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_HOUR
	 * 			UNTIL:			END_TYPE_NEVER_ENDS
	 **************************************************************/

	public void test_repeatEachHour_neverEnds(){
		Date now = new Date(112, 4, 3, 20, 37);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Hour_neverEnds(){
		Date now = new Date(112, 4, 1, 5, 43);
		Date end = new Date(112, 5, 3, 12, 31);
		
		Date start = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 1, 7, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_DAY
	 * 			UNTIL:			END_TYPE_NEVER_ENDS
	 **************************************************************/

	public void test_repeatEachDay_neverEnds(){
		Date now = new Date(112, 4, 4, 18, 39);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Day_neverEnds(){
		Date now = new Date(112, 4, 5, 5, 43);
		Date end = new Date(112, 5, 3, 12, 31);
		
		Date start = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 7, 1, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_WEEK
	 * 			UNTIL:			END_TYPE_NEVER_ENDS
	 **************************************************************/

	public void test_repeatEachWeek_neverEnds(){
		Date now = new Date(112, 4, 4, 12, 31); // viernes
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, true, false, false, false, false, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEachWeek_neverEnds_sameWeek_lastDayAlreadyTriggered(){
		Date now = new Date(112, 4, 3, 12, 31); // jueves
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEach3Week_neverEnds(){
		Date now = new Date(112, 4, 11, 12, 31); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 3);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_MONTH
	 * 			UNTIL:			END_TYPE_NEVER_ENDS
	 **************************************************************/

	public void test_repeatEachMonth_neverEnds(){
		Date now = new Date(112, 5, 1, 18, 31);
		Date end = new Date(112, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Month_neverEnds(){
		Date now = new Date(112, 5, 1, 18, 39);
		Date end = new Date(112, 9, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_MONTH, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 7, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_YEAR
	 * 			UNTIL:			END_TYPE_NEVER_ENDS
	 **************************************************************/

	public void test_repeatEachYear_neverEnds(){
		Date now = new Date(114, 4, 1, 18, 31);
		Date end = new Date(130, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(114, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Year_neverEnds(){
		Date now = new Date(114, 4, 1, 17, 39);
		Date end = new Date(130, 9, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_YEAR, 3);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_HOUR
	 * 			UNTIL:			END_TYPE_ITERATIONS
	 **************************************************************/

	public void test_repeatEachHour_iterations_noSchedule(){
		Date now = new Date(112, 4, 3, 21, 31);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 2);
		
		Date result = a.getNextTimeLapse(now);
		assertNull(result);
	}

	public void test_repeatEachHour_iterations_secondSchedule(){
		Date now = new Date(112, 4, 3, 19, 39);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEachHour_iterations(){
		Date now = new Date(112, 4, 3, 12, 43);
		Date end = new Date(112, 5, 3, 12, 31);
		
		Date start = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 30);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 3, 13, 38);
		assertEquals(result, nextTime);
	}
	
	public void test_repeatEach3Hour_iterations(){
		Date now = new Date(112, 4, 1, 5, 43);
		Date end = new Date(112, 5, 3, 12, 31);
		
		Date start = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 3, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 1, 7, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_DAY
	 * 			UNTIL:			END_TYPE_ITERATIONS
	 **************************************************************/

	public void test_repeatEachDay_iterations_noSchedule(){
		Date now = new Date(112, 4, 6, 21, 31);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 3);

		Date result = a.getNextTimeLapse(now);
		assertNull(result);
	}
	
	public void test_repeatEachDay_iterations_noSchedule_secondSchedule(){
		Date now = new Date(112, 4, 4, 18, 39);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Day_iterations(){
		Date now = new Date(112, 4, 5, 5, 43);
		Date end = new Date(112, 5, 3, 12, 31);
		
		Date start = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 3, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 7, 1, 38);
		assertEquals(result, nextTime);
	}
	
	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_WEEK
	 * 			UNTIL:			END_TYPE_ITERATIONS
	 **************************************************************/	

	public void test_repeatEachWeek_iterations_noSchedule(){
		Date now = new Date(112, 4, 24, 12, 31);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miércoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 3);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		assertNull(a.getNextTimeLapse(now));
	}

	public void test_repeatEachWeek_iterations_afterStartDate(){
		Date now = new Date(112, 4, 4, 12, 31); // viernes
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] days = {false, true, false, false, false, false, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}	

	public void test_repeatEachWeek_iterations_afterStartDate_sameWeekLastSchedule(){
		Date now = new Date(112, 4, 3, 12, 31); // jueves
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEach3Week_iterations_afterStartDate(){
		Date now = new Date(112, 4, 11, 12, 31); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 3, 10);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEachWeek_iterations_alreadyExecutedThisWeek(){
		Date now = new Date(112, 4, 11, 19, 31); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result, nextWeek);
	}

	public void test_repeatEachWeek_iterations_sameMinute(){
		Date now = new Date(112, 4, 11, 18, 38); // viernes
		Date end = new Date(112, 5, 4, 12, 31);
		Date start = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] days = {false, false, false, false, false, true, false};
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(days);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(now);
		Date result = new Date(112, 4, 11, 18, 38);
		assertEquals(result, nextWeek);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_MONTH
	 * 			UNTIL:			END_TYPE_ITERATIONS
	 **************************************************************/

	public void test_repeatEachMonth_iterations_noSchedule(){
		Date now = new Date(112, 6, 1, 12, 31);
		Date end = new Date(113, 5, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 1, 2);
		
		assertNull(a.getNextTimeLapse(now));
	}

	public void test_repeatEachMonth_iterations_minuteAfterTriggered(){
		Date now = new Date(112, 5, 1, 18, 39);
		Date end = new Date(112, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 1, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 6, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Month_iterations(){
		Date now = new Date(112, 5, 1, 18, 39);
		Date end = new Date(112, 9, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 3, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(112, 7, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	/**************************************************************
	 * 			REPEAT EACH: 	REPEAT_PERIOD_YEAR
	 * 			UNTIL:			END_TYPE_ITERATIONS
	 **************************************************************/
	
	public void test_repeatEachYear_iterations_noSchedule(){
		Date now = new Date(115, 2, 1, 12, 31);
		Date end = new Date(112, 5, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 1, 2);
		
		assertNull(a.getNextTimeLapse(now));
	}

	public void test_repeatEachYear_iterations_minuteAfterTriggered(){
		Date now = new Date(114, 4, 1, 18, 39);
		Date end = new Date(130, 6, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 1, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}

	public void test_repeatEach3Year_iterations(){
		Date now = new Date(114, 4, 1, 17, 39);
		Date end = new Date(130, 9, 3, 12, 31);
		Date start = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 3, 10);
		
		Date nextTime = a.getNextTimeLapse(now);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result, nextTime);
	}
}
