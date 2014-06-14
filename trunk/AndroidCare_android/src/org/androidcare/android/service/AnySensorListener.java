package org.androidcare.android.service;

import android.content.Context;
import android.os.PowerManager;
import org.androidcare.android.service.AnySensorRetriever;

public interface AnySensorListener {

    public void onChangeSensor(float[] values, PowerManager.WakeLock lock, AnySensorRetriever retriever);
    public Context getContext();

}
