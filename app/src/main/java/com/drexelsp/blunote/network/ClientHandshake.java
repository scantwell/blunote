package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

/**
 * Created by omnia on 4/25/16.
 */
public class ClientHandshake implements Runnable{
    private BluetoothSocket socket;

    public ClientHandshake(BluetoothSocket socket) {
        this.socket = socket;
    }

    public void run() {
        // Read
        NetworkPacket packet = this.socket.

        // Write Drop / Maintain
    }
}
