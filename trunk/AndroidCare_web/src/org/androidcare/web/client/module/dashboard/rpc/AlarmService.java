package org.androidcare.web.client.module.dashboard.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.androidcare.web.shared.persistent.Alarm;

import java.util.List;

@RemoteServiceRelativePath("alarm")
public interface AlarmService extends RemoteService {
    List<Alarm> getActiveAlarms();
    void saveAlarm(Alarm alarm);

    boolean deleteAlarm(Alarm alarm);
}
