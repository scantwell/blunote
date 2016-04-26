package com.drexelsp.blunote.network;

import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Handshake is a runnable that runs the host side of the handshaking process
 * using the BlunoteInputStream and BlunoteOutputStream from the BlunoteSocket
 *
 * Created by omnia on 4/21/16.
 */
public class Handshake implements Runnable{
    private static final String TAG = "Handshake";
    private BlunoteSocket socket;
    private BlunoteInputStream inputStream;
    private BlunoteOutputStream outputStream;
    private Router router;
    private byte[] handshakePacket;

    public Handshake(BlunoteSocket socket, Router router, byte[] handshakePacket) {
        this.socket = socket;
        this.router = router;
        this.handshakePacket = handshakePacket;
    }

    public void run() {

        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //np = createNetworkPacket
        //Stick handshake in
        //write(np.toByte);

        NetworkPacket response = read();
        if (response != null) {
            switch (response.getType()) {
                case NEW:
                    this.router.addDownstream(this.socket);
                    break;
                case DROP:
                    this.close(socket);
                    break;
                default:
                    Log.e(TAG, "Bad handshake response, closing connection");
                    this.close(socket);
                    break;
            }
        }
    }

    private void close(BlunoteSocket socket)
    {
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
