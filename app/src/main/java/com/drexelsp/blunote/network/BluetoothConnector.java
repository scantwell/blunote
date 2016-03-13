package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.events.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by omnia on 1/13/16.
 *
 * Initializes a connection to a host device
 */
public class BluetoothConnector {
    private static final String TAG = "Bluetooth Connector";
    private final UUID MY_UUID;

    private BluetoothAdapter mBluetoothAdapter;
    private BlunoteRouter mRouter;
    private EventBus mEventBus;

    public BluetoothConnector(UUID uuid) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mEventBus = EventBus.getDefault();
        mRouter = BlunoteRouter.getInstance();
        MY_UUID = uuid;
    }

    /**
     * Connect To Device
     * Initiates a connection to a new Host Bluetooth Device
     */
    public void connectToDevice(String deviceMacAddress) {
        ClientThread clientThread = new ClientThread(deviceMacAddress);
        clientThread.start();
    }

    /**
     * Bluetooth Client Thread
     * Connect to a Device running the Server Thread
     */
    private class ClientThread extends Thread {
        private static final String TAG = "Bluetooth Client Thread";
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ClientThread(String deviceMacAddress) {
            mmDevice = mBluetoothAdapter.getRemoteDevice(deviceMacAddress);
            BluetoothSocket tmp = null;
            try {
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error creating Insecure Socket: " + e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            BluetoothEvent bluetoothEvent;
            try {
                mmSocket.connect();
                BlunoteBluetoothSocket blunoteBluetoothSocket = new BlunoteBluetoothSocket(mmSocket);
                mRouter.setUpStream(blunoteBluetoothSocket);

                bluetoothEvent = new BluetoothEvent(BluetoothEvent.CONNECTOR, true, mmDevice.getAddress());
                mEventBus.post(bluetoothEvent);

                Log.v(TAG, "Connection to a host Accepted");
            } catch (IOException connectException) {
                bluetoothEvent = new BluetoothEvent(BluetoothEvent.CONNECTOR, false, mmDevice.getAddress());
                mEventBus.post(bluetoothEvent);

                Log.e(TAG, "Connection to a host Refused: " + connectException.getMessage());
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
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
