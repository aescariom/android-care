package uspceu.logservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {
	Intent logServiceWithLock;
    Intent logServiceWithoutLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button startServiceButton = (Button) findViewById(R.id.startServiceButton);
		final Button stopServiceButton = (Button) findViewById(R.id.stopServiceButton);

		logServiceWithLock = new Intent(this, LogServiceWithWakelock.class);
        logServiceWithoutLock = new Intent(this, LogServiceWithoutWakelock.class);

		startServiceButton.setOnClickListener(this);
		stopServiceButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
        final ToggleButton wakeLockToggleButton = (ToggleButton) findViewById(R.id.enableLockToggleButton);
        boolean isWakeLockEnabled = wakeLockToggleButton.isChecked();

		if (v.getId() == R.id.startServiceButton) {
            startServiceWithLock(isWakeLockEnabled);
            wakeLockToggleButton.setEnabled(false);
		} else if (v.getId() == R.id.stopServiceButton) {
            stopServiceWithLock(isWakeLockEnabled);
            wakeLockToggleButton.setEnabled(true);
		}
	}

    private void stopServiceWithLock(boolean wakeLockEnabled) {
        if (wakeLockEnabled) {
            stopService(logServiceWithLock);
        } else {
            stopService(logServiceWithoutLock);
        }
    }

    private void startServiceWithLock(boolean wakeLockEnabled) {
        if (wakeLockEnabled) {
            startService(logServiceWithLock);
        } else {
            Log.e("TEST", "Hola");
            startService(logServiceWithoutLock);
        }
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}