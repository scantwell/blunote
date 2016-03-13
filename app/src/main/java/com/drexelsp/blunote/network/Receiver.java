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
        Log.v(TAG, "Received");
        // Extract data included in the Intent
        String messageType = intent.getStringExtra("Type");
        if (messageType.equals("MessageReceived")) {
            byte[] data = intent.getByteArrayExtra("Data");
            Log.v(TAG, "Got message: " + data);
            cService.onReceived(data);
        } else if (messageType.equals("BluetoothEvent")) {
            int event = intent.getIntExtra("Event", BluetoothEvent.ERROR);
            boolean success = intent.getBooleanExtra("Success", false);
            String macAddress = intent.getStringExtra("MacAddress");

            BluetoothEvent bluetoothEvent = new BluetoothEvent(event, success, macAddress);
            Log.v(TAG, String.format("Got Network Event %d, Success %b, Address %s", event, success, macAddress));

            cService.onNetworkEvent(bluetoothEvent);
        }

    }
}