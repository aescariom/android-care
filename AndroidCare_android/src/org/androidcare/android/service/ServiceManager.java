package org.androidcare.android.service;

import org.androidcare.android.service.location.LocationService;
import org.androidcare.android.service.reminders.ReminderService;

import android.content.Context;
import android.content.Intent;

public abstract class ServiceManager {

    public static void startAllServices(Context context){
        context.startService(new Intent(context, ConnectionService.class));
        context.startService(new Intent(context, LocationService.class));
        context.startService(new Intent(context, ReminderService.class));
    }
    
    public static void stopAllServices(Context context){
        context.stopService(new Intent(context, LocationService.class));
        context.stopService(new Intent(context, ReminderService.class));
        context.stopService(new Intent(context, ConnectionService.class));
    }
    
    public static void stopSecondaryServices(Context context){
        context.stopService(new Intent(context, LocationService.class));
        context.stopService(new Intent(context, ReminderService.class));
    }
}
