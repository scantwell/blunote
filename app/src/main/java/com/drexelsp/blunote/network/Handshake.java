package com.drexelsp.blunote.network;

import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.WelcomePacket;
import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

import java.io.IOException;

/**
 * Created by omnia on 4/21/16.
 */
public class Handshake implements Runnable{
    private static final String TAG = "Connection";
    private BlunoteBluetoothSocket socket;
    private BlunoteRouter router;

    public Handshake(BlunoteBluetoothSocket blunoteBluetoothSocket) {
        this.socket = blunoteBluetoothSocket;
        this.router = BlunoteRouter.getInstance();
    }

    public void run() {
        // TODO: Get Handshake Packet (Welcome + Network) from configuration
        WelcomePacket.Builder WPBuilder = WelcomePacket.newBuilder();
        WelcomePacket wp = WPBuilder.build();

        NetworkPacket.Builder NPBuilder = NetworkPacket.newBuilder();
        NPBuilder.setType(NetworkPacket.Type.HANDSHAKE);
        //NPBuilder.setMacAddresses(0, "");
        NPBuilder.setPdu(wp.toByteString());
        NetworkPacket handshake = NPBuilder.build();

        write(handshake);
        NetworkPacket response = read();
        switch(response.getType()) {
            case NEW:
                // Maintain Connection
                this.router.addDownStream(this.socket);
                break;
            case DROP:
                // Clean up and remove Socket
                this.router.removeHandshaking(this.socket);
                this.socket.close();
                return;
            default:
                Log.e(TAG, "Bad handshake response");
                return;
        }
    }

    private NetworkPacket read() {
        try {
            return this.socket.read();
        } catch (IOException e) {
            Log.e(TAG, "Unable to read from socket");
            return null;
        }
    }

    private boolean write(NetworkPacket packet){
        try {
            return this.socket.write(packet);
        } catch (IOException e) {
            Log.e(TAG, String.format("Unable to write to socket: %s", e.getMessage()));
            return false;
        }
    }
}
