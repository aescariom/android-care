package org.androidcare.web.client.widgets.forms.panels;

import org.androidcare.web.client.LocalizedConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class DaysOfTheWeek extends HorizontalPanel {

	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;

	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

	protected Label lblMonday = new Label(LocalizedConstants.mondayShort());
	protected CheckBox chkMonday = new CheckBox();
	
	protected Label lblTuesday = new Label(LocalizedConstants.tuesdayShort());
	protected CheckBox chkTuesday = new CheckBox();
	
	protected Label lblWednesday = new Label(LocalizedConstants.wednesdayShort());
	protected CheckBox chkWednesday = new CheckBox();
	
	protected Label lblThursday = new Label(LocalizedConstants.thursdayShort());
	protected CheckBox chkThursday = new CheckBox();
	
	protected Label lblFriday = new Label(LocalizedConstants.fridayShort());
	protected CheckBox chkFriday = new CheckBox();
	
	protected Label lblSaturday = new Label(LocalizedConstants.saturdayShort());
	protected CheckBox chkSaturday = new CheckBox();
	
	protected Label lblSunday = new Label(LocalizedConstants.sundayShort());
	protected CheckBox chkSunday = new CheckBox();
	
	public DaysOfTheWeek(){
		super();
		
		this.add(chkMonday);
		this.add(lblMonday);

		this.add(chkTuesday);
		this.add(lblTuesday);

		this.add(chkWednesday);
		this.add(lblWednesday);

		this.add(chkThursday);
		this.add(lblThursday);

		this.add(chkFriday);
		this.add(lblFriday);

		this.add(chkSaturday);
		this.add(lblSaturday);

		this.add(chkSunday);
		this.add(lblSunday);		
	}

	public boolean[] getDaysArray() {
		boolean[] days = new boolean[7];
		days[DaysOfTheWeek.MONDAY] = chkMonday.getValue();
		days[DaysOfTheWeek.TUESDAY] = chkTuesday.getValue();
		days[DaysOfTheWeek.WEDNESDAY] = chkWednesday.getValue();
		days[DaysOfTheWeek.THURSDAY] = chkThursday.getValue();
		days[DaysOfTheWeek.FRIDAY] = chkFriday.getValue();
		days[DaysOfTheWeek.SATURDAY] = chkSaturday.getValue();
		days[DaysOfTheWeek.SUNDAY] = chkSunday.getValue();
		return days;
	}

	public void setValue(boolean[] days) {
		if(days.length >= 7){
			chkMonday.setValue(days[DaysOfTheWeek.MONDAY]);
			chkTuesday.setValue(days[DaysOfTheWeek.TUESDAY]);
			chkWednesday.setValue(days[DaysOfTheWeek.WEDNESDAY]);
			chkThursday.setValue(days[DaysOfTheWeek.THURSDAY]);
			chkFriday.setValue(days[DaysOfTheWeek.FRIDAY]);
			chkSaturday.setValue(days[DaysOfTheWeek.SATURDAY]);
			chkSunday.setValue(days[DaysOfTheWeek.SUNDAY]);
		}
	}
}
