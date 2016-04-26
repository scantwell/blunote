package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 4/26/16.
 */
public class BluetoothGreeter extends Thread {
    private static final String TAG = "Bluetooth Greeter";
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID uuid;
    private BluetoothSocket socket;
    private NetworkPacket packet;

    public BluetoothGreeter(String macAddress, UUID uuid) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.uuid = uuid;
        try {
            this.socket = this.bluetoothAdapter.getRemoteDevice(macAddress)
                    .createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error creating insecure socket: %s", e.getMessage()));
        }
    }

    public void run() {
        try {
            this.socket.connect();
            BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(this.socket);
            BlunoteOutputStream outputStream = blunoteSocket.getOutputStream();
            BlunoteInputStream inputStream = blunoteSocket.getInputStream();

            this.packet = inputStream.read();

            NetworkPacket.Builder builder = NetworkPacket.newBuilder();
            builder.setType(NetworkPacket.Type.DROP);
            NetworkPacket response = builder.build();
            outputStream.write(response);
        } catch (IOException e) {
            Log.e(TAG, String.format("Connection refused, handshake failed: %s", e.getMessage()));
            this.packet = null;
        }
    }

    public NetworkPacket getPacket() {
        return this.packet;
    }
}
