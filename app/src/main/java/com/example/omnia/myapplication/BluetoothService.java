package com.example.omnia.myapplication;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {

    private BluetoothAdapter bluetoothAdapter;
    private AcceptThread serverThread;
    private ConnectThread clientThread;
    private int uniqueId = 0;
    public Map<Integer, BluetoothDevice> connectedDevices;
    private Map<BluetoothDevice, ConnectedThread> connectedThreads;
    private Handler handler;

    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private static final String NAME = "BluNote";

    // Constructor, Takes handler
    public BluetoothService(Handler handler) {
        // Setup Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedDevices = new HashMap<>();
        connectedThreads = new HashMap<>();
        serverThread = new AcceptThread();
        serverThread.start();
        this.handler = handler;
    }

    // Get All Devices that have been paired to this device
    public Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    // Start a Client connection thread to connect to the given device
    public void connectToDevice(BluetoothDevice device) {
        clientThread = new ConnectThread(device);
        clientThread.start();
    }

    // Write to the Connected Thread for the given BluetoothDevice
    public boolean write(BluetoothDevice device, int command, byte[] buffer) {
        if (connectedThreads.size() == 0) {
            return false;
        }
        ConnectedThread connectedThread = connectedThreads.get(device);
        connectedThread.write(command, buffer);
        return true;
    }

    // Get Number of Connected Threads
    public int numConnectedThreads() {
        return connectedThreads.size();
    }

    // Clean up
    public void cancel() {
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.cancel();
        }
        if (clientThread != null) {
            clientThread.interrupt();
            clientThread.cancel();
        }

        for (ConnectedThread thread : connectedThreads.values()) {
            thread.interrupt();
            thread.cancel();
        }
    }

    // Get connected devices
    public ArrayList<BluetoothDevice> getConnectedDevices() {
        return new ArrayList<>(connectedDevices.values());
    }

    // Get ID using Device
    public int getIdByDevice(BluetoothDevice device) {
        for (Map.Entry<Integer, BluetoothDevice> entry: connectedDevices.entrySet()) {
            if(Objects.equals(device, entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    // Get Device using ID
    public BluetoothDevice getDeviceById(int id) {
        return connectedDevices.get(id);
    }


    // Bluetooth Server Thread
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;
            while(true) {
                try {
                    socket = mmServerSocket.accept();
                    if (socket != null) {
                        Log.w("Connection", "Received");
                        handler.obtainMessage(0, 0, 0, "Connection Received").sendToTarget();
                        ConnectedThread connectedThread = new ConnectedThread(socket);
                        connectedThread.start();
                        connectedDevices.put(uniqueId++, socket.getRemoteDevice());
                        connectedThreads.put(socket.getRemoteDevice(), connectedThread);
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Bluetooth Client Thread
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch(IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
            Log.w("Connection", "Accepted");
            handler.obtainMessage(0, 0, 0, "Connection Accepted").sendToTarget();
            ConnectedThread connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
            connectedDevices.put(uniqueId++, mmSocket.getRemoteDevice());
            connectedThreads.put(mmSocket.getRemoteDevice(), connectedThread);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Bluetooth Connection Transfer Thread
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e ) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(mmInStream));
            byte[] buffer;
            int command, messageSize, bytes;
            while(true) {
                try {
                    command = inStream.readInt();
                    messageSize = inStream.readInt();
                    bytes = 0;
                    buffer = new byte[messageSize];
                    while (bytes < messageSize) {
                        bytes += inStream.read(buffer, bytes, messageSize - bytes);
                    }
                    handler.obtainMessage(command,
                            getIdByDevice(mmSocket.getRemoteDevice()),
                            bytes,
                            buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    cancel();
                    break;
                }
            }
        }

        public void write(int command, byte[] bytes) {
            try {
                // Get Data Output Stream for Bluetooth Communication
                DataOutputStream d = new DataOutputStream(new BufferedOutputStream(mmOutStream, bytes.length + 4));

                // Write Command, then length of bytes to Transfer
                d.writeInt(command);
                d.writeInt(bytes.length);

                // Send Bytes in 1K "packets"
                int bufferSize = 1024;
                for (int i = 0; i < bytes.length; i+=bufferSize) {
                    int b = ((i + bufferSize) < bytes.length) ? bufferSize : bytes.length - i;
                    d.write(bytes, i, b);
                    d.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
