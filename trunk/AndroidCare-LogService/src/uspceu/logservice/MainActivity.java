package uspceu.logservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {
	EditText activityEditText;
	Intent logService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button startServiceButton = (Button) findViewById(R.id.startServiceButton);
		final Button stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
		activityEditText = (EditText) findViewById(R.id.activityEditText);
		logService = new Intent(this, LogService.class);

		startServiceButton.setOnClickListener(this);
		stopServiceButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startServiceButton) {
			logService.putExtra("Activity", activityEditText.getText()
					.toString());
			startService(logService);
		} else if (v.getId() == R.id.stopServiceButton) {
			stopService(logService);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}