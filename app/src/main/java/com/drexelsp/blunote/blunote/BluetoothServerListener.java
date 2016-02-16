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
 */
public class BluetoothServerListener {
    private static final String NAME = "BluNote";
    private static final String TAG = "Bluetooth Server Listener";
    private final UUID MY_UUID;

    private BluetoothAdapter mBluetoothAdapter;
    private ServerThread mServerThread;
    private BluetoothBeacon mBluetoothBeacon;
    private BlunoteRouter mBlunoteRouter;

    public BluetoothServerListener(BlunoteRouter router, UUID uuid) {
        MY_UUID = uuid;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBlunoteRouter = router;

        mServerThread = new ServerThread();
        mServerThread.start();

        mBluetoothBeacon = new BluetoothBeacon();
        mBluetoothBeacon.advertiseBeacon();
    }

    public void updateAdvertiseData(String networkName, int userCount, int songCount, int latency) {
        mBluetoothBeacon.updateAdvertiseData(networkName, userCount, songCount, latency);
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
                        BlunoteBluetoothSocket blunoteBluetoothSocket = new BlunoteBluetoothSocket(socket, mBlunoteRouter);

                        mBlunoteRouter.addDownStream(blunoteBluetoothSocket);

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
