package org.androidcare.android.reminders;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Reminder implements Serializable {

    private static final long serialVersionUID = -8087552729716719434L;

    // Alert data - the same than in the web
    private int id;
    private String title;
    private String description;
    
    private String blobKey;

    private boolean repeat;
    private Calendar activeFrom;
    private Calendar activeUntil;
    private int numerOfRepetitions;

    private int endType;
    public static final int END_TYPE_NEVER_ENDS = 0;
    public static final int END_TYPE_UNTIL_DATE = 1;
    public static final int END_TYPE_ITERATIONS = 2;

    private int repeatPeriod;
    public static final int REPEAT_PERIOD_HOUR = 0;
    public static final int REPEAT_PERIOD_DAY = 1;
    public static final int REPEAT_PERIOD_WEEK = 2;
    public static final int REPEAT_PERIOD_MONTH = 3;
    public static final int REPEAT_PERIOD_YEAR = 4;
    // How may HOURS/DAYS/... should we repeat
    private int repeatEachXPeriods;

    private DaysOfWeekInWhichShouldTrigger daysOfWeekInWhichShouldTrigger;
    private boolean requestConfirmation;

    // default date time format
    private final static DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss 'UTC' yyyy",
            Locale.ENGLISH);

    public Reminder() {
    }

    public Reminder(JSONObject jsonObj) throws AndroidCareDateFormatException, NumberFormatException,
            JSONException, ParseException {
        // Mandatory fields
        id = Integer.parseInt(jsonObj.getString("id"));
        title = jsonObj.getString("title");
        setActiveFrom(dateFormat.parse(jsonObj.getString("activeFrom")));
        repeat = jsonObj.getBoolean("repeat");
        requestConfirmation = jsonObj.getBoolean("requestConfirmation");

        // Optional fields
        try {
            this.setDescription(jsonObj.getString("description"));
        }
        catch (JSONException e) {
            Log.w("Parsing alert", "No description found");
        }
        try {
            this.setBlobKey(jsonObj.getString("blobKey"));
        }
        catch (JSONException e) {
            Log.w("Parsing alert", "No image found");
        }

        if (repeat) {
            repeatEachXPeriods = jsonObj.getInt("repeatEachXPeriods");
            endType = jsonObj.getInt("endType");
            if (endType == Reminder.END_TYPE_ITERATIONS) {
                numerOfRepetitions = jsonObj.getInt("numerOfRepetitions");
            } else if (endType == Reminder.END_TYPE_UNTIL_DATE) {
                setUntilDate(dateFormat.parse(jsonObj.getString("activeUntil")));
            } else {
                // if is not one of the above values, it must be the following one
                endType = Reminder.END_TYPE_NEVER_ENDS;
            }
            setRepeatPeriod(jsonObj.getInt("repeatPeriod"));
            if (repeatPeriod == Reminder.REPEAT_PERIOD_WEEK) {
                daysOfWeekInWhichShouldTrigger = new DaysOfWeekInWhichShouldTrigger(
                        jsonObj.getJSONArray("daysOfWeekInWhichShouldTrigger"));
            }
        }
    }

    public Reminder(String title, String description, Date startTime){
        this.title = title;
        this.description = description;
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(startTime);
        setActiveFrom(cStart);
    }
    
    public Reminder(String title, String description, Date startTime, Date endTime, boolean repeat,
            int endType, int repeatPeriod, int repeatEach) {
        this.title = title;
        this.description = description;
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(startTime);
        setActiveFrom(cStart);
        this.repeat = repeat;
        this.endType = endType;
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(endTime);
        this.setActiveUntil(cEnd);
        this.repeatPeriod = repeatPeriod;
        this.repeatEachXPeriods = repeatEach;
    }

    public Reminder(String title, String description, Date startTime, Date endTime, boolean repeat,
            int endType, int repeatPeriod, int repeatEach, int iterations) {
        this(title, description, startTime, endTime, repeat, endType, repeatPeriod, repeatEach);
        numerOfRepetitions = iterations;
    }

    public Calendar getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Calendar activeFrom) {
        activeFrom.set(Calendar.SECOND, 0);
        activeFrom.set(Calendar.MILLISECOND, 0);
        this.activeFrom = activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        Calendar c = Calendar.getInstance();
        c.setTime(activeFrom);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        this.activeFrom = c;
    }

    public int getRepeatPeriod() {
        return repeatPeriod;
    }
    
    /**
     * @param repeatPeriod
     * @throws AndroidCareDateFormatException
     *             if repeatPeriod is not a valid repeatPeriod
     */
    public void setRepeatPeriod(int repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
        voidvaladateRepeatPeriod();
    }

    private void voidvaladateRepeatPeriod() {
        switch (this.repeatPeriod) {
        case Reminder.REPEAT_PERIOD_HOUR:
        case Reminder.REPEAT_PERIOD_DAY:
        case Reminder.REPEAT_PERIOD_WEEK:
        case Reminder.REPEAT_PERIOD_MONTH:
        case Reminder.REPEAT_PERIOD_YEAR:
            return;
        default:
            throw new AndroidCareDateFormatException("Repeat period not valid");
        }
    }

    public Calendar getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Calendar activeUntil) {
        activeUntil.set(Calendar.SECOND, 0);
        activeUntil.set(Calendar.MILLISECOND, 0);
        this.activeUntil = activeUntil;
    }

    public void setUntilDate(Date until) {
        Calendar c = Calendar.getInstance();
        c.setTime(until);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        this.activeUntil = c;
    }

    public Date getNextTimeLapse(Date time) {
        return TimeManager.getNextTimeLapse(this, time);
    }

    @Override
    public String toString() {
        return "Reminder: " + getTitle() + " active from " + activeFrom.getTime().toString() + 
                ((activeUntil != null) ? " active to " + activeUntil.getTime().toString() : "");
    }

    public int getEndType() {
        return endType;
    }

    public void setEndType(int endType) {
        this.endType = endType;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public DaysOfWeekInWhichShouldTrigger getDaysOfWeekInWhichShouldTrigger() {
        return daysOfWeekInWhichShouldTrigger;
    }

    public void setDaysOfWeekInWhichShouldTrigger(DaysOfWeekInWhichShouldTrigger days) {
        this.daysOfWeekInWhichShouldTrigger = days;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setBlobKey(String blobKey) {
        this.blobKey = blobKey;
    }

    public String getBlobKey() {
        return blobKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequestConfirmation() {
        return requestConfirmation;
    }

    public void setRequestConfirmation(boolean requestConfirmation) {
        this.requestConfirmation = requestConfirmation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumerOfRepetitions() {
        return numerOfRepetitions;
    }

    public int getRepeatEachXPeriods() {
        return repeatEachXPeriods;
    }

    public void setRepeatEachXPeriods(int epeatEachXPeriods) {
        this.repeatEachXPeriods = epeatEachXPeriods;
    }
}