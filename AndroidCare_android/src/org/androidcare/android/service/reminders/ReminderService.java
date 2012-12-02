package org.androidcare.android.service.reminders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.location.GeoMessage;
import org.androidcare.android.view.ReminderReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Alejandro Escario Méndez
 * 
 * 
 */
// @comentario Romper este servicio en dos; uno que se encargue de los reminders y otro de la localizaci�n;
// el de la localizaci�n va al paquete location
public class ReminderService extends ConnectionService {

    private static final int REMINDER_REQUEST_CODE = 0;
    private final String tag = this.getClass().getName();

    // communcation
    // @comentario �para qu� es esta lista? Nunca se mete nada en ella.
    private final List<Intent> reminderIntents = new ArrayList<Intent>();
    private final IBinder binder = new ReminderServiceBinder();

    // intent broadcast receiver
    private ReminderServiceBroadcastReceiver reminderServiceReceiver = new ReminderServiceBroadcastReceiver(
            this);
    private IntentFilter filter = new IntentFilter(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);

    // location
    private LocationManager locationManager;
    private int minSeconds = 300000; // 5 min
    private int minDistance = 20; // 20 meters
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            ReminderService.this.pushLowPriorityMessage(new GeoMessage(location));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
  //@Comentario deber�amos usar onStartCommand; Este m�todo est� deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(tag, "Service started");

        this.pushMessage(new GetRemindersMessage(this));

        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
      //@Comentario no tengo claro que LocationManager.NETWORK_PROVIDER sea la mejor opci�n para
        //nuestro problema  en todos los escenarios; creo que habr�a que definir m�s din�micamente
        //el mecanismo de localizaci�n a utilizar a trav�s de un Criteria, y que deber�amos estar
        //monitorizar no cambios  en proveedores de mecanismos de localizaci�n y reaccionando a ellos
        //es decir, reaccionar a que active/desactive la Wifi o el GPS
        //pero con esto ponte una vez que hayamos roto esta clase en dos
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.minSeconds,
                this.minDistance, locationListener);

        registerReceiver(reminderServiceReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reminderServiceReceiver);
        // @comentario �Aqu� habr�a que haber a�adido tambi�n la l�nea que he puesto debajo?
        // locationManager.removeUpdates(locationListener)
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

        Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
        intent.setData(Uri.parse("androidCare://" + reminder.getId() + ".- " + reminder.getTitle()));

        intent.putExtra("reminder", reminder);
        PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

        Log.i(tag, "Reminder scheduled: " + reminder.getTitle() + " @ " + cal.getTime().toString());
    }

    // @comentario este m�todo nunca hace nada porque nunca se mete nada en la lista!!
    // �Te has olvidado de hacer algo?
    private void cancelAllReminders() {
        for (Intent intent : this.reminderIntents) {
            PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE,
                    intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sender.cancel();
            this.reminderIntents.remove(intent);
        }
    }
  //@Comentario creo que no estamos usando binding para nada �no? Si es as�, borrar.
    public IBinder getBinder() {
        return binder;
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
