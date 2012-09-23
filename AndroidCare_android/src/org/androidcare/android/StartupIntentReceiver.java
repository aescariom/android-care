package org.androidcare.android;
import org.androidcare.android.service.LocalService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupIntentReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
	    //1 - Starting the service that will manage everything
		Intent i = new Intent(context, LocalService.class);
		context.startService(i);
	}
}