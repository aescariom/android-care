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
 * @author Alejandro Escario MŽndez
 * 
 */
public class DaysOfWeekInWhichShouldTrigger implements Serializable {
    private static final long serialVersionUID = -32589040262037485L;

    // mon tue wed thu fri sat sun
    // 0 1 2 3 4 5 6 7
    private boolean[] dayIsSelected = { false, false, false, false, false, false, false };

    DaysOfWeekInWhichShouldTrigger(JSONArray jSONArray) throws AndroidCareDateFormatException, JSONException {
        dayIsSelected[0] = jSONArray.getBoolean(0);
        dayIsSelected[1] = jSONArray.getBoolean(1);
        dayIsSelected[2] = jSONArray.getBoolean(2);
        dayIsSelected[3] = jSONArray.getBoolean(3);
        dayIsSelected[4] = jSONArray.getBoolean(4);
        dayIsSelected[5] = jSONArray.getBoolean(5);
        dayIsSelected[6] = jSONArray.getBoolean(6);
        if ( !dayIsSelected[0] && !dayIsSelected[1] && !dayIsSelected[2] && !dayIsSelected[3]
                && !dayIsSelected[4] && !dayIsSelected[5] && !dayIsSelected[6]) {
            throw new AndroidCareDateFormatException("No day of week selected", jSONArray);
        }
    }

    public DaysOfWeekInWhichShouldTrigger(boolean[] dayIsSelected) {
        this.dayIsSelected = dayIsSelected.clone();
    }

    /**
     * returns the int value with the difference between today and the next selected day
     * 
     * @param time
     * @return
     */
    public int getNextSelectedDayAfter(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int today = cal.get(Calendar.DAY_OF_WEEK); // each week starts on Sunday
                                                   // sun mon tue wed thu fri sat
                                                   // 1 2 3 4 5 6 7
        int diference = 0;
        // let's loop over the entire week
        for (int i = today; i <= 7 + today; i++) {
            // what if we are beyond the end of the week? let's go back to a valid weekday
            int nextDayToTry = i % 7;
            if (dayIsSelected[nextDayToTry]) {
                diference = nextDayToTry - today;
                // @comentario esto lo pongo porque no se seguro si hago lo mismo
                // me resulta muy extraño lo que hace este método…
                assert (diference == getDayAfterInWeekOLD(time));
                break;
            }
        }
        return diference;
    }// @comentario ttu clase tenía 263 líneas; la mía tendrá 76 cuando borremos el método de abajo

    /**
     * returns the int value with the difference between today and the next selected day
     * 
     * @param time
     * @return
     */
    public int getDayAfterInWeekOLD(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int day = cal.get(Calendar.DAY_OF_WEEK); // each week starts on Sunday
        /**
         * sun mon tue wed thu fri sat 1 2 3 4 5 6 7
         */

        // let's loop over the entire week
        for (int i = day; i <= 7 + day; i++) {
            // what if we are beyond the end of the week? let's go back to a valid weekday
            int aux = (i - 1) % 7;
            aux++;
            switch (aux) {
            case 2: // Monday
                if (dayIsSelected[0]) return 2 - day;
                break;
            case 3: // Tuesday
                if (dayIsSelected[1]) return 3 - day;
                break;
            case 4: // Wednesday
                if (dayIsSelected[2]) return 4 - day;
                break;
            case 5: // Thursday
                if (dayIsSelected[3]) return 5 - day;
                break;
            case 6: // Friday
                if (dayIsSelected[4]) return 6 - day;
                break;
            case 7: // Saturday
                if (dayIsSelected[5]) return 7 - day;
                break;
            case 1: // Sunday
                if (dayIsSelected[6]) return 1 - day;
                break;
            }
        }
        return 0;
    }

}
