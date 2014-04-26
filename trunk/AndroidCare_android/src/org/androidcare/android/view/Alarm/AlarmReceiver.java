//Comentario ¡cómo me ha dolido ver el nombre del paquete con mayúscula!
package org.androidcare.android.view.Alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.androidcare.android.service.alarms.AlarmService;
import org.androidcare.android.service.location.LocationService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        AlarmService alarm = (AlarmService) bundle.getSerializable("alarm_service");
        displayAlarm(context, alarm);
    }

    private void displayAlarm(Context context, AlarmService alarm) {
        Intent intent = new Intent(context, AlarmWindowReceiver.class);
        intent.putExtra("alarm_service", alarm);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            //comentario haz log como Dios manda
            //esta línea debería ser
            //Log.e(TAG, "Alarm Service PendingIntent cancelled: " + e.getMessage(), e);
            //donde tag debería ser un atributo de la clase
            //private static final String TAG = LocationService.class.getName();
            //y usa el nivel de log adecuado: Log.e, Log.i, Log.w....
            //se agradece mucho hacer todo esto cuando hay que depurar algo
            e.printStackTrace();
        }

    }

}
