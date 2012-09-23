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

package org.androidcare.util;

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
public class Week implements Serializable{
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
	 */
	public Week(JSONArray arr) {
		try { this.setMonday(arr.getBoolean(0)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setTuesday(arr.getBoolean(1)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setWednesday(arr.getBoolean(2)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setThursday(arr.getBoolean(3)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setFriday(arr.getBoolean(4)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setSaturday(arr.getBoolean(5)); } catch (JSONException e) { e.printStackTrace(); }
		try { this.setSunday(arr.getBoolean(6)); } catch (JSONException e) { e.printStackTrace(); }
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
}
