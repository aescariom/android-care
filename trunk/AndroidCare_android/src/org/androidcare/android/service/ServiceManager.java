package org.androidcare.android.service;

import org.androidcare.android.service.location.LocationService;
import org.androidcare.android.service.reminders.ReminderService;

import android.content.Context;
import android.content.Intent;

public abstract class ServiceManager {

    public static void startAllServices(Context context){   
        Intent i = new Intent(context, ConnectionService.class);
        context.startService(i);

        i = new Intent(context, ReminderService.class);
        context.startService(i);
        
        i = new Intent(context, LocationService.class);
        context.startService(i);
    }
}
