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

package org.androidCare.android.reminders.test;

import java.util.Date;

import org.androidcare.android.reminders.AndroidCareDateFormatException;
import org.androidcare.android.reminders.DaysOfWeekInWhichShouldTrigger;
import org.androidcare.android.reminders.Reminder;


import junit.framework.TestCase;

public class RemaindertTest extends TestCase {


	/**************************************************************
	 * 
	 * 				FECHA DE FINALIZACIÓN TERMINADA
	 * 
	 **************************************************************/

	/**
	 * fecha de fin del evento ya ha pasado
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaFinPasada() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 43);
		Date endTime = new Date(112, 4, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		try{
			a.getNextTimeLapse(actualTime);
		}catch (AndroidCareDateFormatException ex){
			assertTrue(true); // ha saltado la excepción que tenía que saltar
		}
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES HORARIAS HASTA UNA FECHA
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaInicio1MinAntes() throws Exception{
		Date actualTime = new Date(112, 4, 3, 20, 37);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conCambioDia() throws Exception{
		Date actualTime = new Date(112, 4, 3, 23, 40);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 0, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaInicioPasada1min() throws Exception{
		Date actualTime = new Date(112, 4, 3, 19, 39);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo
	 *  en cuenta minutos menores
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaInicioPasadaMinMen() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 12, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaInicioPasadaMinMay() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 13, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaFecha_conFechaInicioPasada3Dias() throws Exception{
		Date actualTime = new Date(112, 4, 1, 5, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_HOUR, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 1, 7, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES DIARIAS HASTA UNA FECHA
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaFecha_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaFecha_conFechaInicioPasada1min() throws Exception{
		Date actualTime = new Date(112, 4, 4, 18, 39);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	public void testRepeticionHorariaHastaFecha_conCambioMes() throws Exception{
		Date actualTime = new Date(112, 4, 31, 23, 40);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaFecha_conFechaInicio1minAntes() throws Exception{
		Date actualTime = new Date(112, 4, 5, 18, 37);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo
	 *  en cuenta horas menores
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaFecha_conFechaInicioPasadaMinMen() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta horas mayores
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaFecha_conFechaInicioPasadaMinMay() throws Exception{
		Date actualTime = new Date(112, 4, 3, 20, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaFecha_conFechaInicioPasada3Dias() throws Exception{
		Date actualTime = new Date(112, 4, 5, 5, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_DAY, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 7, 1, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES SEMANALES HASTA UNA FECHA
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 2, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miércoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
		DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
		a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual y que se tiene que ejecutar la 
	 * semana que viene el lunes
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conFechaInicioPasada() throws Exception{
		Date actualTime = new Date(112, 4, 4, 12, 31); // viernes
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { true, false, false, false, false, false, false };//on mondays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conFechaInicioPasadaMismaSemana() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31); // jueves
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conFechaInicioPasadaLapseDe3() throws Exception{
		Date actualTime = new Date(112, 4, 11, 12, 31); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 3);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conFechaInicioPasadaLapseDe1PEroEjecutado() throws Exception{
		Date actualTime = new Date(112, 4, 11, 19, 31); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conInicioIgualFechaActualMinutoMasTarde() throws Exception{
		Date actualTime = new Date(112, 4, 11, 18, 39); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 18, 18, 38);
		assertEquals(result.getTime(), nextWeek.getTime());
	}	
	
	/**
	 *
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaFecha_conInicioIgualFechaActualMinutoMismo() throws Exception{
		Date actualTime = new Date(112, 4, 11, 18, 38); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 11, 18, 38);
		assertEquals(result.getTime(), nextWeek.getTime());
	}


	/**************************************************************
	 * 
	 * 				REPETICIONES MENSUALES HASTA UNA FECHA
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaFecha_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	public void testRepeticionMensualHastaFecha_conCambioAno() throws Exception{
		Date actualTime = new Date(112, 11, 31, 23, 40);
		Date endTime = new Date(120, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(113, 0, 3, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaFecha_conFechaInicioPasadaYMinutoAntes() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 31);
		Date endTime = new Date(112, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaFecha_conFechaInicioPasadaYMinutoDespues() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 39);
		Date endTime = new Date(112, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 6, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaFecha_conFechaInicioPasadaYHoraAntesCada3() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 39);
		Date endTime = new Date(112, 9, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_MONTH, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 7, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**************************************************************
	 * 
	 * 				REPETICIONES ANUALES HASTA UNA FECHA
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaFecha_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaFecha_conFechaInicioPasadaYMinutoAntes() throws Exception{
		Date actualTime = new Date(114, 4, 1, 18, 31);
		Date endTime = new Date(130, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(114, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaFecha_conFechaInicioPasadaYMinutoDespues() throws Exception{
		Date actualTime = new Date(114, 4, 1, 18, 39);
		Date endTime = new Date(130, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaFecha_conFechaInicioPasadaYHoraAntesCada3() throws Exception{
		Date actualTime = new Date(114, 4, 1, 17, 39);
		Date endTime = new Date(130, 9, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_UNTIL_DATE, Reminder.REPEAT_PERIOD_YEAR, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	

	
	/**************************************************************
	 * 
	 * 				REPETICIONES HORARIAS
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHoraria_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHoraria_conFechaInicio1MinAntes() throws Exception{
		Date actualTime = new Date(112, 4, 3, 20, 37);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHoraria_conFechaInicioPasada1min() throws Exception{
		Date actualTime = new Date(112, 4, 3, 19, 39);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo
	 *  en cuenta minutos menores
	 * @throws Exception
	 */
	public void testRepeticionHoraria_conFechaInicioPasadaMinMen() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 12, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionHoraria_conFechaInicioPasadaMinMay() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 13, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionHoraria_conFechaInicioPasada3Dias() throws Exception{
		Date actualTime = new Date(112, 4, 1, 5, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_HOUR, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 1, 7, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES DIARIAS
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiaria_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiaria_conFechaInicioPasada1min() throws Exception{
		Date actualTime = new Date(112, 4, 4, 18, 39);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiaria_conFechaInicio1minAntes() throws Exception{
		Date actualTime = new Date(112, 4, 5, 18, 37);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo
	 *  en cuenta horas menores
	 * @throws Exception
	 */
	public void testRepeticionDiaria_conFechaInicioPasadaMinMen() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta horas mayores
	 * @throws Exception
	 */
	public void testRepeticionDiaria_conFechaInicioPasadaMinMay() throws Exception{
		Date actualTime = new Date(112, 4, 3, 20, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionDiaria_conFechaInicioPasada3Dias() throws Exception{
		Date actualTime = new Date(112, 4, 5, 5, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_DAY, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 7, 1, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES SEMANALES
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 2, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miércoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual y que se tiene que ejecutar la 
	 * semana que viene el lunes
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conFechaInicioPasada() throws Exception{
		Date actualTime = new Date(112, 4, 4, 12, 31); // viernes
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { true, false, false, false, false, false, false };//on mondays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conFechaInicioPasadaMismaSemana() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31); // jueves
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conFechaInicioPasadaLapseDe3() throws Exception{
		Date actualTime = new Date(112, 4, 11, 12, 31); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 3);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conFechaInicioPasadaLapseDe1PEroEjecutado() throws Exception{
		Date actualTime = new Date(112, 4, 11, 19, 31); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conInicioIgualFechaActualMinutoMasTarde() throws Exception{
		Date actualTime = new Date(112, 4, 11, 18, 39); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 18, 18, 38);
		assertEquals(result.getTime(), nextWeek.getTime());
	}	
	
	/**
	 *
	 * @throws Exception
	 */
	public void testRepeticionSemanal_conInicioIgualFechaActualMinutoMismo() throws Exception{
		Date actualTime = new Date(112, 4, 11, 18, 38); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_WEEK, 1);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 11, 18, 38);
		assertEquals(result.getTime(), nextWeek.getTime());
	}


	/**************************************************************
	 * 
	 * 				REPETICIONES MENSUALES
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionMensual_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensual_conFechaInicioPasadaYMinutoAntes() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 31);
		Date endTime = new Date(112, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensual_conFechaInicioPasadaYMinutoDespues() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 39);
		Date endTime = new Date(112, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_MONTH, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 6, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensual_conFechaInicioPasadaYHoraAntesCada3() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 39);
		Date endTime = new Date(112, 9, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_MONTH, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 7, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**************************************************************
	 * 
	 * 				REPETICIONES ANUALES
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionAnual_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnual_conFechaInicioPasadaYMinutoAntes() throws Exception{
		Date actualTime = new Date(114, 4, 1, 18, 31);
		Date endTime = new Date(130, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(114, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnual_conFechaInicioPasadaYMinutoDespues() throws Exception{
		Date actualTime = new Date(114, 4, 1, 18, 39);
		Date endTime = new Date(130, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_YEAR, 1);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnual_conFechaInicioPasadaYHoraAntesCada3() throws Exception{
		Date actualTime = new Date(114, 4, 1, 17, 39);
		Date endTime = new Date(130, 9, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_NEVER_ENDS, Reminder.REPEAT_PERIOD_YEAR, 3);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	

	
	/**************************************************************
	 * 
	 * 				REPETICIONES HORARIAS HASTA UN MAXIMO
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conMaximoRebasado() throws Exception{
		Date actualTime = new Date(112, 4, 3, 21, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 2);

		try{
			a.getNextTimeLapse(actualTime);
			assertTrue(false);
		}catch (AndroidCareDateFormatException ex){
			assertTrue(true); // ha saltado la excepción que tenía que saltar
		}
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conFechaInicio1MinAntes() throws Exception{
		Date actualTime = new Date(112, 4, 3, 20, 37);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasada1min() throws Exception{
		Date actualTime = new Date(112, 4, 3, 19, 39);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 20, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo
	 *  en cuenta minutos menores
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasadaMinMen() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 30);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 12, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasadaMinMay() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 1, 30);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 13, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionHorariaHastaRepeticiones_conFechaInicioPasada3Dias() throws Exception{
		Date actualTime = new Date(112, 4, 1, 5, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_HOUR, 3, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 1, 7, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES DIARIAS HASTA UN MAXIMO
	 * 
	 **************************************************************/

	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conMaximoRebasado() throws Exception{
		Date actualTime = new Date(112, 4, 6, 21, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 3);

		try{
			a.getNextTimeLapse(actualTime);
			assertTrue(false);
		}catch (AndroidCareDateFormatException ex){
			assertTrue(true); // ha saltado la excepción que tenía que saltar
		}
	}

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasada1min() throws Exception{
		Date actualTime = new Date(112, 4, 4, 18, 39);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conFechaInicio1minAntes() throws Exception{
		Date actualTime = new Date(112, 4, 5, 18, 37);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 3, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 5, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo
	 *  en cuenta horas menores
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasadaMinMen() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 3, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de hora en hora teniendo 
	 * en cuenta horas mayores
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasadaMinMay() throws Exception{
		Date actualTime = new Date(112, 4, 3, 20, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 2, 18, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual con repeticiones de día en día teniendo 
	 * en cuenta minutos mayores
	 * @throws Exception
	 */
	public void testRepeticionDiariaHastaRepeticiones_conFechaInicioPasada3Dias() throws Exception{
		Date actualTime = new Date(112, 4, 5, 5, 43);
		Date endTime = new Date(112, 5, 3, 12, 31);
		
		Date startTime = new Date(112, 4, 1, 1, 38);
		
		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_DAY, 3, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 7, 1, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**************************************************************
	 * 
	 * 				REPETICIONES SEMANALES HASTA UN MAXIMO
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 4, 2, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miércoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);;
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextHour.getTime());
	}
	

	public void testRepeticionSemanalHastaRepeticiones_conIteracionesPasadas() throws Exception{
		Date actualTime = new Date(112, 4, 24, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miércoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 3);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		try{
			a.getNextTimeLapse(actualTime);
			assertTrue(false);
		}catch (AndroidCareDateFormatException ex){
			assertTrue(true); // ha saltado la excepción que tenía que saltar
		}
	}
	
	/**
	 * fecha de inicio del evento es anterior a la fecha actual y que se tiene que ejecutar la 
	 * semana que viene el lunes
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasada() throws Exception{
		Date actualTime = new Date(112, 4, 4, 12, 31); // viernes
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] dayIsSelected = { true, false, false, false, true, false, false };//on mondays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 7, 18, 38); // es lunes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasadaMismaSemana() throws Exception{
		Date actualTime = new Date(112, 4, 3, 12, 31); // jueves
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 4, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasadaLapseDe3() throws Exception{
		Date actualTime = new Date(112, 4, 11, 12, 31); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 3, 10);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 25, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conFechaInicioPasadaLapseDe1PEroEjecutado() throws Exception{
		Date actualTime = new Date(112, 4, 11, 19, 31); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 2, 18, 38); // miercoles

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 18, 18, 38); // es viernes, debería ejecutarse este día
		assertEquals(result.getTime(), nextWeek.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conInicioIgualFechaActualMinutoMasTarde() throws Exception{
		Date actualTime = new Date(112, 4, 11, 18, 39); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 18, 18, 38);
		assertEquals(result.getTime(), nextWeek.getTime());
	}	
	
	/**
	 *
	 * @throws Exception
	 */
	public void testRepeticionSemanalHastaRepeticiones_conInicioIgualFechaActualMinutoMismo() throws Exception{
		Date actualTime = new Date(112, 4, 11, 18, 38); // viernes
		Date endTime = new Date(112, 5, 4, 12, 31);
		Date startTime = new Date(112, 4, 11, 18, 38);

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_WEEK, 1, 10);
		boolean[] dayIsSelected = { false, false, false, false, true, false, false };//on fridays
        DaysOfWeekInWhichShouldTrigger w = new DaysOfWeekInWhichShouldTrigger(dayIsSelected);
        a.setDaysOfWeekInWhichShouldTrigger(w);
		
		Date nextWeek = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 4, 11, 18, 38);
		assertEquals(result.getTime(), nextWeek.getTime());
	}


	/**************************************************************
	 * 
	 * 				REPETICIONES MENSUALES HASTA UN MAXIMO
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaRepeticiones_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	

	public void testRepeticionMensualHastaRepeticiones_conIteracionesPasadas() throws Exception{
		Date actualTime = new Date(112, 6, 1, 12, 31);
		Date endTime = new Date(113, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 1, 2);
		
		try{
			a.getNextTimeLapse(actualTime);
			assertTrue(false);
		}catch (AndroidCareDateFormatException ex){
			assertTrue(true); // ha saltado la excepción que tenía que saltar
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaRepeticiones_conFechaInicioPasadaYMinutoAntes() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 31);
		Date endTime = new Date(112, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 5, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaRepeticiones_conFechaInicioPasadaYMinutoDespues() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 39);
		Date endTime = new Date(112, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 6, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionMensualHastaRepeticiones_conFechaInicioPasadaYHoraAntesCada3() throws Exception{
		Date actualTime = new Date(112, 5, 1, 18, 39);
		Date endTime = new Date(112, 9, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_MONTH, 3, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(112, 7, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**************************************************************
	 * 
	 * 				REPETICIONES ANUALES HASTA UN MAXIMO
	 * 
	 **************************************************************/

	/**
	 * fecha de inicio del evento es posterior a la fecha actual
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaRepeticiones_conFechaInicioFutura() throws Exception{
		Date actualTime = new Date(112, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		assertEquals(startTime.getTime(), nextHour.getTime());
	}
	
	public void testRepeticionAnualHastaRepeticiones_conIteracionesPasadas() throws Exception{
		Date actualTime = new Date(115, 2, 1, 12, 31);
		Date endTime = new Date(112, 5, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 1, 2);
		
		try{
			a.getNextTimeLapse(actualTime);
			assertTrue(false);
		}catch (AndroidCareDateFormatException ex){
			assertTrue(true); // ha saltado la excepción que tenía que saltar
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaRepeticiones_conFechaInicioPasadaYMinutoAntes() throws Exception{
		Date actualTime = new Date(114, 4, 1, 18, 31);
		Date endTime = new Date(130, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(114, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaRepeticiones_conFechaInicioPasadaYMinutoDespues() throws Exception{
		Date actualTime = new Date(114, 4, 1, 18, 39);
		Date endTime = new Date(130, 6, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 1, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRepeticionAnualHastaRepeticiones_conFechaInicioPasadaYHoraAntesCada3() throws Exception{
		Date actualTime = new Date(114, 4, 1, 17, 39);
		Date endTime = new Date(130, 9, 3, 12, 31);
		Date startTime = new Date(112, 4, 1, 18, 38); 

		Reminder a = new Reminder("", "", startTime, endTime, true, Reminder.END_TYPE_ITERATIONS, Reminder.REPEAT_PERIOD_YEAR, 3, 10);
		
		Date nextHour = a.getNextTimeLapse(actualTime);
		Date result = new Date(115, 4, 1, 18, 38);
		assertEquals(result.getTime(), nextHour.getTime());
	}
}
