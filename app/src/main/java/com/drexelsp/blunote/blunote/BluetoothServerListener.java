package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 2/12/16.
 *
 * Starts a Bluetooth Server listener thread to allow incoming connections
 * Also creates a BluetoothBeacon and begins advertising
 * TODO: Callback to NetworkService with new BlunoteBluetoothSocket
 */
public class BluetoothServerListener {
    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private static final String NAME = "BluNote";
    private static final String TAG = "Bluetooth Server Listener";

    private BluetoothAdapter mBluetoothAdapter;
    private ServerThread mServerThread;
    private BluetoothBeacon mBluetoothBeacon;

    public BluetoothServerListener() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mServerThread = new ServerThread();
        mServerThread.start();

        mBluetoothBeacon = new BluetoothBeacon();
        mBluetoothBeacon.advertiseBeacon();
    }

    private class ServerThread extends Thread {
        private static final String TAG = "Bluetooth Server Thread";
        private final BluetoothServerSocket mmServerSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error creating Insecure Socket: " + e.getMessage());
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;
            while(true) {
                try {
                    socket = mmServerSocket.accept();
                    if (socket != null) {
                        BlunoteBluetoothSocket blunoteBluetoothSocket = new BlunoteBluetoothSocket(socket);

                        // Return blunoteBluetoothSocket to Network Service

                        Log.v(TAG, "New Client Connected: " + socket.getRemoteDevice());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error connecting new Client: " + e.getMessage());
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket: " + e.getMessage());
            }
        }
    }
}
