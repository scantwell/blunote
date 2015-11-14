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
import java.util.Set;
import java.util.UUID;

public class BluetoothService {

    private BluetoothAdapter bluetoothAdapter;
    private AcceptThread serverThread;
    private ConnectThread clientThread;
    private BluetoothSocket connectedSocket;
    private ConnectedThread connectedThread;
    private Handler handler;

    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private static final String NAME = "BluNote";

    public BluetoothService(Handler handler) {
        // Setup Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        serverThread = new AcceptThread();
        serverThread.start();
        this.handler = handler;
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    public void connectToDevice(BluetoothDevice device) {
        clientThread = new ConnectThread(device);
        clientThread.start();
    }

    public boolean write(byte[] buffer) {
        if (connectedThread == null) {
            return false;
        }
        connectedThread.write(buffer);
        return true;
    }

    public boolean isConnected() {
        return connectedSocket.isConnected();
    }

    public void cancel() {
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.cancel();
        }
        if (clientThread != null) {
            clientThread.interrupt();
            clientThread.cancel();
        }
        if (connectedThread != null) {
            connectedThread.interrupt();
            connectedThread.cancel();
        }
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
                        connectedSocket = socket;
                        connectedThread = new ConnectedThread(socket);
                        connectedThread.start();
                        mmServerSocket.close();
                        break;
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
            connectedSocket = mmSocket;
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
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
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
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
            int messageSize;
            int bytes;
            while(true) {
                try {
                    messageSize = inStream.readInt();
                    bytes = 0;
                    buffer = new byte[messageSize];
                    while (bytes < messageSize) {
                        bytes += inStream.read(buffer, bytes, messageSize - bytes);
                    }
                    handler.obtainMessage(1, 0, bytes, buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    cancel();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                // Get Data Output Stream for Bluetooth Communication
                DataOutputStream d = new DataOutputStream(new BufferedOutputStream(mmOutStream, bytes.length + 4));

                // Write length of bytes to Transfer
                d.writeInt(bytes.length);

                // Send Bytes in 1K "packets"
                int bufferSize = 1024;
                for (int i = 0; i < bytes.length; i+=bufferSize) {
                    int b = ((i + bufferSize) < bytes.length) ? bufferSize : bytes.length - i;
                    d.write(bytes, i, b);
                    d.flush();
                }

                // Trigger Message Handler
                handler.obtainMessage(1, 0, bytes.length, bytes).sendToTarget();
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
