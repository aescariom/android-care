package org.androidcare.android.view;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.androidcare.android.R;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.reminders.ReminderStatusCode;
import org.androidcare.android.service.reminders.ReminderLogMessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UIReminderBasicView extends UIReminderView {

    protected Button btnPerformed;
    protected Button btnNotPerformed;
    protected Button btnDelayed;
    protected TextView lblTitle;
    protected TextView lblDescription;
    protected ImageView imgResizable;

    public UIReminderBasicView(ReminderDialogReceiver activity, Reminder reminder) {
        super(activity, reminder);

        inflate(activity, R.layout.basic_reminder_ui, this);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 2 - turning on the screen, display the activity over the locked screen, keeping the screen on,
        // and unlocking the keyboard
        /*getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/

        btnPerformed = (Button) findViewById(R.id.btnOk);
        btnPerformed.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                performed();
                finish();
            }
        });
        btnPerformed.setBackgroundColor(Color.GREEN);
        
        btnDelayed = (Button) findViewById(R.id.btnDelay);
        btnDelayed.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showDelayModal(v);
            }
        });
        btnDelayed.setBackgroundColor(Color.YELLOW);

        btnNotPerformed = (Button) findViewById(R.id.btnCancel);
        btnNotPerformed.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                notPerformed();
                finish();
            }
        });
        btnNotPerformed.setBackgroundColor(Color.RED);

        lblTitle = (TextView) findViewById(R.id.txtReminderTitle);
        lblTitle.setText(this.reminder.getTitle());
        lblDescription = (TextView) findViewById(R.id.txtReminderDescription);
        lblDescription.setText(this.reminder.getDescription());
        imgResizable = (ImageView) findViewById(R.id.imgReminder);
        
        if(reminder.getBlobKey() != null && reminder.getBlobKey() != ""){
            String url = "http://androidcare2.appspot.com/api/reminderPhoto?id=" + reminder.getBlobKey();
            //url="http://www.dipler.org/wp-content/themes/BnB2/images/logo.png";
            new DownloadImageTask(imgResizable).execute(url);
        }
        
        // Noise + vibration
        playSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        vibrate(1500);

        // 5 - notifying
        postData(new ReminderLogMessage(reminder, ReminderStatusCode.REMINDER_DISPLAYED));
    }
    
    public void showDelayModal(View v){
        final CharSequence[] items = {
                "3 min",
                "5 min",
                "10 min",
                "15 min",
                "30 min",
                "60 min",
                };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("titulo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                int min = Integer.parseInt(items[item].toString().split(" ")[0]);

                delayed(min * 60000);
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imgResizbl;

        public DownloadImageTask(ImageView bmImage) {
            this.imgResizbl = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(urldisplay);
                URLConnection connection = url.openConnection();
                connection.setUseCaches(true);
                bitmap = BitmapFactory.decodeStream((InputStream)connection.getContent());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imgResizbl.setImageBitmap(result);
        }
    }
}
