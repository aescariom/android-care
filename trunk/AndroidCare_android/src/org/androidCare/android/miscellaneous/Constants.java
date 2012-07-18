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

package org.androidCare.android.miscellaneous;

/**
 * Constant class
 * @author Alejandro Escario MŽndez
 *
 */
public class Constants {
	public static final String APP_URL = "http://androidcare2.appspot.com/";
	public static final String ALERTS_URL = APP_URL + "api/retrieveAlerts";
	
	
	public static final int HOUR = 0;
	public static final int DAY = 1;
	public static final int WEEK = 2;
	public static final int MONTH = 3;
	public static final int YEAR = 4;
		
	public static final int NEVER_ENDS = 0;
	public static final int UNTIL_DATE = 1;
	public static final int ITERATIONS = 2;
	
	public enum daysOfTheWeek {Monday, Tuesday, Wednesday, Thursday, Friday,
		Saturday, Sunday}
}
