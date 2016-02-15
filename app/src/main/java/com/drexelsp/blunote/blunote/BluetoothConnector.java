package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 1/13/16.
 *
 * Initializes a connection to a host device
 * TODO: Use a callback to send BlunoteBluetoothSocket back to network service
 */
public class BluetoothConnector {
    private static final String TAG = "Bluetooth Manager";
    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");

    public BluetoothConnector() {
    }

    /**
     * Connect To Device
     * Initiates a connection to a new Host Bluetooth Device
     */
    public void connectToDevice(BluetoothDevice device) {
        ClientThread clientThread = new ClientThread(device);
        clientThread.start();
    }

    /**
     * Bluetooth Client Thread
     * Connect to a Device running the Server Thread
     */
    private class ClientThread extends Thread {
        private static final String TAG = "Bluetooth Client Thread";
        private final BluetoothSocket mmSocket;

        public ClientThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error creating Insecure Socket: " + e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                mmSocket.connect();
                BlunoteBluetoothSocket blunoteBluetoothSocket = new BlunoteBluetoothSocket(mmSocket);

                // Use Callback Here with blunoteBluetoothSocket

                Log.v(TAG, "Connection to a host Accepted");
            } catch (IOException connectException) {
                Log.e(TAG, "Connection to a host Refused: " + connectException.getMessage());
                try {
                    mmSocket.close();
                } catch(IOException closeException) {
                    Log.e(TAG, "Error closing socket: " + closeException.getMessage());
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Socket: " + e.getMessage());
            }
        }
    }
}
