package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkMap;
import com.drexelsp.blunote.events.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 1/13/16.
 * <p>
 * Initializes a connection to a host device
 */
public class BluetoothConnector extends Thread {
    private static final String TAG = "Bluetooth Connector";
    private Router router;
    private final UUID uuid;
    private NetworkMap networkMap;
    private BluetoothAdapter bluetoothAdapter;
    private EventBus eventBus;

    public BluetoothConnector(Router router, UUID uuid, NetworkMap networkMap) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.eventBus = EventBus.getDefault();
        this.router = router;
        this.uuid = uuid;
        this.networkMap = networkMap;
    }

    public void run() {
        for (String macAddress : this.networkMap.getMacAddressesList()) {
            try {
                BluetoothSocket bluetoothSocket = this.bluetoothAdapter.getRemoteDevice(macAddress)
                        .createInsecureRfcommSocketToServiceRecord(this.uuid);
                bluetoothSocket.connect();
                BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(bluetoothSocket);
                ClientHandshake clientHandshake = new ClientHandshake(blunoteSocket, router, true);
                new Thread(clientHandshake).start();
                Log.v(TAG, "Connection to a host Accepted, starting handshake");
                return;
            } catch (IOException e) {
                Log.e(TAG, String.format("Error creating insecure socket: %s", e.getMessage()));
            }
        }
        Log.e(TAG, "Unable to connect to any devices in the network map");
        BluetoothEvent bluetoothEvent = new BluetoothEvent(BluetoothEvent.CONNECTOR, false, "");
        this.eventBus.post(bluetoothEvent);
    }
}
