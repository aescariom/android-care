package org.androidcare.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import org.androidcare.android.R;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.reminders.ReminderStatusCode;
import org.androidcare.android.service.reminders.ReminderLogMessage;
import org.androidcare.android.widget.SlideButton;

import java.io.File;

public class UISliderView extends RelativeLayout {

    private final String TAG = this.getClass().getName();
    protected SlideButton sbtnUnlock;
    protected long sleepSoundTime = 500;
    protected long sleepVibrationTime = 1000;
    protected static final long TOO_MUCH_TIME_MAKING_SOUND = 2*60*1000; //2 min
    protected static final long TOO_MUCH_TIME_VIBRATING = 2*60*1000; //2 min
    protected PlaySoundTask playSoundTask;
    protected VibrationTask vibrationTask;

    public UISliderView(Activity activity, Alarm alarm) {
        inflate(activity, R.layout.basic_reminder_ui_sliders, this);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 2 - turning on the screen, display the activity over the locked screen, keeping the screen on,
        // and unlocking the keyboard
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        sbtnUnlock = (SlideButton) findViewById(R.id.sbtnUnlock);
        sbtnUnlock.setBackgroundResource(R.drawable.arrow_down);
        AnimationDrawable frameAnimation = (AnimationDrawable) sbtnUnlock.getBackground();
        frameAnimation.start();
        sbtnUnlock.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                cancelVibrationAndSound();
                RelativeLayout rootLayout = (RelativeLayout)findViewById(R.id.RootLayout);
                FrameLayout unlockLayout = (FrameLayout)findViewById(R.id.unlockLayout);

                rootLayout.setVisibility(VISIBLE);
                unlockLayout.setVisibility(INVISIBLE);
            }
        });

        // Noise + vibration
        playSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        vibrate(1500);
    }

    protected void cancelVibrationAndSound() {
        playSoundTask.cancel(false);
        vibrationTask.cancel(false);
    }

    private class PlaySoundTask extends AsyncTask<Uri, Void, Void> {
        private int timeMakingSound=0;
        @Override
        protected Void doInBackground(Uri... params) {
            try {
                Activity context = (Activity)getContext();

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
                    mMediaPlayer.start();
                    while(!isCancelled() && timeMakingSound < TOO_MUCH_TIME_MAKING_SOUND){
                        boolean finish = context.isFinishing();
                        if(finish){
                            break;
                        }
                        Thread.sleep(sleepSoundTime);
                        timeMakingSound+=sleepSoundTime;
                    }
                    mMediaPlayer.stop();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Could not play the sound when reminder was received", e);
            }
            return null;
        }
    }

    private class VibrationTask extends AsyncTask<Integer, Void, Void> {
        private int timeVibrating=0;

        @Override
        protected Void doInBackground(Integer... params) {
            Activity context = (Activity)getContext();
            int timeOfEachVibration = params[0];

            // Get instance of Vibrator from current Context
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 'length' milliseconds
            try {
                while(!isCancelled() && timeVibrating < TOO_MUCH_TIME_VIBRATING){
                    boolean finish = context.isFinishing();
                    if(finish){
                        break;
                    }
                    vibrator.vibrate(timeOfEachVibration);
                    Thread.sleep(2*timeOfEachVibration);
                    timeVibrating +=3*timeOfEachVibration;
                }
                vibrator.cancel();
            }catch (InterruptedException e) {
                Log.e(TAG, "Could not play the sound when reminder was received", e);
            }
            return null;
        }
    }



}

