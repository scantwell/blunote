package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Bluetooth Greeter makes a temporary connection to a bluetooth device
 * and saves the welcome packet data to be retrieved by the scanner
 * <p>
 * Created by omnia on 4/26/16.
 */
public class BluetoothGreeter extends Thread {
    private static final String TAG = "Bluetooth Greeter";
    private BluetoothSocket socket;
    private NetworkPacket packet;
    private BluetoothConnector2 connector;

    public BluetoothGreeter(String macAddress, UUID uuid) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*try {
            this.socket = bluetoothAdapter.getRemoteDevice(macAddress)
                    .createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error creating insecure socket: %s", e.getMessage()));
        }*/
        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.add(uuid);
        this.connector  = new BluetoothConnector2(bluetoothAdapter.getRemoteDevice(macAddress), false, bluetoothAdapter, uuids);
    }

    public void run() {
        try {
            //this.socket.connect();
            BluetoothConnector2.BluetoothSocketWrapper socket = this.connector.connect();
            //BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(this.socket);
            BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(socket.getUnderlyingSocket());
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
