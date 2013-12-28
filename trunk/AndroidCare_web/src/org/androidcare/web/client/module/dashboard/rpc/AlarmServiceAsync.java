package org.androidcare.web.client.module.dashboard.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.androidcare.web.shared.persistent.Alarm;

import java.util.List;

public interface AlarmServiceAsync {
    void getActiveAlarms(AsyncCallback<List<Alarm>> async);

    void saveAlarm(Alarm alarm, AsyncCallback<Void> async);
    void deleteAlarm(Alarm alarm, AsyncCallback<Boolean> async);
}
