package org.androidcare.android.view.Alarm;

import android.app.Activity;
import android.os.Bundle;
import org.androidcare.android.service.alarms.AlarmService;

public class AlarmWindowReceiver extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        AlarmService alarm = (AlarmService) bundle.getSerializable("alarm_service");
        UIAlarmView view = UIAlarmViewFactory.createUIAlarmView(this, alarm);
        this.setContentView(view);
    }
}