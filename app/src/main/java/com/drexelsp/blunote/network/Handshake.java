package com.drexelsp.blunote.network;

import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Created by omnia on 4/21/16.
 */
public class Handshake implements Runnable{
    private static final String TAG = "Handshake";
    private BlunoteSocket socket;
    private BlunoteInputStream inputStream;
    private BlunoteOutputStream outputStream;
    private Router router;
    private NetworkPacket handshakePacket;

    public Handshake(BlunoteSocket socket, Router router, NetworkPacket handshakePacket) {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.router = router;
        this.handshakePacket = handshakePacket;
    }

    public void run() {
        write(handshakePacket.toByteString());

        NetworkPacket response = read();
        if (response != null) {
            switch (response.getType()) {
                case NEW:
                    this.router.addDownstream(this.socket);
                    break;
                case DROP:
                    this.socket.close();
                    break;
                default:
                    Log.e(TAG, "Bad handshake response, closing connection");
                    this.socket.close();
                    break;
            }
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
