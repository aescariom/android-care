package org.androidcare.android.view.Alarm;

import android.content.Context;
import android.widget.RelativeLayout;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmView extends RelativeLayout{


    private final AlarmService alarm;

    public UIAlarmView(Context context, AlarmService alarm) {
        super(context);
        this.alarm = alarm;
    }

}
