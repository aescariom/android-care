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

package org.androidcare.android.reminders;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class represents a week into the program
 * 
 * @author Alejandro Escario MŽndez
 *
 */
class Week implements Serializable{
	private static final long serialVersionUID = -32589040262037485L;
	
	public enum daysOfTheWeek {Monday, Tuesday, Wednesday, Thursday, Friday,
		Saturday, Sunday}
	
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	
	/**
	 * 
	 * @param arr
	 * @throws NoDaySelectedException 
	 * @throws NoDateFoundException 
	 */
	public Week(JSONArray arr) throws NoDaySelectedException {
		try { this.setMonday(arr.getBoolean(0)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setTuesday(arr.getBoolean(1)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setWednesday(arr.getBoolean(2)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setThursday(arr.getBoolean(3)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setFriday(arr.getBoolean(4)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setSaturday(arr.getBoolean(5)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setSunday(arr.getBoolean(6)); } catch (JSONException e) { e.printStackTrace(); }
		if(!this.monday && !this.tuesday && !this.wednesday && !this.thursday && !this.friday &&
				!this.saturday && !this.sunday){
			throw new NoDaySelectedException();
		}
	}

	/**
	 * default constructor
	 */
	public Week() {
		this.monday = false;
		this.setTuesday(false);
		this.setWednesday(false);
		this.setThursday(false);
		this.setFriday(false);
		this.setSaturday(false);
		this.setSunday(false);
	}

	/**
	 * determines if all the days are selected or not; that decision depends on the 
	 * parameter b
	 * @param b
	 * @return
	 */
	public boolean isAll(boolean b) {
		if(this.isMonday() != b){
			return false;
		}
		if(this.isTuesday() != b){
			return false;
		}
		if(this.isWednesday() != b){
			return false;
		}
		if(this.isThursday() != b){
			return false;
		}
		if(this.isFriday() != b){
			return false;
		}
		if(this.isSaturday() != b){
			return false;
		}
		if(this.isSunday() != b){
			return false;
		}
		return true;
	}

	/**
	 * @return the monday
	 */
	public boolean isMonday() {
		return monday;
	}

	/**
	 * @param monday the monday to set
	 */
	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	/**
	 * @return the friday
	 */
	public boolean isFriday() {
		return friday;
	}

	/**
	 * @param friday the friday to set
	 */
	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTuesday() {
		return tuesday;
	}

	/**
	 * 
	 * @param tuesday
	 */
	private void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isWednesday() {
		return wednesday;
	}

	/**
	 * 
	 * @param wednesday
	 */
	private void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isThursday() {
		return thursday;
	}

	/**
	 * 
	 * @param thursday
	 */
	private void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSaturday() {
		return saturday;
	}

	/**
	 * 
	 * @param saturday
	 */
	private void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSunday() {
		return sunday;
	}

	/**
	 * 
	 * @param sunday
	 */
	private void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	/**
	 * returns the int value with the difference between today and the next selected day
	 * @param time
	 * @return
	 * @throws NoDaySelectedException
	 */
	public int getDayAfterInWeek(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int day = cal.get(Calendar.DAY_OF_WEEK); // each week starts on Sunday
		/**
		 *  sun mon tue wed thu fri sat
		 *   1   2   3   4   5   6   7
		 */
		
		// let's loop over the entire week
		for(int i = day; i <= 7 + day; i++){
			// what if we are beyond the end of the week? let's go back to a valid weekday 
			int aux = (i-1) % 7;
			aux++;
			switch(aux){
			case 2: // Monday
				if(this.isMonday()) return 2 - day;
				break;
			case 3: // Tuesday
				if(this.isTuesday()) return 3 - day;
				break;
			case 4: // Wednesday
				if(this.isWednesday()) return 4 - day;
				break;
			case 5: // Thursday
				if(this.isThursday()) return 5 - day;
				break;
			case 6: // Friday
				if(this.isFriday()) return 6 - day;
				break;
			case 7: // Saturday
				if(this.isSaturday()) return 7 - day;
				break;
			case 1: // Sunday
				if(this.isSunday()) return 1 - day;
				break;
			}
		}
		return 0;
	}
}
