package org.androidcare.android.preferences;

import java.sql.SQLException;
import java.util.List;

import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.reminders.Reminder;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ReminderList extends ListActivity {

    private DatabaseHelper databaseHelper = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        try {
            List<Reminder> reminders = getHelper().getReminderDao().queryForAll();
            this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, reminders));
        }
        catch (SQLException e) {
            throw new RuntimeException("Could not read the reminders from the local database", e);
        }
    }
    
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
