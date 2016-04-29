package com.drexelsp.blunote.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.drexelsp.blunote.events.BluetoothEvent;

public class Receiver extends android.content.BroadcastReceiver {
    private static final String TAG = "Receiver";

    private ClientService cService;

    public Receiver(ClientService service) {
        cService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract data included in the Intent
        String messageType = intent.getStringExtra("Type");
        if (messageType.equals("OnReceiveDownstream")) {
            Log.v(TAG, "Received message.");
            byte[] data = intent.getByteArrayExtra("Data");
            cService.onReceiveDownstream(data);
        } else if (messageType.equals("OnReceiveUpstream")) {
            Log.v(TAG, "Received message.");
            byte[] data = intent.getByteArrayExtra("Data");
            cService.onReceiveUpstream(data);
        } else if (messageType.equals("OnConnectionDownstream")) {
            Log.v(TAG, "OnConnectionDownstream event has occurred.");
            cService.onConnectionDownstream(intent.getStringExtra("MacAddress"));
        } else if (messageType.equals("OnConnectionUpstream")) {
            Log.v(TAG, "OnConnectionUpstream event has occurred.");
            cService.onConnectionUpstream(intent.getStringExtra("MacAddress"));
        } else if (messageType.equals("OnDisconnectionDownstream")) {
            Log.v(TAG, "OnDisconnectionDownstream event has occurred.");
            cService.onDisconnectionDownstream(intent.getStringExtra("MacAddress"));
        } else if (messageType.equals("OnDisconnectionUpstream")) {
            Log.v(TAG, "OnDisconnectionUpstream event has occurred.");
            cService.onDisconnectionUpstream(intent.getStringExtra("MacAddress"));
        }
    }
}