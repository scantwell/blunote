package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages;
import com.google.protobuf.ByteString;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class BluetoothOutputStream implements BlunoteOutputStream{
    private final OutputStream outputStream ;
    private static int BUFFERSIZE = 1024;

    public BluetoothOutputStream(BluetoothSocket socket) throws IOException
    {
        this.outputStream = socket.getOutputStream();
    }

    public boolean write(BlunoteMessages.NetworkPacket networkPacket) throws IOException {
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

    @Override
    public int write(ByteString data) throws IOException {
        byte[] bytes = data.toByteArray();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream, bytes.length + 4));
        dataOutputStream.writeInt(bytes.length);
        int b = 0;
        int i = 0;
        for (i = 0; i < bytes.length; i += BUFFERSIZE) {
            b = ((i + BUFFERSIZE) < bytes.length) ? BUFFERSIZE : bytes.length - i;
            dataOutputStream.write(bytes, i, b);
            dataOutputStream.flush();
        }
        return bytes.length;
    }
}
