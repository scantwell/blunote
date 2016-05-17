package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by omnia on 2/12/16.
 * <p>
 * BluetoothSocket that implements BlunoteSocket interface
 * Allows Reading Messages from mailbox, and writing messages out to the data thread
 */
public class BlunoteBluetoothSocket implements BlunoteSocket {
    private static final String TAG = "BlunoteBluetoothSocket";
    private BluetoothSocket socket;

    public BlunoteBluetoothSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothOutputStream getOutputStream() throws IOException {
        return new BluetoothOutputStream(this.socket);
    }

    public BluetoothInputStream getInputStream() throws IOException {
        return new BluetoothInputStream(this.socket);
    }

    public String getAddress() {
        return this.socket.getRemoteDevice().getAddress();
    }

    public void close() throws IOException {
        this.socket.getInputStream().close();
        this.socket.getOutputStream().close();
        this.socket.close();
    }
}