package org.androidcare.web.client.widgets.forms.panels;

import java.util.Date;

import org.androidcare.web.client.LocalizedConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.Format;

public class DateTimeBox extends HorizontalPanel {

	private static final DateTimeFormat defaultDateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final DateTimeFormat defaultTimeFormat = DateTimeFormat.getFormat("HH:mm");
	private static final Format dateFormat = new DateBox.DefaultFormat(defaultDateFormat);
	private static final Format timeFormat = new DateBox.DefaultFormat(defaultTimeFormat);
	
	private static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

	protected DateBox txtDate;
	protected Label lblAt;
	protected DateBox txtTime;
	protected boolean showTime;
	
	public DateTimeBox(){
		super();
		init(true);
	}

	public DateTimeBox(boolean time){
		super();		
		init(time);
	}

	private void init(boolean time) {
		showTime = time;
		
		txtDate = new DateBox();
		txtDate.setFormat(dateFormat);
		
		this.add(txtDate);
		
		lblAt = new Label(LocalizedConstants.atTime());
		lblAt.setVisible(showTime);
		this.add(lblAt);
		
		txtTime = new DateBox();
		txtTime.setFormat(timeFormat);
		txtTime.getDatePicker().removeFromParent();
		txtTime.setVisible(showTime);
		this.add(txtTime);
	}

	public Date getDate() {
		Date date = txtDate.getValue();
		if(date == null){
			return null;
		}
		Date time = txtTime.getValue();
		String dateTime = defaultDateFormat.format(date) + " " + defaultTimeFormat.format(time);
		return dateTimeFormat.parse(dateTime);
	}

	public void setValue(Date d) {
		txtDate.setValue(d);
		txtTime.setValue(d);
	}

	public void setEnabled(boolean b) {
		txtDate.setEnabled(b);
		txtTime.setEnabled(b);
	}
}
