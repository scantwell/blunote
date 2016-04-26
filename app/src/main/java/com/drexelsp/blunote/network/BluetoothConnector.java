package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.events.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 1/13/16.
 * <p/>
 * Initializes a connection to a host device
 */
public class BluetoothConnector extends Thread {
    private static final String TAG = "Bluetooth Connector";
    private Router router;
    private final UUID uuid;
    private BluetoothAdapter bluetoothAdapter;
    private EventBus eventBus;
    private BluetoothSocket socket;

    public BluetoothConnector(Router router, UUID uuid, String macAddress) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.eventBus = EventBus.getDefault();
        this.router = router;
        this.uuid = uuid;
        try {
            this.socket = this.bluetoothAdapter.getRemoteDevice(macAddress)
                    .createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error creating insecure socket: %s", e.getMessage()));
        }

    }

    public void run() {
        BluetoothEvent bluetoothEvent;
        try {
            this.socket.connect();
            BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(this.socket);
            new Thread(new ClientHandshake(blunoteSocket, router, true));
            Log.v(TAG, "Connection to a host Accepted, starting handshake");
        } catch (IOException connectException) {
            bluetoothEvent = new BluetoothEvent(BluetoothEvent.CONNECTOR, false, this.socket.getRemoteDevice().getAddress());
            eventBus.post(bluetoothEvent);
            Log.e(TAG, String.format("Connection to a host Refused, handshake failed: %s", connectException.getMessage()));
            close();
        }
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e(TAG, String.format("Error closing socket: %s", e.getMessage()));
        }
    }
}
