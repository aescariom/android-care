package org.androidcare.android.reminders;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

public class DaysOfWeekInWhichShouldTrigger implements Serializable {
    private static final long serialVersionUID = -32589040262037485L;

    // sun mon tue wed thu fri sat 
    // 0 1 2 3 4 5 6
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
        // let's loop over the entire week
        for (int i = today, j = 1; i <= 7 + today; i++, j++) {
            // what if we are beyond the end of the week? let's go back to a valid weekday
            int dayToEvaluate = i % 7;  
            if(dayIsSelected[dayToEvaluate]){
                return j;
            }
        }
        return 0;
    }

}
