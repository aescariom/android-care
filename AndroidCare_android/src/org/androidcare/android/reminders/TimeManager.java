package org.androidcare.android.reminders;

import java.util.Calendar;
import java.util.Date;

abstract class TimeManager {

    /**
     * returns the next date in which the alert should be triggered
     * 
     * @param timeScheduleRequested
     *            when it was triggered for the last time
     * @return if there is no future dates it returns null
     */
    public static Date getNextTimeLapse(Reminder reminder, Date timeScheduleRequested) {
        timeScheduleRequested.setSeconds(0);
        if (reminder.getActiveFrom() != null) {
            if (timeScheduleRequested.before(reminder.getActiveFrom().getTime()) &&
            // @comentario no lo pillo �por qu� esta segunda condici�n?
                    reminder.getRepeatPeriod() != Reminder.REPEAT_PERIOD_WEEK) {
                // If we are before the start date, let's select the start date as the next execution time
                return reminder.getActiveFrom().getTime();
            } else if (reminder.isRepeat()) {
                // let's look for the next occurrence
                switch (reminder.getEndType()) {
                case Reminder.END_TYPE_NEVER_ENDS:
                    return TimeManager.getNextTimeLapseEndTypeNeverEnds(reminder, timeScheduleRequested);
                case Reminder.END_TYPE_UNTIL_DATE:
                    return TimeManager.getNextTimeLapseByDate(reminder, timeScheduleRequested);
                case Reminder.END_TYPE_ITERATIONS:
                    return TimeManager.getNextTimeLapseByIterations(reminder, timeScheduleRequested);
                }
            }
        }
        return null;
    }

    private static Date getNextTimeLapseEndTypeNeverEnds(Reminder reminder, Date timeScheduleRequested) {
        // 1 - let's calculate the next ocurrence
        switch (reminder.getRepeatPeriod()) {
        case Reminder.REPEAT_PERIOD_HOUR:
            return TimeManager.getNextHourByTimeLapse(reminder, timeScheduleRequested);
        case Reminder.REPEAT_PERIOD_DAY:
            return TimeManager.getNextDayByTimeLapse(reminder, timeScheduleRequested);
        case Reminder.REPEAT_PERIOD_WEEK:
            return TimeManager.getNextWeekDayByTimeLapse(reminder, timeScheduleRequested);
        case Reminder.REPEAT_PERIOD_MONTH:
            return TimeManager.getNextMonthByTimeLapse(reminder, timeScheduleRequested);
        default:// Reminder.REPEAT_PERIOD_YEAR:
            return TimeManager.getNextYearByTimeLapse(reminder, timeScheduleRequested);
        }// @comentario esto devolv�an null si no se cumpl�a ningun case, pero entiendo que eso no puede ser
         // salvo bug
    }

    private static Date getNextTimeLapseByDate(Reminder reminder, Date timeScheduleRequested) {
        // 1 - is this alert still active? or, on the other hand, the end date is in the past
        if (timeScheduleRequested.after(reminder.getActiveUntil().getTime())) {
            return null;
        }

        // 2 - let's calculate the next ocurrence
        switch (reminder.getRepeatPeriod()) {
        case Reminder.REPEAT_PERIOD_HOUR:
            Date nextHour = TimeManager.getNextHourByTimeLapse(reminder, timeScheduleRequested);
            if (nextHour.before(reminder.getActiveUntil().getTime())) {
                return nextHour;
            }
            break;
        case Reminder.REPEAT_PERIOD_DAY:
            Date nextDay = TimeManager.getNextDayByTimeLapse(reminder, timeScheduleRequested);
            if (nextDay.before(reminder.getActiveUntil().getTime())) {
                return nextDay;
            }
            break;
        case Reminder.REPEAT_PERIOD_WEEK:
            Date nextDayOfTheWeek = TimeManager.getNextWeekDayByTimeLapse(reminder, timeScheduleRequested);
            if (nextDayOfTheWeek.before(reminder.getActiveUntil().getTime())) {
                return nextDayOfTheWeek;
            }
            break;
        case Reminder.REPEAT_PERIOD_MONTH:
            Date nextMonth = TimeManager.getNextMonthByTimeLapse(reminder, timeScheduleRequested);
            if (nextMonth.before(reminder.getActiveUntil().getTime())) {
                return nextMonth;
            }
            break;
        case Reminder.REPEAT_PERIOD_YEAR:
            Date nextYear = TimeManager.getNextYearByTimeLapse(reminder, timeScheduleRequested);
            if (nextYear.before(reminder.getActiveUntil().getTime())) {
                return nextYear;
            }
            break;
        }
        return null;
    }

