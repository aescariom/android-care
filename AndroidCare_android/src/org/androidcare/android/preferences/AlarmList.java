package org.androidcare.android.preferences;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.alarms.AlarmSeverity;
import org.androidcare.android.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmList extends ListActivity {

    private DatabaseHelper databaseHelper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            List<Alarm> alarms = getHelper().getAlarmDao().queryForAll();
            alarms = new ArrayList<Alarm>();
            alarms.add(new Alarm(1, AlarmSeverity.WARNING, false, false, false, false, "678803604", "alvarodelrosal@gmail.com",
                    new Date(), new Date(), false, false, 0.0 ,0.0));
            this.setListAdapter(new ArrayAdapter<Alarm>(this, android.R.layout.simple_expandable_list_item_1, alarms));
        }
        catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Could not read the alarms from the local database", e);
            this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
                    new String[]{"Could not read the alarms from the local database"}));
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
