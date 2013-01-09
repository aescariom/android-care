package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.reminders.ReminderStatusCode;
import org.androidcare.android.service.ConnectionServiceBroadcastReceiver;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.reminders.ReminderLogMessage;
import org.androidcare.android.service.reminders.ReminderServiceBroadcastReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

public abstract class UIReminderView extends RelativeLayout {

    protected Reminder reminder;

    public UIReminderView(Context context, Reminder reminder) {
        super(context);
        this.reminder = reminder;
        reschedule(reminder);
    }

    public void performed() {
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DONE));
    }

    public void notPerformed() {
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_IGNORED));
    }

    public void delayed() {
    }

    public void displayed() {
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.ALERT_DISPLAYED));
    }

    public void finish() {
        Activity parent = (Activity) getContext();
        parent.finish();
    }

    protected Window getWindow() {
        Activity parent = (Activity) getContext();
        return parent.getWindow();
    }

    protected void reschedule(Reminder reminderr) {
        Intent intent = new Intent(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);
        intent.putExtra(ReminderServiceBroadcastReceiver.EXTRA_REMINDER, reminderr);
        this.getContext().sendBroadcast(intent);
    }

    protected void postData(Message message) {
        Intent intent = new Intent(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);
        intent.putExtra(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE, message);
        this.getContext().sendBroadcast(intent);
    }

    protected void playSound(Uri soundUri) {        
        try {
            Context context = getContext();
            // 1 - getting the sound
            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, soundUri);
            // 2 - setting up the audio manager
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            // 3 - playing the sound
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        }
        catch (Exception e) { // we must catch the exception
            Log.i("ReminderReceiver", "Could not play the sound when reminder was received");
            e.printStackTrace();
        }
    }
    
    protected void vibrate(int length){
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
         
        // Vibrate for 'length' milliseconds
        v.vibrate(length);
    }
}