    private static Date getNextTimeLapseByIterations(Reminder reminder, Date timeScheduleRequested) {
        switch (reminder.getRepeatPeriod()) {
        case Reminder.REPEAT_PERIOD_HOUR:
            Date nextHour = TimeManager.getNextHourByTimeLapse(reminder, timeScheduleRequested);
            if (TimeManager.executedTimesUntilByHour(reminder, nextHour) < reminder.getNumerOfRepetitions()) {
                return nextHour;
            }
            break;
        case Reminder.REPEAT_PERIOD_DAY:
            Date nextDay = TimeManager.getNextDayByTimeLapse(reminder, timeScheduleRequested);
            if (TimeManager.executedTimesUntilByDay(reminder, nextDay) < reminder.getNumerOfRepetitions()) {
                return nextDay;
            }
            break;
        case Reminder.REPEAT_PERIOD_WEEK:
            Date nextDayOfTheWeek = TimeManager.getNextWeekDayByTimeLapse(reminder, timeScheduleRequested);
            if (TimeManager.executedTimesUntilByWeek(reminder, nextDayOfTheWeek) < 
                    reminder.getNumerOfRepetitions()) {
                return nextDayOfTheWeek;
            }
            break;
        case Reminder.REPEAT_PERIOD_MONTH:
            Date nextMonth = TimeManager.getNextMonthByTimeLapse(reminder, timeScheduleRequested);
            if (TimeManager.executedTimesUntilByMonth(reminder, nextMonth) < reminder.getNumerOfRepetitions()) {
                return nextMonth;
            }
            break;
        case Reminder.REPEAT_PERIOD_YEAR:
            Date nextYear = TimeManager.getNextYearByTimeLapse(reminder, timeScheduleRequested);
            if (TimeManager.executedTimesUntilByYear(reminder, nextYear) < reminder.getNumerOfRepetitions()) {
                return nextYear;
            }
            break;
        }
        return null;
    }

    // @comentario cuando le� tu funci�n pens� que estaba bien. Despu�s me puse a cambiar nombres
    // como yo siempre hago. Despu�s le� de nuevo la funci�n. Ahora, o una de dos, o no entiendo que quieres hacer
    //O esto est� mal. �ves t� ahora problema?.
    private static Date getNextHourByTimeLapse(Reminder reminder, Date timeScheduleRequested) {
        // 2 - getting the time lapse between the start date and today
        long millsecSindeReminderActiveToScheduleRequested = (timeScheduleRequested.getTime() - 
                reminder.getActiveFrom().getTimeInMillis());
        // 3 - let's get the number of hours between those two times
        int hoursSinceReminderActiveToScheduleRequested = (int) (millsecSindeReminderActiveToScheduleRequested 
                / ONE_HOUR_IN_MILLISEC);
        // 4 - how many hours have passed since the last executed interval?
        int hoursSiceLastTrigger = hoursSinceReminderActiveToScheduleRequested
                % reminder.getRepeatEachXPeriods();
        /*
         * 5 - getting the number of hours between the start date and the next execution time by adding to the
         * number of hours betwen the start date and the last execution time to a new whole 'repeat period' in
         * hours
         */
        int hoursToNextTrigger = hoursSinceReminderActiveToScheduleRequested
                                      + reminder.getRepeatEachXPeriods() - hoursSiceLastTrigger;
        // 6 - add the calculated hours to the start date
        return new Date(reminder.getActiveFrom().getTimeInMillis() + hoursToNextTrigger * ONE_HOUR_IN_MILLISEC);
    }

