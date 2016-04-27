package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;

import com.drexelsp.blunote.blunote.BlunoteMessages;
import com.google.protobuf.ByteString;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class BluetoothOutputStream implements BlunoteOutputStream {
    private final OutputStream outputStream;
    private static int BUFFERSIZE = 1024;

    public BluetoothOutputStream(BluetoothSocket socket) throws IOException {
        this.outputStream = socket.getOutputStream();
    }

    public int write(BlunoteMessages.NetworkPacket networkPacket) throws IOException {
        byte[] bytes = networkPacket.toByteArray();
        return this.write(bytes);
    }

    @Override
    public int write(ByteString data) throws IOException {
        byte[] bytes = data.toByteArray();
        return this.write(bytes);
    }

    public int write(byte[] data) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream, data.length + 4));
        dataOutputStream.writeInt(data.length);
        int b = 0;
        int i = 0;
        for (i = 0; i < data.length; i += BUFFERSIZE) {
            b = ((i + BUFFERSIZE) < data.length) ? BUFFERSIZE : data.length - i;
            dataOutputStream.write(data, i, b);
            dataOutputStream.flush();
        }
        return data.length;
    }

    public void close() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
