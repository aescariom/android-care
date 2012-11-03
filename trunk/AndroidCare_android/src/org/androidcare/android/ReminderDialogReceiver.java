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

package org.androidcare.android;

import org.androidcare.android.util.Reminder;
import org.androidcare.android.view.UIReminderBasicView;
import org.androidcare.android.view.UIReminderBasicViewFactory;

import android.app.Activity;
import android.os.Bundle;

/**
 * An Alert dialog receiver will display the title and description of the alert.
 * It will also send (sometimes) information to the server
 * @author Alejandro Escario MŽndez
 *
 */
public class ReminderDialogReceiver extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//1 - getting relevant information
		Bundle b = getIntent().getExtras();
		Reminder reminder = (Reminder) b.getSerializable("reminder");
		
		//2 - setting the view
		UIReminderBasicViewFactory factory = new UIReminderBasicViewFactory();
		UIReminderBasicView view  = (UIReminderBasicView) factory.createView(this, reminder);
		
		this.setContentView(view);
	}
}