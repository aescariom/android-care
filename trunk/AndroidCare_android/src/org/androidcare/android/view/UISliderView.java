package org.androidcare.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.androidcare.android.R;
import org.androidcare.android.widget.SlideButton;

import java.io.Serializable;

public class UISliderView extends RelativeLayout {

    private final String TAG = this.getClass().getName();
    protected SlideButton sbtnUnlock;
    protected long sleepSoundTime = 500;
    protected TextView lblTitle;
    protected static final long TOO_MUCH_TIME_MAKING_SOUND = 2*60*1000; //2 min
    protected static final long TOO_MUCH_TIME_VIBRATING = 2*60*1000; //2 min
    protected PlaySoundTask playSoundTask;
    protected VibrationTask vibrationTask;
    private Activity activity;
    private boolean shouldFire = true;

    public UISliderView(final Activity activity, final String type, final Serializable displayable) {
        super(activity.getApplicationContext());

        this.activity = activity;
        playSoundTask = new PlaySoundTask();
        vibrationTask = new VibrationTask();

        inflate(activity, R.layout.basic_reminder_ui_sliders, this);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lblTitle = (TextView) findViewById(R.id.txtReminderTitlePreview);

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
                closeWindow(type, displayable);
                shouldFire = false;
            }
        });

        // Noise + vibration
        playSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        vibrate(1500);

        if ("alarm".equals(type)) {
            new CountDownTimer(30 * 1000, 1 * 1000) {
                public void onTick(long millisUntilFinished) {
                    lblTitle.setText(String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    if (shouldFire) {
                        closeWindow(type, displayable);
                    }
                }

            }.start();
        }
    }

    private void closeWindow(String type, Serializable displayable) {
        cancelVibrationAndSound();
        FrameLayout unlockLayout = (FrameLayout)findViewById(R.id.unlockLayout);

        unlockLayout.setVisibility(INVISIBLE);

        Intent intent = new Intent(getContext(), SpecificWarningActivityLauncher.class);
        intent.putExtra("type", type);
        intent.putExtra("displayable", displayable);

        Log.d(TAG, "Starting specific window once slided.");

        activity.getApplicationContext().sendBroadcast(intent);
    }

    protected void cancelVibrationAndSound() {
        playSoundTask.cancel(false);
        vibrationTask.cancel(false);
        Log.d(TAG, "Vibration and sound cancelled");
    }

    private class PlaySoundTask extends AsyncTask<Uri, Void, Void> {
        private int timeMakingSound=0;
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
                    mMediaPlayer.start();
                    while(!isCancelled() && timeMakingSound < TOO_MUCH_TIME_MAKING_SOUND){
                        Thread.sleep(sleepSoundTime);
                        timeMakingSound+=sleepSoundTime;
                    }
                    mMediaPlayer.stop();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Could not play the sound", e);
            }
            return null;
        }
    }

    private class VibrationTask extends AsyncTask<Integer, Void, Void> {
        private int timeVibrating=0;

        @Override
        protected Void doInBackground(Integer... params) {
            int timeOfEachVibration = params[0];

            // Get instance of Vibrator from current Context
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 'length' milliseconds
            try {
                while(!isCancelled() && timeVibrating < TOO_MUCH_TIME_VIBRATING){
                    boolean finish = activity.isFinishing();
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

    protected void playSound(Uri soundUri) {
        playSoundTask.execute(soundUri);
    }

    protected void vibrate(int length){
        vibrationTask.execute(length);
    }

}

