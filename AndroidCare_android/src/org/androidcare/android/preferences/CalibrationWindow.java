package org.androidcare.android.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import org.androidcare.android.R;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.AnySensorListener;
import org.androidcare.android.service.AnySensorRetriever;
import org.androidcare.android.service.ServiceManager;
import org.androidcare.android.service.alarms.FellOffAlgorithm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CalibrationWindow extends Activity implements AnySensorListener {

    private final String TAG = this.getClass().getName();
    private List<Integer> sensorValues = new ArrayList();
    private CalibrationWindow mainWindow = this;
    private boolean continueRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button startStopButton = (Button) findViewById(R.id.startStopButton);

        startStopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sensorValues.size() == 0) {
                    ServiceManager.stopSecondaryServices(mainWindow);
                    continueRunning = true;
                    new AnySensorRetriever(mainWindow, mainWindow, null, Sensor.TYPE_LINEAR_ACCELERATION);
                } else {
                    continueRunning = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onChangeSensor(float[] values, PowerManager.WakeLock lock, AnySensorRetriever retriever) {
        if (continueRunning) {
            sensorValues.add(FellOffAlgorithm.run(values));
        } else {
            retriever.unregister();
            int bigThreshold = getTheBiggestValue();
            Log.d(TAG, "The biggest one is: " + bigThreshold);

            try {
                DatabaseHelper helper = new DatabaseHelper(this);
                helper.setFellOffThreshold(bigThreshold);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String sensorCalibrated = getResources().getString(R.string.sensor_calibrated);
            String restartingServices = getResources().getString(R.string.restarting_service);
            Toast.makeText(this, sensorCalibrated + ". " + restartingServices, Toast.LENGTH_SHORT).show();

            ServiceManager.startAllServices(this);
        }
    }

    public int getTheBiggestValue() {
        int max = 0;
        for(int value : sensorValues) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public Context getContext() {
        return this;
    }
}