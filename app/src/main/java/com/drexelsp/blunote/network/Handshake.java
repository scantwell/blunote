package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.Pdu;
import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Handshake is a runnable that runs the host side of the handshaking process
 * using the BlunoteInputStream and BlunoteOutputStream from the BlunoteSocket
 * <p>
 * Created by omnia on 4/21/16.
 */
public class Handshake implements Runnable {
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

        DeliveryInfo.Builder dinfoBuilder = DeliveryInfo.newBuilder();
        dinfoBuilder.setTimestamp(System.currentTimeMillis());
        dinfoBuilder.setUsername(BluetoothAdapter.getDefaultAdapter().getAddress());

        Pdu.Builder pduBuilder = Pdu.newBuilder();
        pduBuilder.setDeliveryInfo(dinfoBuilder.build());
        pduBuilder.setData(ByteString.copyFrom(this.handshakePacket));

        NetworkPacket.Builder networkPacketBuilder = NetworkPacket.newBuilder();
        networkPacketBuilder.setNetworkMap(this.router.getNetworkMap());
        networkPacketBuilder.setPdu(pduBuilder.build());

        this.write(networkPacketBuilder.build().toByteArray());

        NetworkPacket response = this.read();
        if (response != null) {
            switch (response.getType()) {
                case NEW:
                    try {
                        this.router.addDownstream(this.socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case DROP:
                    this.close();
                    break;
                default:
                    Log.e(TAG, "Bad handshake response, closing connection");
                    this.close();
                    break;
            }
        }
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

    private int write(byte[] byteArray) {
        try {
            return this.outputStream.write(byteArray);
        } catch (IOException e) {
            Log.e(TAG, String.format("Unable to write to output stream: %s", e.getMessage()));
            return 0;
        }
    }
}
