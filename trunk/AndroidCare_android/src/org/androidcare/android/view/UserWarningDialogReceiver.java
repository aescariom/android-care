package org.androidcare.android.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

public class UserWarningDialogReceiver extends Activity {
    private final String TAG = this.getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Request received");
        Bundle bundle = getIntent().getExtras();
        String type = (String) bundle.getSerializable("type");
        Serializable displayable = bundle.getSerializable("displayable");
        Log.d(TAG, "received: " + type + " exactly " + displayable);

        UISliderView view = new UISliderView(this, type, displayable);
        this.setContentView(view);
    }
}
