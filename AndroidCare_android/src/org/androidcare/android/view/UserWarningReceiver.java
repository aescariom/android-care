package org.androidcare.android.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

public class UserWarningReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Request received");
        Bundle bundle = intent.getExtras();
        String type = bundle.getString("type");
        Serializable displayable = bundle.getSerializable("displayable");
        displayDialog(context, type, displayable);
    }

    private void displayDialog(Context ctx, String type, Serializable displayable) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(ctx, UserWarningDialogReceiver.class);
        intent.putExtra("type", type);
        intent.putExtra("displayable", displayable);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ctx.startActivity(intent);
    }
}
