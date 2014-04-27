package org.androidcare.android.service.extra_tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;

public class VibrationGenerator extends AsyncTask<Integer, Void, Void> {

    private Activity context;
    private int timeVibrating=0;
    protected static final long TOO_MUCH_TIME_VIBRATING = 2*60*1000; //2 min

    public VibrationGenerator (Activity context) {
        this.context = context;
    }

    @Override
    public Void doInBackground(Integer... params) {
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