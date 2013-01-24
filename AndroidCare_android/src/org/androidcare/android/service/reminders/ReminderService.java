package org.androidcare.android.service.reminders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.view.ReminderReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends ConnectionService {

    private static final int REMINDER_REQUEST_CODE = 0;
    private final String tag = this.getClass().getName();

    // communcation
    // @comentario ¿para qué es esta lista? Nunca se mete nada en ella.
    private final List<Intent> reminderIntents = new ArrayList<Intent>();
    private final IBinder binder = new ReminderServiceBinder();

    // intent broadcast receiver
    private ReminderServiceBroadcastReceiver reminderServiceReceiver = new ReminderServiceBroadcastReceiver(
            this);
    private IntentFilter filter = new IntentFilter(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(tag, "Reminder service started");

        registerReceiver(reminderServiceReceiver, filter);

        this.pushMessage(new GetRemindersMessage(this));
        
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

    public void schedule(Reminder[] reminders) {
        cancelAllReminders();
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

        Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
        intent.setData(Uri.parse("androidCare://" + reminder.getId() + ".- " + reminder.getTitle()));

        intent.putExtra("reminder", reminder);
        PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        
        Log.i(tag, "Reminder scheduled: " + reminder.getTitle() + " @ " + cal.getTime().toString());
    }

    // @comentario este método nunca hace nada porque nunca se mete nada en la lista!!
    // ¿Te has olvidado de hacer algo?
    private void cancelAllReminders() {
        for (Intent intent : this.reminderIntents) {
            PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                    intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sender.cancel();
            this.reminderIntents.remove(intent);
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
