package uspceu.logservice;

import android.content.Context;
import android.os.PowerManager;

public interface AnySensorListener {

    public void onChangeSensorAcl(float[] values, PowerManager.WakeLock lock, AnySensorRetriever retriever);
    public Context getContext();

}
