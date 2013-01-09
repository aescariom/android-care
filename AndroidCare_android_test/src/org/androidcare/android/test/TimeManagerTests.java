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
	
	/**
	 * 
	 * @throws Exception
	 */
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

//	/**************************************************************
//	 * 
//	 * 				REPETICIONES MENSUALES HASTA UNA FECHA
//	 * 
//	 **************************************************************/
//	
//	public void testRepeticionMensualHastaFecha_conCambioAno(){
//		Date now = new Date(112, 11, 31, 23, 40);
//		Date end = new Date(120, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.MONTH, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(113, 0, 3, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensualHastaFecha_conFechaInicioPasadaYMinutoAntes(){
//		Date now = new Date(112, 5, 1, 18, 31);
//		Date end = new Date(112, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.MONTH, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 5, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensualHastaFecha_conFechaInicioPasadaYMinutoDespues(){
//		Date now = new Date(112, 5, 1, 18, 39);
//		Date end = new Date(112, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.MONTH, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 6, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensualHastaFecha_conFechaInicioPasadaYHoraAntesCada3(){
//		Date now = new Date(112, 5, 1, 18, 39);
//		Date end = new Date(112, 9, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.MONTH, 3);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 7, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES ANUALES HASTA UNA FECHA
//	 * 
//	 **************************************************************/
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnualHastaFecha_conFechaInicioPasadaYMinutoAntes(){
//		Date now = new Date(114, 4, 1, 18, 31);
//		Date end = new Date(130, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.YEAR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(114, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnualHastaFecha_conFechaInicioPasadaYMinutoDespues(){
//		Date now = new Date(114, 4, 1, 18, 39);
//		Date end = new Date(130, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.YEAR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(115, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnualHastaFecha_conFechaInicioPasadaYHoraAntesCada3(){
//		Date now = new Date(114, 4, 1, 17, 39);
//		Date end = new Date(130, 9, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.YEAR, 3);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(115, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//
//	
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES HORARIAS
//	 * 
//	 **************************************************************/
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionHoraria_conFechaInicio1MinAntes(){
//		Date now = new Date(112, 4, 3, 20, 37);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 20, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionHoraria_conFechaInicioPasada1min(){
//		Date now = new Date(112, 4, 3, 19, 39);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 20, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo
//	 *  en cuenta minutos menores
//	 * @throws Exception
//	 */
//	public void testRepeticionHoraria_conFechaInicioPasadaMinMen(){
//		Date now = new Date(112, 4, 3, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 12, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
//	 * en cuenta minutos mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionHoraria_conFechaInicioPasadaMinMay(){
//		Date now = new Date(112, 4, 3, 12, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 13, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
//	 * en cuenta minutos mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionHoraria_conFechaInicioPasada3Dias(){
//		Date now = new Date(112, 4, 1, 5, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 1, 1, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 3);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 1, 7, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES DIARIAS
//	 * 
//	 **************************************************************/
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionDiaria_conFechaInicioPasada1min(){
//		Date now = new Date(112, 4, 4, 18, 39);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 5, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionDiaria_conFechaInicio1minAntes(){
//		Date now = new Date(112, 4, 5, 18, 37);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 5, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo
//	 *  en cuenta horas menores
//	 * @throws Exception
//	 */
//	public void testRepeticionDiaria_conFechaInicioPasadaMinMen(){
//		Date now = new Date(112, 4, 3, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
//	 * en cuenta horas mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionDiaria_conFechaInicioPasadaMinMay(){
//		Date now = new Date(112, 4, 3, 20, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 4, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo 
//	 * en cuenta minutos mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionDiaria_conFechaInicioPasada3Dias(){
//		Date now = new Date(112, 4, 5, 5, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 1, 1, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 3);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 7, 1, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES SEMANALES
//	 * 
//	 **************************************************************/
//
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conFechaInicioFutura(){
//		Date now = new Date(112, 4, 2, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miércoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 1);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual y que se tiene que ejecutar la 
//	 * semana que viene el lunes
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conFechaInicioPasada(){
//		Date now = new Date(112, 4, 4, 12, 31); // viernes
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 1);
//		Week w = new Week();
//		w.setMonday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}	
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conFechaInicioPasadaMismaSemana(){
//		Date now = new Date(112, 4, 3, 12, 31); // jueves
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 1);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conFechaInicioPasadaLapseDe3(){
//		Date now = new Date(112, 4, 11, 12, 31); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 3);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conFechaInicioPasadaLapseDe1PEroEjecutado(){
//		Date now = new Date(112, 4, 11, 19, 31); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 1);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conInicioIgualFechaActualMinutoMasTarde(){
//		Date now = new Date(112, 4, 11, 18, 39); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 11, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 1);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 18, 18, 38);
//		assertEquals(result, nextWeek);
//	}	
//	
//	/**
//	 *
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanal_conInicioIgualFechaActualMinutoMismo(){
//		Date now = new Date(112, 4, 11, 18, 38); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 11, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.WEEK, 1);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 11, 18, 38);
//		assertEquals(result, nextWeek);
//	}
//
//
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES MENSUALES
//	 * 
//	 **************************************************************/
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensual_conFechaInicioPasadaYMinutoAntes(){
//		Date now = new Date(112, 5, 1, 18, 31);
//		Date end = new Date(112, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.MONTH, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 5, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensual_conFechaInicioPasadaYMinutoDespues(){
//		Date now = new Date(112, 5, 1, 18, 39);
//		Date end = new Date(112, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.MONTH, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 6, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensual_conFechaInicioPasadaYHoraAntesCada3(){
//		Date now = new Date(112, 5, 1, 18, 39);
//		Date end = new Date(112, 9, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.MONTH, 3);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 7, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES ANUALES
//	 * 
//	 **************************************************************/
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnual_conFechaInicioPasadaYMinutoAntes(){
//		Date now = new Date(114, 4, 1, 18, 31);
//		Date end = new Date(130, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.YEAR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(114, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnual_conFechaInicioPasadaYMinutoDespues(){
//		Date now = new Date(114, 4, 1, 18, 39);
//		Date end = new Date(130, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.YEAR, 1);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(115, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnual_conFechaInicioPasadaYHoraAntesCada3(){
//		Date now = new Date(114, 4, 1, 17, 39);
//		Date end = new Date(130, 9, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.NEVER_ENDS, Reminder.YEAR, 3);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(115, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//
//	
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES HORARIAS HASTA UN MAXIMO
//	 * 
//	 **************************************************************/
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionHorariaHastaRepeticiones_conMaximoRebasado(){
//		Date now = new Date(112, 4, 3, 21, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 2);
//
//		try{
//			a.getNextTimeLapse(now);
//			assertTrue(false);
//		}catch (NoDateFoundException ex){
//			assertTrue(true); // ha saltado la excepción que tenía que saltar
//		}
//	}
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionHorariaHastaRepeticiones_conFechaInicio1MinAntes(){
//		Date now = new Date(112, 4, 3, 20, 37);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 20, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasada1min(){
//		Date now = new Date(112, 4, 3, 19, 39);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 20, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo
//	 *  en cuenta minutos menores
//	 * @throws Exception
//	 */
//	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasadaMinMen(){
//		Date now = new Date(112, 4, 3, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 30);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 12, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
//	 * en cuenta minutos mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasadaMinMay(){
//		Date now = new Date(112, 4, 3, 12, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 30);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 13, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
//	 * en cuenta minutos mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasada3Dias(){
//		Date now = new Date(112, 4, 1, 5, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 1, 1, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 3, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 1, 7, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES DIARIAS HASTA UN MAXIMO
//	 * 
//	 **************************************************************/
//
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conMaximoRebasado(){
//		Date now = new Date(112, 4, 6, 21, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 3);
//
//		try{
//			a.getNextTimeLapse(now);
//			assertTrue(false);
//		}catch (NoDateFoundException ex){
//			assertTrue(true); // ha saltado la excepción que tenía que saltar
//		}
//	}
//
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioFutura(){
//		Date now = new Date(112, 4, 3, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		assertEquals(start, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasada1min(){
//		Date now = new Date(112, 4, 4, 18, 39);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 5, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conFechaInicio1minAntes(){
//		Date now = new Date(112, 4, 5, 18, 37);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 3, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 5, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo
//	 *  en cuenta horas menores
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasadaMinMen(){
//		Date now = new Date(112, 4, 3, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 3, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
//	 * en cuenta horas mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasadaMinMay(){
//		Date now = new Date(112, 4, 3, 20, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 2, 18, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 4, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo 
//	 * en cuenta minutos mayores
//	 * @throws Exception
//	 */
//	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasada3Dias(){
//		Date now = new Date(112, 4, 5, 5, 43);
//		Date end = new Date(112, 5, 3, 12, 31);
//		
//		Date start = new Date(112, 4, 1, 1, 38);
//		
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 3, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 7, 1, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES SEMANALES HASTA UN MAXIMO
//	 * 
//	 **************************************************************/
//
//	/**
//	 * fecha de inicio del evento es posterior a la fecha actual
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioFutura(){
//		Date now = new Date(112, 4, 2, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miércoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 10);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextTime);
//	}
//	
//
//	public void testRepeticionSemanalHastaRepeticiones_conIteracionesPasadas(){
//		Date now = new Date(112, 4, 24, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miércoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 3);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		try{
//			a.getNextTimeLapse(now);
//			assertTrue(false);
//		}catch (NoDateFoundException ex){
//			assertTrue(true); // ha saltado la excepción que tenía que saltar
//		}
//	}
//	
//	/**
//	 * fecha de inicio del evento es anterior a la fecha actual y que se tiene que ejecutar la 
//	 * semana que viene el lunes
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasada(){
//		Date now = new Date(112, 4, 4, 12, 31); // viernes
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 10);
//		Week w = new Week();
//		w.setMonday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}	
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasadaMismaSemana(){
//		Date now = new Date(112, 4, 3, 12, 31); // jueves
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 10);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasadaLapseDe3(){
//		Date now = new Date(112, 4, 11, 12, 31); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 3, 10);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasadaLapseDe1PEroEjecutado(){
//		Date now = new Date(112, 4, 11, 19, 31); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 2, 18, 38); // miercoles
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 10);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
//		assertEquals(result, nextWeek);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conInicioIgualFechaActualMinutoMasTarde(){
//		Date now = new Date(112, 4, 11, 18, 39); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 11, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 10);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 18, 18, 38);
//		assertEquals(result, nextWeek);
//	}	
//	
//	/**
//	 *
//	 * @throws Exception
//	 */
//	public void testRepeticionSemanalHastaRepeticiones_conInicioIgualFechaActualMinutoMismo(){
//		Date now = new Date(112, 4, 11, 18, 38); // viernes
//		Date end = new Date(112, 5, 4, 12, 31);
//		Date start = new Date(112, 4, 11, 18, 38);
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.WEEK, 1, 10);
//		Week w = new Week();
//		w.setFriday(true);
//		a.setWeek(w);
//		
//		Date nextWeek = a.getNextTimeLapse(now);
//		Date result = new Date(112, 4, 11, 18, 38);
//		assertEquals(result, nextWeek);
//	}
//
//
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES MENSUALES HASTA UN MAXIMO
//	 * 
//	 **************************************************************/
//	
//
//	public void testRepeticionMensualHastaRepeticiones_conIteracionesPasadas(){
//		Date now = new Date(112, 6, 1, 12, 31);
//		Date end = new Date(113, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.MONTH, 1, 2);
//		
//		try{
//			a.getNextTimeLapse(now);
//			assertTrue(false);
//		}catch (NoDateFoundException ex){
//			assertTrue(true); // ha saltado la excepción que tenía que saltar
//		}
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensualHastaRepeticiones_conFechaInicioPasadaYMinutoAntes(){
//		Date now = new Date(112, 5, 1, 18, 31);
//		Date end = new Date(112, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.MONTH, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 5, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensualHastaRepeticiones_conFechaInicioPasadaYMinutoDespues(){
//		Date now = new Date(112, 5, 1, 18, 39);
//		Date end = new Date(112, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.MONTH, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 6, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionMensualHastaRepeticiones_conFechaInicioPasadaYHoraAntesCada3(){
//		Date now = new Date(112, 5, 1, 18, 39);
//		Date end = new Date(112, 9, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.MONTH, 3, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(112, 7, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**************************************************************
//	 * 
//	 * 				REPETICIONES ANUALES HASTA UN MAXIMO
//	 * 
//	 **************************************************************/
//	
//	public void testRepeticionAnualHastaRepeticiones_conIteracionesPasadas(){
//		Date now = new Date(115, 2, 1, 12, 31);
//		Date end = new Date(112, 5, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.YEAR, 1, 2);
//		
//		try{
//			a.getNextTimeLapse(now);
//			assertTrue(false);
//		}catch (NoDateFoundException ex){
//			assertTrue(true); // ha saltado la excepción que tenía que saltar
//		}
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnualHastaRepeticiones_conFechaInicioPasadaYMinutoAntes(){
//		Date now = new Date(114, 4, 1, 18, 31);
//		Date end = new Date(130, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.YEAR, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(114, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnualHastaRepeticiones_conFechaInicioPasadaYMinutoDespues(){
//		Date now = new Date(114, 4, 1, 18, 39);
//		Date end = new Date(130, 6, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.YEAR, 1, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(115, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
//	
//	/**
//	 * 
//	 * @throws Exception
//	 */
//	public void testRepeticionAnualHastaRepeticiones_conFechaInicioPasadaYHoraAntesCada3(){
//		Date now = new Date(114, 4, 1, 17, 39);
//		Date end = new Date(130, 9, 3, 12, 31);
//		Date start = new Date(112, 4, 1, 18, 38); 
//
//		Reminder a = new Reminder("", "", start, end, true, Reminder.ITERATIONS, Reminder.YEAR, 3, 10);
//		
//		Date nextTime = a.getNextTimeLapse(now);
//		Date result = new Date(115, 4, 1, 18, 38);
//		assertEquals(result, nextTime);
//	}
}
