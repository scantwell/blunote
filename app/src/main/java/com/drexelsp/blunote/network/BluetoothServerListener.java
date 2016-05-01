package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 2/12/16.
 * <p>
 * Starts a Bluetooth Server listener thread to allow incoming connections
 * Also creates a BluetoothBeacon and begins advertising
 */
public class BluetoothServerListener {
    private static final String TAG = "BluetoothServerListener";
    private static final String NAME = "BluNote";
    private final Router router;
    private final UUID MY_UUID;
    private byte[] handshake;
    private BluetoothAdapter mBluetoothAdapter;
    private ServerThread mServerThread;

    public BluetoothServerListener(Router router, UUID uuid, byte[] handshake) {
        Log.v(TAG, "Created");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.router = router;
        this.MY_UUID = uuid;
        this.handshake = handshake;

        mServerThread = new ServerThread();
        mServerThread.start();
    }

    public void setHandshake(byte[] handshake) {
        this.handshake = handshake;
    }

    public void shutdown() {
        Log.v(TAG, "Shutting Down");
        mServerThread.interrupt();
        mServerThread.cancel();
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
            while (true) {
                try {
                    Log.v(TAG, "Listening for new connection");
                    socket = mmServerSocket.accept();
                    BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(socket);
                    new Thread(new ServerHandshake(blunoteSocket, router, handshake)).start();

                    Log.v(TAG, "New Client Connected: " + socket.getRemoteDevice());
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
