package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by omnia on 1/13/16.
 *
 * Manages Bluetooth Adapter and Client / Server / Data Transfer Threads
 *
 * TODO: Use Bluetooth Beacons
 * TODO: Change writes to use Messages, then convert to byte[]
 * TODO: Replace Handler callback for communication back to NetworkService?
 */
public class BluetoothManager {
    private static final String TAG = "Bluetooth Manager";
    private BluetoothAdapter bluetoothAdapter;
    private ServerThread serverThread;
    private ClientThread clientThread;

    private DataTransferThread upStream;
    private ArrayList<DataTransferThread> downStream = new ArrayList<>();

    private Handler handler;

    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private static final String NAME = "BluNote";

    public BluetoothManager(Handler handler) {
        // Setup Bluetooth Adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Create and Start Server listening thread
        serverThread = new ServerThread();
        serverThread.start();

        // Set handler ??
        this.handler = handler;
    }

    /**
     * Get Bonded Devices
     * @return Set of Bluetooth Devices that have been paired to this device
     */
    public Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    /**
     * Connect To Device
     * Initiates a connection to a new upStream Bluetooth Device
     * TODO: Automate this to scan and connect to open BT Beacons
     */
    public void connectToDevice(BluetoothDevice device) {
        clientThread = new ClientThread(device);
        clientThread.start();
    }

    /**
     * Write Up Stream
     * Send Message Up Stream Towards the Host
     * TODO: Change method to take in a Message
     */
    public void writeUpStream(int command, byte[] buffer) {
        this.upStream.write(command, buffer);
    }

    /**
     * Write Down Stream
     * Send Message Down Stream to all the clients
     * TODO: Change method to take in a Message
     */
    public void writeDownStream(int command, byte[] buffer) {
        for (DataTransferThread thread : this.downStream) {
            thread.write(command, buffer);
        }
    }

    /**
     * Clean up and before shutting down
     */
    public void cancel() {
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.cancel();
        }

        if (clientThread != null) {
            clientThread.interrupt();
            clientThread.cancel();
        }

        if (upStream != null) {
            upStream.interrupt();
            upStream.cancel();
        }

        for (DataTransferThread thread : downStream) {
            thread.interrupt();
            thread.cancel();
        }
    }

    /**
     * Get Host Device
     *
     * @return  Current host device used for upstream communication
     */
    public BluetoothDevice getHostDevice() {
        if (upStream != null) {
            return upStream.getRemoteDevice();
        }
        return null;
    }

    /**
     * Get Client Devices
     */
    public ArrayList<BluetoothDevice> getClientDevices() {
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        for (DataTransferThread thread : downStream) {
            devices.add(thread.getRemoteDevice());
        }
        return devices;
    }

    /**
     * Bluetooth Server Thread
     * Listen for Connecting Devices
     * Creates a new DataTransferThread to be added to this devices downStream
     * TODO: Use Beacons
     */
    private class ServerThread extends Thread {
        private static final String TAG = "Bluetooth Server Thread";
        private final BluetoothServerSocket mmServerSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error creating Insecure Socket: " + e.getMessage());
            }
            mmServerSocket = tmp;
        }

        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            BluetoothSocket socket;
            while(true) {
                try {
                    socket = mmServerSocket.accept();
                    if (socket != null) {
                        DataTransferThread dataTransferThread = new DataTransferThread(socket, false);
                        dataTransferThread.start();
                        downStream.add(dataTransferThread);
                        Log.v(TAG, "New Client Connected: " + dataTransferThread.getRemoteDevice());
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
                DataTransferThread dataTransferThread = new DataTransferThread(mmSocket, true);
                dataTransferThread.start();
                upStream = dataTransferThread;
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


    /**
     * Bluetooth Data Transfer Thread
     * Send or Receive Data from a connected Bluetooth Device
     */
    private class DataTransferThread extends Thread {
        private static final String TAG = "Bluetooth Data Thread";
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final boolean IsUpStream;

        public DataTransferThread(BluetoothSocket socket, boolean isUpStream) {
            IsUpStream = isUpStream;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e ) {
                Log.e(TAG, "Error setting Input/Output Stream: " + e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public BluetoothDevice getRemoteDevice() {
            return mmSocket.getRemoteDevice();
        }

        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(mmInStream));
            byte[] buffer;
            int command, messageSize, bytes, bufferSize = 1024 * 10;
            while(true) {
                try {
                    command = inStream.readInt();
                    messageSize = inStream.readInt();
                    bytes = 0;
                    buffer = new byte[messageSize];

                    while (bytes < messageSize) {
                        int b = ((bytes + bufferSize) < messageSize) ? bufferSize : messageSize - bytes;
                        bytes += inStream.read(buffer, bytes, b);
                    }
                    handler.obtainMessage(command,
                            0,
                            bytes,
                            buffer).sendToTarget();

                } catch (IOException e) {
                    Log.e(TAG, "Error Connection Lost: " + e.getMessage());
                    reconnect();
                    break;
                }
            }
        }

        public void write(int command, byte[] bytes) {
            try {
                // Get Data Output Stream for Bluetooth Communication
                DataOutputStream d = new DataOutputStream(new BufferedOutputStream(mmOutStream, bytes.length + 8));

                // Write Command, then length of bytes to Transfer
                d.writeInt(command);
                d.writeInt(bytes.length);

                // Send Bytes in 1K "packets"
                int bufferSize = 1024 * 10;
                for (int i = 0; i < bytes.length; i+=bufferSize) {
                    int b = ((i + bufferSize) < bytes.length) ? bufferSize : bytes.length - i;
                    d.write(bytes, i, b);
                    d.flush();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error writing to thread: " + e.getMessage());
            }
        }

        public void reconnect() {
            cancel();
            if (IsUpStream) {
                upStream = null;
                // Search for new host
            } else {
                downStream.remove(this);
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
