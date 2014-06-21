package org.androidcare.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import org.androidcare.android.R;
import org.androidcare.android.preferences.CalibrationWindow;

public class NoCalibrationFoundWindow extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context parentContext = this;

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.no_calibration_found);
        alertDialog.setMessage(getResources().getString(R.string.calibrate_felloff_sensor));
        alertDialog.setButton(getResources().getString(R.string.calibrate),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(parentContext, CalibrationWindow.class);
                        parentContext.startActivity(intent);
                    }
                });

        alertDialog.setIcon(R.drawable.notification_icon);
        alertDialog.show();
    }
    
}
