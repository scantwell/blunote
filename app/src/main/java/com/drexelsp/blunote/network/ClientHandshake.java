package com.drexelsp.blunote.network;

import android.util.Log;

import com.drexelsp.blunote.network.NetworkMessages.NetworkPacket;
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
    private NetworkPacket networkPacket;
    private boolean maintain;
    private boolean success;

    public ClientHandshake(BlunoteSocket socket, boolean maintain) throws IOException {
        this.socket = socket;
        this.maintain = maintain;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void run() {
        try {
            this.networkPacket = this.inputStream.read();

            NetworkPacket.Builder builder = NetworkPacket.newBuilder();
            if (this.maintain) {
                builder.setType(NetworkPacket.Type.NEW);
            } else {
                builder.setType(NetworkPacket.Type.DROP);
            }
            NetworkPacket response = builder.build();
            this.outputStream.write(response.toByteString());

            if (!this.maintain) {
                this.close();
            }
            this.success = true;
        } catch (IOException e) {
            this.success = false;
        }
    }

    public boolean getSuccess() {
        return this.success;
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
}
