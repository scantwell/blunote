package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * BluetoothSocket that implements BlunoteSocket interface
 * Allows Reading Messages from mailbox, and writing messages out to the data thread
 */
public class BlunoteBluetoothSocket implements BlunoteSocket {
    private static final String TAG = "BlunoteBluetoothSocket";
    private BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream ;
    private static int BUFFERSIZE = 1024;

    public BlunoteBluetoothSocket(BluetoothSocket socket) {
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error setting Input/Output Stream: " + e.getMessage());
        }

        this.inputStream = tmpIn;
        this.outputStream = tmpOut;
    }

    public boolean write(NetworkPacket networkPacket) throws IOException {
        byte[] bytes = networkPacket.toByteArray();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream, bytes.length + 4));
        dataOutputStream.writeInt(bytes.length);
        for (int i = 0; i < bytes.length; i += BUFFERSIZE) {
            int b = ((i + BUFFERSIZE) < bytes.length) ? BUFFERSIZE : bytes.length - i;
            dataOutputStream.write(bytes, i, b);
            dataOutputStream.flush();
        }
        return true;
    }

    public NetworkPacket read() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
        int messageSize, bytes;
        byte[] buffer;
        messageSize = dataInputStream.readInt();
        bytes = 0;
        buffer = new byte[messageSize];
        while (bytes < messageSize) {
            int b = ((bytes + BUFFERSIZE) < messageSize) ? BUFFERSIZE : messageSize - bytes;
            bytes += dataInputStream.read(buffer, bytes, b);
        }
        return NetworkPacket.parseFrom(buffer);
    }


    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Socket: " + e.getMessage());
        }
    }
}