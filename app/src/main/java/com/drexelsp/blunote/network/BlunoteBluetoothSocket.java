package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * BluetoothSocket that implements BlunoteSocket interface
 * Allows Reading Messages from mailbox, and writing messages out to the data thread
 */
public class BlunoteBluetoothSocket implements BlunoteSocket {
    private BluetoothSocket mSocket;
    private DataTransferThread dataTransferThread;
    private ArrayList<Message> mailbox;
    private BlunoteRouter mRouter;

    public BlunoteBluetoothSocket(BluetoothSocket socket) {
        mSocket = socket;
        mailbox = new ArrayList<>();
        mRouter = BlunoteRouter.getInstance();
        dataTransferThread = new DataTransferThread(mSocket);
        dataTransferThread.start();
    }

    @Override
    public int numMessages() {
        return mailbox.size();
    }

    @Override
    public Message readMessage() {
        if (numMessages() > 0) {
            return mailbox.remove(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean writeMessage(Message msg) {
        Bundle bundle = msg.getData();
        dataTransferThread.write(bundle.getByteArray("data"));
        return true;
    }

    private class DataTransferThread extends Thread {
        private static final String TAG = "Bluetooth Data Thread";
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public DataTransferThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error setting Input/Output Stream: " + e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(mmInStream));
            int messageSize, bytes, bufferSize = 1024 * 10;
            byte[] buffer;

            while (true) {
                try {
                    messageSize = inStream.readInt();
                    bytes = 0;
                    buffer = new byte[messageSize];
                    while (bytes < messageSize) {
                        int b = ((bytes + bufferSize) < messageSize) ? bufferSize : messageSize - bytes;
                        bytes += inStream.read(buffer, bytes, b);
                    }
                    Bundle bundle = new Bundle(1);
                    bundle.putByteArray("data", buffer);
                    Message msg = Message.obtain(null, 0);
                    msg.setData(bundle);
                    mailbox.add(msg);
                    mRouter.wakeUp();
                } catch (IOException e) {
                    Log.e(TAG, "Error Connection Lost: " + e.getMessage());
                    cancel();
                    break;
                }
            }

        }

        public void write(byte[] bytes) {
            try {
                Log.v(TAG, "Writing to BluetoothSocket");
                DataOutputStream d = new DataOutputStream(new BufferedOutputStream(mmOutStream, bytes.length + 4));

                d.writeInt(bytes.length);

                int bufferSize = 1024;
                for (int i = 0; i < bytes.length; i += bufferSize) {
                    int b = ((i + bufferSize) < bytes.length) ? bufferSize : bytes.length - i;
                    d.write(bytes, i, b);
                    d.flush();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error writing to thread: " + e.getMessage());
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
