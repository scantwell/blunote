package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * BluetoothSocket that implements BlunoteSocket interface
 * Allows Reading Messages from mailbox, and writing messages out to the data thread
 */
public class BlunoteBluetoothSocket implements BlunoteSocket {
    private static final String TAG = "BlunoteBluetoothSocket";
    private BluetoothSocket socket;
    private BluetoothInputStream bluetoothInputStream;
    private BluetoothOutputStream bluetoothOutputStream;


    public BlunoteBluetoothSocket(BluetoothSocket socket) {
        this.socket = socket;
        try {
            this.bluetoothInputStream = new BluetoothInputStream(socket);
            this.bluetoothOutputStream = new BluetoothOutputStream(socket);
        } catch (IOException e) {
            Log.e(TAG, "Error setting Input/Output Stream: " + e.getMessage());
        }
    }

    public BluetoothOutputStream getOutputStream() {
        return this.bluetoothOutputStream;
    }

    public BluetoothInputStream getInputStream() {
        return this.bluetoothInputStream;
    }

    public String getAddress()
    {
        return this.socket.getRemoteDevice().getAddress();
    }

    public void close() {
        try {
            this.bluetoothInputStream.close();
            this.bluetoothOutputStream.close();
            this.socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Socket: " + e.getMessage());
        }
    }
}