package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class BluetoothInputStream implements BlunoteInputStream {
    private final InputStream inputStream;
    private static int BUFFERSIZE = 1024;

    public BluetoothInputStream(BluetoothSocket socket) throws IOException {
        this.inputStream = socket.getInputStream();
    }

    public NetworkPacket read() throws IOException
    {
        byte[] buffer = rawRead();
        return NetworkPacket.parseFrom(buffer);
    }

    @Override
    public byte[] rawRead() throws IOException {
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
        return buffer;
    }

    public void close() {
        try {
            this.inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
