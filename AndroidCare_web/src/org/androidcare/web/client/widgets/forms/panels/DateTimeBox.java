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

package org.androidcare.web.client.widgets.forms.panels;

import java.util.Date;

import org.androidcare.web.client.LocalizedConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.Format;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public class DateTimeBox extends HorizontalPanel {

	private static final Format dateFormat = new DateBox.DefaultFormat(DateTimeFormat.getFormat("d/M/y"));
	private static final Format timeFormat = new DateBox.DefaultFormat(DateTimeFormat.getFormat("HH:mm"));

	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

	protected DateBox txtDate;
	protected Label lblAt;
	protected DateBox txtTime;
	protected boolean showTime;
	
	/**
	 * 
	 */
	public DateTimeBox(){
		super();
		init(true);
	}
	
	/**
	 * 
	 * @param time
	 */
	public DateTimeBox(boolean time){
		super();
		
		init(time);
	}

	/**
	 * 
	 * @param time
	 */
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

	/**
	 * 
	 * @return
	 */
	public Date getDate() {
		Date date = txtDate.getValue();
		if(date == null){
			return null;
		}
		Date time = txtTime.getValue();
		date.setHours(time.getHours());
		date.setMinutes(time.getMinutes());
		return date;
	}

	/**
	 * 
	 * @param d
	 */
	public void setValue(Date d) {
		txtDate.setValue(d);
		txtTime.setValue(d);
	}

	/**
	 * 
	 * @param b
	 */
	public void setEnabled(boolean b) {
		txtDate.setEnabled(b);
		txtTime.setEnabled(b);
	}
}
