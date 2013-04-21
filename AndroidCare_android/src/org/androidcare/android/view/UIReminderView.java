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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

public abstract class UIReminderView extends RelativeLayout {

    protected Reminder reminder;
    protected long sleepSoundTime = 5000;
    protected long sleepVibrationTime = 5000;
    protected PlaySoundTask playSoundTask;
    protected VibrationTask vibrationTask;

    public UIReminderView(ReminderDialogReceiver context, Reminder reminder) {
        super(context);
        this.reminder = reminder;
        playSoundTask = new PlaySoundTask();
        vibrationTask = new VibrationTask();
    }

    public void performed() {
        reschedule(reminder);
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.REMINDER_DONE));
    }

    public void notPerformed() {
        reschedule(reminder);
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.REMINDER_IGNORED));
    }

    public void delayed(int ms) {
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.REMINDER_DELAYED));
        reschedule(reminder, ms);
    }

    public void displayed() {
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.REMINDER_DISPLAYED));
    }

    public void finish() {
        playSoundTask.cancel(true);
        vibrationTask.cancel(true);
        
        Activity parent = (Activity) getContext();
        parent.finish();
    }

    protected Window getWindow() {
        Activity parent = (Activity) getContext();
        return parent.getWindow();
    }

    protected void reschedule(Reminder reminder) {
        reschedule(reminder, 0);
    }

    protected void reschedule(Reminder reminder, int ms) {
        Intent intent = new Intent(ReminderServiceBroadcastReceiver.ACTION_SCHEDULE_REMINDER);
        intent.putExtra(ReminderServiceBroadcastReceiver.EXTRA_REMINDER, reminder);
        intent.putExtra(ReminderServiceBroadcastReceiver.EXTRA_DELAY, ms);
        this.getContext().sendBroadcast(intent);
    }

    protected void postData(Message message) {
        Intent intent = new Intent(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);
        intent.putExtra(ConnectionServiceBroadcastReceiver.EXTRA_MESSAGE, message);
        this.getContext().sendBroadcast(intent);
    }

    protected void playSound(Uri soundUri) {        
        playSoundTask.execute(soundUri);
    }
    
    protected void vibrate(int length){
        vibrationTask.execute(length);
    }
    
    private class PlaySoundTask extends AsyncTask<Uri, Void, Void> {

        @Override
        protected Void doInBackground(Uri... params) {
            try {
                Context context = getContext();
                // 1 - getting the sound
                MediaPlayer mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(context, params[0]);
                // 2 - setting up the audio manager
                final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                // 3 - playing the sound
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
                    while(true){
                        mMediaPlayer.start();
                        Thread.sleep(sleepSoundTime);
                        if(sleepSoundTime < 120000){
                            sleepSoundTime += 5000;
                        }
                    }
                }
            }
            catch (Exception e) { // we must catch the exception
                Log.i("ReminderReceiver", "Could not play the sound when reminder was received");
                e.printStackTrace();
            }
            return null;
        }
    }
    
    private class VibrationTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
             
            // Vibrate for 'length' milliseconds
            try {
                while(true){
                    v.vibrate(params[0]);
                    Thread.sleep(sleepVibrationTime);
                    if(sleepVibrationTime < 120000){
                        sleepVibrationTime += 5000;
                    }
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
}
