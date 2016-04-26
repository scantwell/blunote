package com.drexelsp.blunote.network;

import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Created by omnia on 4/25/16.
 */
public class ClientHandshake implements Runnable{
    private static final String TAG = "ClientHandshake";
    private BlunoteSocket socket;
    private BlunoteInputStream inputStream;
    private BlunoteOutputStream outputStream;
    private Router router;
    private NetworkPacket networkPacket;
    private boolean maintain;

    public ClientHandshake(BlunoteSocket socket, Router router, boolean maintain) {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.router = router;
        this.maintain = maintain;
    }

    public void run() {
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
            // TODO: Store Up To Date NetworkPacket
            // this.router.setNetworkPacket(networkPacket);
            this.router.addUpstream(socket);
        } else {
            this.socket.close();
        }
    }

    public NetworkPacket getNetworkPacket() {
        return this.networkPacket;
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
