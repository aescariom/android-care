package org.androidcare.android.service.reminders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.view.ReminderReceiver;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends OrmLiteBaseService<DatabaseHelper> {

    private static final int REMINDER_REQUEST_CODE = 0;
    private final String tag = this.getClass().getName();

    // Communication
    private final IBinder binder = new ReminderServiceBinder();

    // intent broadcast receiver
    private ReminderServiceBroadcastReceiver reminderServiceReceiver = 
                                                                new ReminderServiceBroadcastReceiver(this);
    private IntentFilter reminderServiceBroadcastFilter = 
                                new IntentFilter(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(tag, "Reminder service started");

        registerReceiver(reminderServiceReceiver, reminderServiceBroadcastFilter);

        GetRemindersMessage.setReminderService(this);
        this.scheduleFromDatabase();
        refreshReminders();
        
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reminderServiceReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    private void refreshReminders() {
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), RefreshRemindersReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
    
    public void scheduleFromDatabase(){
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
        cancelAllReminders();
        updateDatabase(reminders);
        for (Reminder r : reminders) {
            this.schedule(r);
        }
    }

    public void schedule(Reminder reminder) {
        Calendar cal = Calendar.getInstance();
        Date date = reminder.getNextTimeLapse(cal.getTime());
        if (date == null) {
            return;
        }
        cal.setTime(date);
        
        scheduleTo(reminder, cal);
    }
    
    public void scheduleTo(Reminder reminder, Calendar cal) {
        Intent intent = createReminderIntent(reminder);
        PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, /*cal.getTimeInMillis()*/Calendar.getInstance().getTimeInMillis() + 10000, sender);
        
        Log.i(tag, "Reminder scheduled: " + reminder.getTitle() + " @ " + cal.getTime().toString());
    }
    
    private Intent createReminderIntent(Reminder reminder) {
        Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
        intent.setData(Uri.parse("androidCare://" + reminder.getId() + ".- " + reminder.getTitle()));

        intent.putExtra("reminder", reminder);
        
        return intent;
    }

    private void updateDatabase(Reminder[] reminders){
        try {            
            getHelper().truncateReminderTable();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete the old reminders", e);
        }
        for (Reminder r : reminders) {
            try {
                getHelper().getReminderDao().createIfNotExists(r);
            }catch (SQLException e) {
                Log.e(tag, "Could not insert the reminder: " + r + " -> " + e.toString());
            }
        }
    }

    private void cancelAllReminders() {
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

    /**
     * this class will allow us to connect activities with the running instance of this service
     */
    public class ReminderServiceBinder extends Binder {
        public ReminderService getService() {
            return ReminderService.this;
        }
    }
}
