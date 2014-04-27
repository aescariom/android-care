//Comentario �c�mo me ha dolido ver el nombre del paquete con may�scula!
package org.androidcare.android.view.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.androidcare.android.service.alarms.AlarmService;

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
            //esta l�nea deber�a ser
            //Log.e(TAG, "Alarm Service PendingIntent cancelled: " + e.getMessage(), e);
            //donde tag deber�a ser un atributo de la clase
            //private static final String TAG = LocationService.class.getName();
            //y usa el nivel de log adecuado: Log.e, Log.i, Log.w....
            //se agradece mucho hacer todo esto cuando hay que depurar algo
            e.printStackTrace();
        }

    }

}
