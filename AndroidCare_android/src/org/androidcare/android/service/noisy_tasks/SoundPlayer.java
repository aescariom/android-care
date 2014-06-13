package org.androidcare.android.service.noisy_tasks;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

public class SoundPlayer extends AsyncTask<Uri, Void, Void> {

    private Activity context;
    private int timeMakingSound=0;
    protected long sleepSoundTime = 500;
    protected static final long TOO_MUCH_TIME_MAKING_SOUND = 2*60*1000; //2 min

    public SoundPlayer(Activity context) {
        this.context = context;
    }

    @Override
    public Void doInBackground(Uri... params) {
        try {
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
            e.printStackTrace();
        }
        return null;
    }
}