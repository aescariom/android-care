package org.androidcare.android.view.alarm;

import android.app.Activity;
import android.os.Bundle;
import org.androidcare.android.service.alarms.AlarmService;

public class AlarmWindowReceiver extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        AlarmService alarmService = (AlarmService) bundle.getSerializable("alarm");
        UIAlarmView view = UIAlarmViewFactory.createUIAlarmView(this, alarmService);
        this.setContentView(view);
    }
}