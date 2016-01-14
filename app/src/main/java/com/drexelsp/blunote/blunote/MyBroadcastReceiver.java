package com.drexelsp.blunote.blunote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by scantwell on 1/13/2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Extract data included in the Intent
        String message = intent.getStringExtra("data");
        Log.v("receiver", "Got message: " + message);
    }
}
