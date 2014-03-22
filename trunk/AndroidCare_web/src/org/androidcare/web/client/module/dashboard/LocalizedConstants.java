package org.androidcare.web.client.module.dashboard;

public interface LocalizedConstants extends org.androidcare.web.client.internacionalization.LocalizedConstants {
	
	/**
	 * Tabs
	 */
	@DefaultStringValue("Reminders")
	String reminders();
	@DefaultStringValue("Map")
	String map();
    @DefaultStringValue("Alarms")
    String alarms();

	/**
	 * Buttons
	 */
	@DefaultStringValue("New")
	String addNew();
	@DefaultStringValue("Submit")
	String submit();
	@DefaultStringValue("Cancel")
	String cancel();
	@DefaultStringValue("Continue")
	String proceed();
	@DefaultStringValue("Edit")
	String edit();
	@DefaultStringValue("Log")
	String log();
	@DefaultStringValue("Delete")
	String delete();
	@DefaultStringValue("Refresh")
	String refresh();
		
	/**
	 * Labels
	 */
	@DefaultStringValue("Add new reminder")
	String addNewReminder();
	
	@DefaultStringValue("Display log")
	String displayLog();
	
	@DefaultStringValue("Remove reminder")
	String removeReminder();
	
	@DefaultStringValue("Title")
	String title();
	
	@DefaultStringValue("Description")
	String description();

	@DefaultStringValue("Repeat")
	String repeat();
	
	@DefaultStringValue("Repeat each")
	String repeatEach();

	@DefaultStringValue("Repeat period")
	String repeatPeriod();

	@DefaultStringValue("Photo")
	String photo();
	
	@DefaultStringValue("Week days")
	String weekDays();

	@DefaultStringValue("Request confirmation")
	String requestConfirmation();

	@DefaultStringValue("Starts on")
	String since();
	@DefaultStringValue("Date and time")
	String dateTime();
	@DefaultStringValue("Ends")
	String until();

	@DefaultStringValue("Never")
	String never();
	@DefaultStringValue("After")
	String after();
	@DefaultStringValue("iterations")
	String iterations();
	@DefaultStringValue("Date")
	String date();
	
	@DefaultStringValue("Hour")
	String hour();
	@DefaultStringValue("Day")
	String day();
	@DefaultStringValue("Week")
	String week();
	@DefaultStringValue("Month")
	String month();
	@DefaultStringValue("Year")
	String year();

	@DefaultStringValue("M")
	String mondayShort();
	@DefaultStringValue("T")
	String tuesdayShort();
	@DefaultStringValue("W")
	String wednesdayShort();
	@DefaultStringValue("Th")
	String thursdayShort();
	@DefaultStringValue("F")
	String fridayShort();
	@DefaultStringValue("Sa")
	String saturdayShort();
	@DefaultStringValue("Su")
	String sundayShort();

	@DefaultStringValue("at")
	String atTime();

	@DefaultStringValue("+ Show advanced form")
	String toggleToAdvanced();
	@DefaultStringValue("- Show basic form")
	String toggleToBasic();

	@DefaultStringValue("Number of positions")
	String positionNumber();

	@DefaultStringValue("Latitude")
	String latitude();
	@DefaultStringValue("Longitude")
	String longitude();
	@DefaultStringValue("Time")
	String time();

    @DefaultStringValue("Add new alarm")
    String addNewAlarm();
    @DefaultStringValue("Edit alarm")
    String editAlarm();
    @DefaultStringValue("Remove alarm")
    String removeAlarm();

    @DefaultStringValue("Alarm type")
    String alarmType();
    @DefaultStringValue("Alarm name")
    String alarmName();
    @DefaultStringValue("Alarm type")
    String alarmType();
    @DefaultStringValue("Severity level")
    String severityLevel();
    @DefaultStringValue("Start time")
    String startTime();
    @DefaultStringValue("End time")
    String endTime();
    @DefaultStringValue("Red zone map")
    String redZoneMap();
    @DefaultStringValue("Phone number")
    String phoneNumber();
    @DefaultStringValue("Email")
    String email();
    @DefaultStringValue("Make call")
    String makeCall();
    @DefaultStringValue("Send SMS")
    String sendSMS();
    @DefaultStringValue("Send Email")
    String sendEmail();
    @DefaultStringValue("Place")
    String place();

    @DefaultStringValue("Wake up")
    String wakeUp();
    @DefaultStringValue("Red zone")
    String redZone();
    @DefaultStringValue("Fell off")
    String fellOff();
    
    @DefaultStringValue("Information")
    String info();
    @DefaultStringValue("Warning")
    String warning();
    @DefaultStringValue("Severe")
    String severe();
    @DefaultStringValue("Very severe")
    String verySevere();
	/**
	 * return Messages
	 */
	@DefaultStringValue("The range must be between 1 and 100")
	String mustBeBetween1and100();
	
}
