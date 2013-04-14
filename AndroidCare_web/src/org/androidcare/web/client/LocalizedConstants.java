package org.androidcare.web.client;

import com.google.gwt.i18n.client.Constants;

public interface LocalizedConstants extends Constants {
	
	/**
	 * Tabs
	 */
	@DefaultStringValue("Reminders")
	String reminders();
	@DefaultStringValue("Map")
	String map();
	
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
	
	/**
	 * return Messages
	 */
	@DefaultStringValue("OK")
	String ok();
	@DefaultStringValue("Error")
	String error();
	@DefaultStringValue("Server error")
	String serverError();
}
