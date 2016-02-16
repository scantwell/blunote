package com.drexelsp.blunote.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends android.content.BroadcastReceiver {

    private ClientService cService;

    public Receiver(ClientService service) {
        cService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract data included in the Intent
        byte[] data = intent.getByteArrayExtra("data");
        Log.v("receiver", "Got message: " + data);
        cService.onReceived(data);
    }
}