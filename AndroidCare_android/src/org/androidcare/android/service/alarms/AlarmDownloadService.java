package org.androidcare.android.service.alarms;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

public class AlarmDownloadService extends Service {

    private final String TAG = this.getClass().getName();

    private DatabaseHelper databaseHelper = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Download alarms service started");

        GetAlarmsMessage.setAlarmDownloadService(this);
        
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stopping service");
        closeDatabaseConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public void addAlarmsToDatabase(List<Alarm> alarms){
        for (Alarm alarm : alarms) {
            try {
                getHelper().getAlarmDao().createIfNotExists(alarm);
            }catch (SQLException e) {
                Log.e(TAG, "Could not insert the alarm: " + alarm + " -> " + e.toString());
            }
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
    
    private void closeDatabaseConnection() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public class AlarmDownloadServiceBinder extends Binder {
        public AlarmDownloadService getService() {
            return AlarmDownloadService.this;
        }
    }

}
