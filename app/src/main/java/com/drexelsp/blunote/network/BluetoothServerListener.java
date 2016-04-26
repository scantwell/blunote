package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.events.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * Starts a Bluetooth Server listener thread to allow incoming connections
 * Also creates a BluetoothBeacon and begins advertising
 */
public class BluetoothServerListener {
    private static final String TAG = "BluetoothServerListener";
    private static final String NAME = "BluNote";
    private final UUID MY_UUID;

    private BluetoothAdapter mBluetoothAdapter;
    private ServerThread mServerThread;
    private BlunoteRouter mBlunoteRouter;
    private EventBus mEventBus;

    public BluetoothServerListener(UUID uuid) {
        Log.v(TAG, "Created");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mEventBus = EventBus.getDefault();
        mBlunoteRouter = BlunoteRouter.getInstance();
        MY_UUID = uuid;

        mServerThread = new ServerThread();
        mServerThread.start();
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
            BluetoothEvent bluetoothEvent;
            BluetoothSocket socket;
            BlunoteBluetoothSocket blunoteBluetoothSocket;
            while (true) {
                try {
                    Log.v(TAG, "Listening for new connection");
                    socket = mmServerSocket.accept();
                    blunoteBluetoothSocket socket = new BlunoteBluetoothSocket(socket);
                    //Handshake(Runnable) -> creates new Connection(socket) and adds it to router


                    new Thread(new Connection(socket)).start();

                    //bluetoothEvent = new BluetoothEvent(BluetoothEvent.SERVER_LISTENER, true, socket.getRemoteDevice().getAddress());
                    //mEventBus.post(bluetoothEvent);

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
