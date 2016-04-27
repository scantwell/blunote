package com.drexelsp.blunote.network;

import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Client Handshake is a runnable that runs the client side of the handshaking process
 * using the BlunoteInputStream and BlunoteOutputStream from the BlunoteSocket
 * <p>
 * Created by omnia on 4/25/16.
 */
public class ClientHandshake implements Runnable {
    private static final String TAG = "ClientHandshake";
    private BlunoteSocket socket;
    private BlunoteInputStream inputStream;
    private BlunoteOutputStream outputStream;
    private Router router;
    private NetworkPacket networkPacket;
    private boolean maintain;

    public ClientHandshake(BlunoteSocket socket, Router router, boolean maintain) {
        this.socket = socket;
        this.router = router;
        this.maintain = maintain;
    }

    public void run() {
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.networkPacket = read();

        NetworkPacket.Builder builder = NetworkPacket.newBuilder();
        if (this.maintain) {
            builder.setType(NetworkPacket.Type.NEW);
        } else {
            builder.setType(NetworkPacket.Type.DROP);
        }
        NetworkPacket response = builder.build();

        write(response.toByteString());

        if (this.maintain) {
            try {
                this.router.addUpstream(socket);
            } catch (IOException e) {
                e.printStackTrace();
                this.close();
            }
        } else {
            this.close();
        }
    }

    public NetworkPacket getNetworkPacket() {
        return this.networkPacket;
    }

    private void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NetworkPacket read() {
        try {
            return this.inputStream.read();
        } catch (IOException e) {
            Log.e(TAG, String.format("Unable to read from input stream: %s", e.getMessage()));
            return null;
        }
    }

    private int write(ByteString byteString) {
        try {
            return this.outputStream.write(byteString);
        } catch (IOException e) {
            Log.e(TAG, String.format("Unable to write to output stream: %s", e.getMessage()));
            return 0;
        }
    }
}
