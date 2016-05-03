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
    private final String macAddress;

    public BluetoothOutputStream(BluetoothSocket socket) throws IOException {
        this.outputStream = socket.getOutputStream();
        this.macAddress = socket.getRemoteDevice().getAddress();
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
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream); //new BufferedOutputStream(outputStream, 4 + data.length);
        dataOutputStream.writeInt(data.length);
        int offset = 0, count;
        while (offset < data.length) {
            count = ((offset + BUFFERSIZE) < data.length) ? BUFFERSIZE : data.length - offset;
            dataOutputStream.write(data, offset, count);
            dataOutputStream.flush();
            offset += count;
        }
        return data.length;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public void close() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