    private static Date getNextDayByTimeLapse(Reminder reminder, Date timeScheduleRequested) {
        // 2 - getting the time lapse between the start date and today
        long millsecSindeReminderActiveToScheduleRequested = timeScheduleRequested.getTime()
                - reminder.getActiveFrom().getTimeInMillis();
        // 3 - let's get the number of hours between those two times
        int daysSinceReminderActiveToScheduleRequested = (int) (millsecSindeReminderActiveToScheduleRequested 
                / ONE_DAY_IN_MILLISEC);
        // 4 - how many days passed since the last executed interval?
        int daysSiceLastTrigger = daysSinceReminderActiveToScheduleRequested
                % reminder.getRepeatEachXPeriods();
        /*
         * 5 - getting the number of days between the start date and the next execution time by adding to the
         * number of days betwen the start date and the last execution time to a new whole 'repeat period' in
         * days
         */
        int daysToNextTrigger = daysSinceReminderActiveToScheduleRequested
                                    + reminder.getRepeatEachXPeriods() - daysSiceLastTrigger;
        // 6 - add the calculated hours to the start date
        return new Date(reminder.getActiveFrom().getTimeInMillis() + daysToNextTrigger * ONE_DAY_IN_MILLISEC);
    }

    /**
     * Having a reference time, this method will return the next time the alert should be triggered based on
     * the number of weeks or days of the week between alarms
     * 
     * @param timeScheduleRequested
     * @return
     */
    // @comentario quiero mirar este m�todo junto contigo; no s� si me entero
    private static Date getNextWeekDayByTimeLapse(Reminder reminder, Date timeScheduleRequested) {
        /*
         * NOTES: - the first day of the week is monday
         */
        int nextDayOfWeekInWhichTrigger = 0;
        // @comentario hay que darle un nombre adecuado a esto, pero no s� cu�l
        Date reference = null;

        // 2 - getting the time lapse between the start date and today
        long millisecSinceReminderActiveToScheduleRequested = timeScheduleRequested.getTime()
                                                                - reminder.getActiveFrom().getTimeInMillis();
        // 3 - let's get the number of weeks between those two times
        int weeksSindeReminderActiveToScheduleRequested = 
                (int) (millisecSinceReminderActiveToScheduleRequested / ONE_WEEK_IN_MILLISEC);
        // 4 - how many weeks passed since the last executed interval?
        int weeksSiceLastTrigger = weeksSindeReminderActiveToScheduleRequested
                                                    % reminder.getRepeatEachXPeriods();

        // 5 - check if it's the first time that the alarm will be triggered
        if (timeScheduleRequested.after(reminder.getActiveFrom().getTime())) {
            // @comentario donde el siguiente comentario dice "end date" deber�a decir "start date" �no?
            // the alarm could have been already triggered, because today is after the end date
            // 6 - Wich is the next 'week day' that the alarm should be triggered?
            nextDayOfWeekInWhichTrigger = reminder.getDaysOfWeekInWhichShouldTrigger()
                                                  .getNextSelectedDayAfter(timeScheduleRequested);
            /*
             * 7 - if aux is equals to 0, then we are at the beginning of the week, so we can just get the
             * next time just by calculating the difference also, if we are before the time of the day in wich
             * the alarm should be triggered then, the next alarm should be scheduled using the same
             * operations
             */
            boolean nextDayOfWeekInWhichTriggerIsBeforeToday = nextDayOfWeekInWhichTrigger < 0;
            boolean atLeastOneWeekHasPassedSiceLastTrigger = weeksSiceLastTrigger != 0;
            if (nextDayOfWeekInWhichTriggerIsBeforeToday
                    || atLeastOneWeekHasPassedSiceLastTrigger
                    || shouldTriggerTodaySomeTimeAfterNow(reminder, timeScheduleRequested,
                                                          nextDayOfWeekInWhichTrigger)) {
                weeksSindeReminderActiveToScheduleRequested = reminder.getRepeatEachXPeriods()
                        - weeksSiceLastTrigger;
            } else {
                // 7 - elsewhere, we will work with the following week
                weeksSindeReminderActiveToScheduleRequested = 0;
            }

            // 8 - otherwise we are in the same week
            reference = new Date(timeScheduleRequested.getTime());
            // 9 - The time of the day should be the same of the start date
            reference.setHours(reminder.getActiveFrom().get(Calendar.HOUR_OF_DAY));
            reference.setMinutes(reminder.getActiveFrom().get(Calendar.MINUTE));
        } else {// we are before the first time the alarm should be triggered
                // 6 - Wich is the next 'week day' that the alarm should be triggered?
            nextDayOfWeekInWhichTrigger = reminder.getDaysOfWeekInWhichShouldTrigger()
                                                  .getNextSelectedDayAfter(reminder.getActiveFrom().getTime());

            // 7 - if the number of days is > 0 then we are in the same week but, if it's < 0 then we have to
            // move to the next week
            if (nextDayOfWeekInWhichTrigger < 0
            // @comentario �es esto un bug? M�s bien la funci�n deber�a tener el nombre
            // shouldNotTriggerTodaySomeTimeAfterNow pero la condici�n era la misma que en el anterior caso
                    || shouldTriggerTodaySomeTimeAfterNow(reminder, timeScheduleRequested,
                                                          nextDayOfWeekInWhichTrigger)) {
                // 7 - this week has no more 'active' days, let's move to the next week
                weeksSindeReminderActiveToScheduleRequested = reminder.getRepeatEachXPeriods();
            } else {// 7 - we still have work this week
                weeksSindeReminderActiveToScheduleRequested = 0;
            }

            // 8 - calculating the reference time
            reference = reminder.getActiveFrom().getTime();
        }
        // 9/10 - getting the next execution time
        return new Date(reference.getTime() + weeksSindeReminderActiveToScheduleRequested
                * ONE_WEEK_IN_MILLISEC + nextDayOfWeekInWhichTrigger * ONE_DAY_IN_MILLISEC);
    }

