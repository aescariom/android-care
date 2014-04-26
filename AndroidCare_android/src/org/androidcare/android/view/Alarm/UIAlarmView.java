package org.androidcare.android.view.Alarm;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.widget.RelativeLayout;
import org.androidcare.android.service.alarms.AlarmService;

public class UIAlarmView extends RelativeLayout{

//Comentario no estás haciendo nada con la alarma ¿es esto un trabajo en proceso?
    private final AlarmService alarm;
    protected long sleepSoundTime = 500;
    protected static final long TOO_MUCH_TIME_MAKING_SOUND = 2*60*1000; //2 min
    protected static final long TOO_MUCH_TIME_VIBRATING = 2*60*1000; //2 min
    protected PlaySoundTask playSoundTask;
    protected VibrationTask vibrationTask;

    public UIAlarmView(Context context, AlarmService alarm) {
        super(context);
        this.alarm = alarm;

        vibrationTask = new VibrationTask();
        playSoundTask = new PlaySoundTask();
    }

    protected void stopPlayingSoundAndVibration() {
        playSoundTask.cancel(false);
        vibrationTask.cancel(false);
    }

    protected void startPlayingSoundAndVibration() {
        playSoundTask.execute(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        //vibrationTask.doInBackground(1500);
    }
//Comentario mira que eres chapuzas. Haces copiar y pegar el código de Alejandro.
    //Convierte estas  dos clases en publicas  y tales un  constructor
    //al que se le pasen los parámetros que hagan falta y usarlas en tu código
    //Y en el de Alejandro en vez de copiar y pegar
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
            return null;
        }
    }

}
