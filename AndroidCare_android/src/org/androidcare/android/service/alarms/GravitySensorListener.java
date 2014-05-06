package org.androidcare.android.service.alarms;

import android.content.Context;
import android.os.PowerManager;
import org.androidcare.android.service.GravitySensorRetriever;

public interface GravitySensorListener {

    public void onChangeSensor(float[] values, PowerManager.WakeLock lock, GravitySensorRetriever retriever);
    public Context getContext();

}
