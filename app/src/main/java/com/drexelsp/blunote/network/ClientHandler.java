package com.drexelsp.blunote.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drexelsp.blunote.network.NetworkMessages.NetworkConfiguration;
import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by scantwell on 1/12/2016.
 * <p>
 * Handles messages from activities or binding service to the background service
 */
public class ClientHandler extends Handler {

    static public final int SEND_UPSTREAM = 1;
    static public final int SEND_DOWNSTREAM = 2;
    static public final int SONG_RECOMMENDATION = 3;
    static public final int CONNECT_TO_NETWORK = 4;
    static public final int START_NEW_NETWORK = 5;
    static public final int UPDATE_HANDSHAKE = 6;
    static public final int DISCONNECT = 7;
    private final WeakReference<NetworkService> mService;
    private String TAG = "NetworkServiceClientHandler";

    public ClientHandler(NetworkService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle b;
        byte[] data;
        switch (msg.what) {
            case SONG_RECOMMENDATION:
                msg.getData();
                break;
            case SEND_UPSTREAM:
                Log.v(TAG, "Sending upstream message.");
                b = msg.getData();
                data = b.getByteArray("data");
                mService.get().sendUpstream(data);
                break;
            case SEND_DOWNSTREAM:
                Log.v(TAG, "Sending downstream message.");
                b = msg.getData();
                data = b.getByteArray("data");
                mService.get().sendDownstream(data);
                break;
            case CONNECT_TO_NETWORK:
                Log.v(TAG, "Connect To Network");
                b = msg.getData();
                try {
                    NetworkConfiguration config = NetworkConfiguration.parseFrom(b.getByteArray("configuration"));
                    mService.get().connectToNetwork(config);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to connect to network. Could not parse 'NetworkConfiguration'.");
                }
                break;
            case START_NEW_NETWORK:
                Log.v(TAG, "Starting New Network");
                b = msg.getData();
                try {
                    NetworkConfiguration config = NetworkConfiguration.parseFrom(b.getByteArray("configuration"));
                    mService.get().startNetwork(config);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to start new network. Could not parse 'NetworkConfiguration'.");
                }
                break;
            case UPDATE_HANDSHAKE:
                b = msg.getData();
                byte[] handshake = b.getByteArray("handshake");
                this.mService.get().updateHandshake(ByteString.copyFrom(handshake));
                break;
            case DISCONNECT:
                this.mService.get().disconnect();
            default:
                Log.v(TAG, "Unknown message type, sending to parent handleMessage().");
                super.handleMessage(msg);
        }
    }
}