    private static boolean shouldTriggerTodaySomeTimeAfterNow(Reminder reminder, Date timeScheduleRequested,
            int nextDayOfWeekInWhichTrigger) {
        boolean triggerTodaySomeTimeAfterNow;
        boolean triggerToday = nextDayOfWeekInWhichTrigger == 0;
        boolean triggerTimeIsAfterScheduleRequestedTime = timeScheduleRequested.getHours() > 
                        reminder.getActiveFrom().get(Calendar.HOUR_OF_DAY)
                || ((timeScheduleRequested.getHours() == reminder.getActiveFrom().get(Calendar.HOUR_OF_DAY) 
                        && timeScheduleRequested.getMinutes() > reminder.getActiveFrom()
                                                                                                                                                            .get(Calendar.MINUTE)));
        triggerTodaySomeTimeAfterNow = triggerToday && triggerTimeIsAfterScheduleRequestedTime;
        return triggerTodaySomeTimeAfterNow;
    }

    /**
     * Having a reference time, this method will return the next time the alert should be triggered based on
     * the number of months or days of the week between alarms
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static Date getNextMonthByTimeLapse(Reminder reminder, Date timeScheduleRequested) {
        int monthOfNextTrigger, totalTimeInMonths, yearOfNextTrigger;

        // 1 - calculating the reference times
        Date dateReminderIsActiveFrom = new Date(reminder.getActiveFrom().getTimeInMillis());
        Calendar timeScheduleRequestedAsACalendar = Calendar.getInstance();
        timeScheduleRequestedAsACalendar.setTime(timeScheduleRequested);

        // 2 - get the difference (in months) between today and the start date
        int monthsSindeReminderActiveToScheduleRequested = 
                (timeScheduleRequestedAsACalendar.get(Calendar.YEAR) * 12
                                + timeScheduleRequestedAsACalendar.get(Calendar.MONTH))
                 - (reminder.getActiveFrom().get(Calendar.YEAR) * 12 
                             + reminder.getActiveFrom().get(Calendar.MONTH));
        // 3 - how many months passed since the last executed interval?
        int monthsSiceLastTrigger = monthsSindeReminderActiveToScheduleRequested
                % reminder.getRepeatEachXPeriods();

        if (monthsSiceLastTrigger == 0) {
            // 4 - this is an active month, let's check if we are before or after the trigger moment
            if (TimeManager.isBeforeInMonth(reminder, timeScheduleRequestedAsACalendar)) {
                monthOfNextTrigger = monthsSindeReminderActiveToScheduleRequested;
            } else {
                monthOfNextTrigger = (monthsSindeReminderActiveToScheduleRequested
                        + reminder.getRepeatEachXPeriods() - monthsSiceLastTrigger);
            }
        } else {
            // 4 - this month is not active, let's jump to the next one
            monthOfNextTrigger = (monthsSindeReminderActiveToScheduleRequested
                    + reminder.getRepeatEachXPeriods() - monthsSiceLastTrigger);
        }

        // 5 - calculating the new month and year
        totalTimeInMonths = dateReminderIsActiveFrom.getMonth() + monthOfNextTrigger;
        yearOfNextTrigger = dateReminderIsActiveFrom.getYear() + totalTimeInMonths / 12;
        monthOfNextTrigger = totalTimeInMonths % 12;

        // 6 - setting up the next date
        dateReminderIsActiveFrom.setMonth(monthOfNextTrigger);
        dateReminderIsActiveFrom.setYear(yearOfNextTrigger);

        return dateReminderIsActiveFrom;
    }

    /**
     * Having a reference time, this method will return the next time the alert should be triggered based on
     * the number of months or days of the week between alarms
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static Date getNextYearByTimeLapse(Reminder reminder, Date timeScheduleRequested) {
        int yearOfNextTrigger;

        // 1 - calculating the reference times
        Date dateReminderIsActiveFrom = new Date(reminder.getActiveFrom().getTimeInMillis());
        Calendar timeScheduleRequestedAsACalendar = Calendar.getInstance();
        timeScheduleRequestedAsACalendar.setTime(timeScheduleRequested);

        // 2 - get the difference (in months) between today and the start date
        int yearsSindeReminderActiveToScheduleRequested = timeScheduleRequestedAsACalendar.get(Calendar.YEAR)
                - reminder.getActiveFrom().get(Calendar.YEAR);
        // 3 - how many weeks passed since the last executed interval?
        int yearsSiceLastTrigger = yearsSindeReminderActiveToScheduleRequested
                % reminder.getRepeatEachXPeriods();
        if (yearsSiceLastTrigger == 0) {
            // 4 - this is an active year, let's check if we are before or after the trigger moment
            if (TimeManager.isBeforeInYear(reminder, timeScheduleRequestedAsACalendar)) {
                yearOfNextTrigger = yearsSindeReminderActiveToScheduleRequested;
            } else {
                yearOfNextTrigger = (yearsSindeReminderActiveToScheduleRequested
                        + reminder.getRepeatEachXPeriods() - yearsSiceLastTrigger);
            }
        } else {
            // 4 - this is not an active year, let's jump to the next one
            yearOfNextTrigger = (yearsSindeReminderActiveToScheduleRequested
                    + reminder.getRepeatEachXPeriods() - yearsSiceLastTrigger);
        }

        // 5 - adding years to the start date
        yearOfNextTrigger += dateReminderIsActiveFrom.getYear();
        dateReminderIsActiveFrom.setYear(yearOfNextTrigger);
        return dateReminderIsActiveFrom;
    }

    /**
     * number of times that the alert has been executed (in hours)
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static int executedTimesUntilByHour(Reminder reminder, Date timeScheduleRequested) {

        // 2 - calculating the number of days in this lapse of time
        long millsecSindeReminderActiveToScheduleRequested = timeScheduleRequested.getTime()
                - reminder.getActiveFrom().getTimeInMillis();
        int hours = (int) (millsecSindeReminderActiveToScheduleRequested / ONE_HOUR_IN_MILLISEC);
        return hours / reminder.getRepeatEachXPeriods();
    }

    /**
     * number of times that the alert has been executed (in days)
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static int executedTimesUntilByDay(Reminder reminder, Date timeScheduleRequested) {
        // 2 - calculating the number of days in this lapse of time
        long millsecSindeReminderActiveToScheduleRequested = timeScheduleRequested.getTime()
                - reminder.getActiveFrom().getTimeInMillis();
        int days = (int) (millsecSindeReminderActiveToScheduleRequested / ONE_DAY_IN_MILLISEC);

        return days / reminder.getRepeatEachXPeriods();
    }

    /**
     * number of times that the alert has been executed (in weeks)
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static int executedTimesUntilByWeek(Reminder reminder, Date timeScheduleRequested) {
        // 2 - calculating the number of weeks in this lapse of time
        long millsecSindeReminderActiveToScheduleRequested = timeScheduleRequested.getTime()
                - reminder.getActiveFrom().getTimeInMillis();
        int weeks = (int) (millsecSindeReminderActiveToScheduleRequested / ONE_WEEK_IN_MILLISEC);

        return weeks / reminder.getRepeatEachXPeriods();
    }

    /**
     * number of times that the alert has been executed (in months)
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static int executedTimesUntilByMonth(Reminder reminder, Date timeScheduleRequested) {
        Calendar cTime = Calendar.getInstance();
        cTime.setTime(timeScheduleRequested);

        int diff = (timeScheduleRequested.getYear() * 12 + timeScheduleRequested.getMonth())
                - (reminder.getActiveFrom().getTime().getYear() * 12 + reminder.getActiveFrom().getTime()
                                                                               .getMonth());

        if ( !TimeManager.isBeforeInYear(reminder, cTime)) {
            diff++;
        }

        return diff / reminder.getRepeatEachXPeriods();
    }

    /**
     * number of times that the alert has been executed (in years)
     * 
     * @param timeScheduleRequested
     * @return
     */
    private static int executedTimesUntilByYear(Reminder reminder, Date timeScheduleRequested) {
        Calendar cTime = Calendar.getInstance();
        cTime.setTime(timeScheduleRequested);

        int diff = timeScheduleRequested.getYear() - reminder.getActiveFrom().getTime().getYear();

        if ( !TimeManager.isBeforeInMonth(reminder, cTime)) {
            diff++;
        }

        return diff / reminder.getRepeatEachXPeriods();
    }

