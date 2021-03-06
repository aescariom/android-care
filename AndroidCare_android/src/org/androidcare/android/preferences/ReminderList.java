package org.androidcare.android.preferences;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.reminders.Reminder;

import java.sql.SQLException;
import java.util.List;

public class ReminderList extends ListActivity {

    private DatabaseHelper databaseHelper = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        try {
            List<Reminder> reminders = getHelper().getReminderDao().queryForAll();
            this.setListAdapter(new ArrayAdapter<Reminder>(this, android.R.layout.simple_expandable_list_item_1, reminders));
        }
        catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Could not read the reminders from the local database", e);
            this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
                    new String[]{"Could not read the reminders from the local database"}));
        }
    }
    
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
