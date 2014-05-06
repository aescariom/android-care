package org.androidcare.android.service.alarms;

import android.content.Context;
import android.os.PowerManager;

public interface GravitySensorListener {

    public void onChangeSensor(float[] values, PowerManager.WakeLock lock);
    public Context getContext();

}