    /**
     * Determines if we are before or after a 'moment' (day, hour, minute) inside a year
     * 
     * @param cTime
     * @return
     */
    private static boolean isBeforeInYear(Reminder reminder, Calendar cTime) {
        return cTime.get(Calendar.MONTH) < reminder.getActiveFrom().get(Calendar.MONTH)
                || (cTime.get(Calendar.MONTH) == reminder.getActiveFrom().get(Calendar.MONTH) 
                                && TimeManager.isBeforeInMonth(reminder, cTime));
    }

    /**
     * Determines if we are before or after a 'moment' (day, hour, minute) inside a month
     * 
     * @param cTime
     * @return
     */
    private static boolean isBeforeInMonth(Reminder reminder, Calendar cTime) {
        return cTime.get(Calendar.DAY_OF_MONTH) < reminder.getActiveFrom().get(Calendar.DAY_OF_MONTH)
                || (cTime.get(Calendar.DAY_OF_MONTH) == reminder.getActiveFrom().get(Calendar.DAY_OF_MONTH) 
                     && (cTime.get(Calendar.HOUR_OF_DAY) < reminder.getActiveFrom().get(Calendar.HOUR_OF_DAY) 
                     || (cTime.get(Calendar.HOUR_OF_DAY) == reminder.getActiveFrom() .get(Calendar.HOUR_OF_DAY) 
                     && cTime.get(Calendar.MINUTE) < reminder.getActiveFrom() .get(Calendar.MINUTE))));
    }

    private static final long ONE_WEEK_IN_MILLISEC = 1000 * 60 * 60 * 24 * 7;
    private static final long ONE_DAY_IN_MILLISEC = 1000 * 60 * 60 * 24;
    private static final long ONE_HOUR_IN_MILLISEC = 1000 * 60 * 60;
}
