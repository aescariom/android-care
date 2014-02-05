package org.androidcare.web.client.widgets;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.Date;

public class TimeBox extends HorizontalPanel {

    private static final DateTimeFormat defaultTimeFormat = DateTimeFormat.getFormat("HH:mm");
    private static final DateBox.Format timeFormat = new DateBox.DefaultFormat(defaultTimeFormat);
    private DateBox timeBox = new DateBox();

    public TimeBox() {
        super();
        init();
    }

    private void init() {
        timeBox.setFormat(timeFormat);
        timeBox.getDatePicker().removeFromParent();
        this.add(timeBox);
    }

    public void setValue(Date date) {
        timeBox.setValue(date);
    }

    public Date getValue() {
        return timeBox.getValue();
    }

}
