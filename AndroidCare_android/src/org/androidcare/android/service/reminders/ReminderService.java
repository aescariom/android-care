package org.androidcare.android.service.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.view.ReminderReceiver;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderService extends Service {

    private static final int REMINDER_REQUEST_CODE = 0;
    private final String TAG = this.getClass().getName();

    // intent broadcast receiver
    private ReminderServiceBroadcastReceiver reminderServiceReceiver = 
                                                                new ReminderServiceBroadcastReceiver(this);
    private IntentFilter reminderServiceBroadcastFilter = 
                                new IntentFilter(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);

    private DatabaseHelper databaseHelper = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ReminderService", "onStartCommand()");
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Reminder service started");

        registerReceiver(reminderServiceReceiver, reminderServiceBroadcastFilter);

        GetRemindersMessage.setReminderService(this);
        this.scheduleFromDatabase();
        refreshReminders();
        
        return result;
    }

    @Override
    public void onDestroy() {
        Log.e("ReminderService", "onDestroy()");
        super.onDestroy();
        Log.d(TAG, "Stopping service");
        cancelAllReminders();
        unregisterReceiver(reminderServiceReceiver);
        closeDatabaseConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void refreshReminders() {
        Log.e("ReminderService", "refreshReminders()");
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), RefreshRemindersReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
    
    public void scheduleFromDatabase(){
        Log.e("ReminderService", "scheduleFromDatabase()");
        cancelAllReminders();
        try {
            List<Reminder> reminders = getHelper().getReminderDao().queryForAll();
            for (Reminder r : reminders) {
                this.schedule(r);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Could not read the reminders from the local database", e);
        }
    }

    public void schedule(Reminder[] reminders) {
        Log.e("ReminderService", "schedule()");
        cancelAllReminders();
        updateDatabase(reminders);
        for (Reminder r : reminders) {
            this.schedule(r);
        }
    }

    public void schedule(Reminder reminder) {
        Log.e("ReminderService", "schedule()");
        Calendar cal = Calendar.getInstance();
        Date date = reminder.getNextTimeLapse(cal.getTime());
        if (date == null) {
            return;
        }
        cal.setTime(date);

        scheduleTo(reminder, cal);
    }
    
    public void scheduleTo(Reminder reminder, Calendar cal) {
        Log.e("ReminderService", "scheduleTo()");
        Intent intent = createReminderIntent(reminder);
        PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()/*Calendar.getInstance().getTimeInMillis() + 30000*/, sender);
        
        Log.i(TAG, "Reminder scheduled: " + reminder.getTitle() + " @ " + cal.getTime().toString());
    }
    
    private Intent createReminderIntent(Reminder reminder) {
        Log.e("ReminderService", "createReminderIntent()");
        Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
        intent.setData(Uri.parse("androidCare://" + reminder.getId() + ".- " + reminder.getTitle()));

        intent.putExtra("reminder", reminder);
        
        return intent;
    }

    private void updateDatabase(Reminder[] reminders){
        Log.e("ReminderService", "updateDatabase()");
        try {            
            getHelper().truncateReminderTable();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete the old reminders", e);
        }
        for (Reminder r : reminders) {
            try {
                getHelper().getReminderDao().createIfNotExists(r);
            }catch (SQLException e) {
                Log.e(TAG, "Could not insert the reminder: " + r + " -> " + e.toString());
            }
        }
    }

    private void cancelAllReminders() {
        Log.e("ReminderService", "cancelAllReminders()");
        List<Reminder> all;
        try {
            all = getHelper().getReminderDao().queryForAll();
            for (Reminder r : all) {
                Intent intent = createReminderIntent(r);
                PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                        intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sender.cancel();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private DatabaseHelper getHelper() {
        Log.e("ReminderService", "getHelper()");
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
}
