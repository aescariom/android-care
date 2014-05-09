package org.androidcare.android.view.alarm;

import android.app.Activity;
import android.content.Context;
import android.media.RingtoneManager;
import android.widget.RelativeLayout;
import org.androidcare.android.service.alarms.AlarmService;
import org.androidcare.android.service.extra_tasks.SoundPlayer;
import org.androidcare.android.service.extra_tasks.VibrationGenerator;

public class UIAlarmView extends RelativeLayout{

//Comentario no est�s haciendo nada con la alarma �es esto un trabajo en proceso?
    private final AlarmService alarm;
    protected static final long TOO_MUCH_TIME_VIBRATING = 2*60*1000; //2 min
    protected SoundPlayer playSoundTask;
    protected VibrationGenerator vibrationTask;

    public UIAlarmView(Context context, AlarmService alarm) {
        super(context);
        this.alarm = alarm;

        vibrationTask = new VibrationGenerator((Activity) getContext());
        playSoundTask = new SoundPlayer((Activity) getContext());
    }

    protected void stopPlayingSoundAndVibration() {
        playSoundTask.cancel(false);
        vibrationTask.cancel(false);
    }

    protected void startPlayingSoundAndVibration() {
        playSoundTask.execute(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        //vibrationTask.doInBackground(1500);
    }
}
