package com.drexelsp.blunote.network;

import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.*;
import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by scantwell on 1/12/2016.
 * <p/>
 * Handles messages from activities or binding service to the background service
 */
public class ClientHandler extends Handler {

    static public final int SEND = 1;
    static public final int SONG_RECOMMENDATION = 2;
    static public final int GET_AVAILABLE_NETWORKS = 3;
    static public final int CONNECT_TO_NETWORK = 4;
    static public final int START_NEW_NETWORK = 5;
    static public final int UPDATE_HANDSHAKE = 6;
    private final WeakReference<NetworkService> mService;
    private String TAG = "NetworkServiceClientHandler";

    public ClientHandler(NetworkService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle b;
        switch (msg.what) {
            case SONG_RECOMMENDATION:
                msg.getData();
                break;
            case SEND:
                Log.v(TAG, "Sending message.");
                mService.get().send(msg);
                break;
            case GET_AVAILABLE_NETWORKS:
                Log.v(TAG, "Get Available Networks");
                mService.get().getAvailableNetworks();
                break;
            case CONNECT_TO_NETWORK:
                Log.v(TAG, "Connect To Network");
                b = msg.getData();
                try {
                    NetworkConfiguration config = serializeConfiguration(b.get("configuration"));
                    mService.get().connectToNetwork(config);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to connect to network. Could not parse 'NetworkConfiguration'.");
                }
                break;
            case START_NEW_NETWORK:
                Log.v(TAG, "Starting New Network");
                b = msg.getData();
                b.get("configuration");
                try {
                    NetworkConfiguration config = serializeConfiguration(b.get("configuration"));
                    mService.get().startNetwork(config);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to start new network. Could not parse 'NetworkConfiguration'.");
                }
                break;
            case UPDATE_HANDSHAKE:
                b = msg.getData();
                try {
                    byte[] handshake = serialize(b.get("handshake"));
                    this.mService.get().updateHandshake(ByteString.copyFrom(handshake));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to update handshake content. Could not serialize.");
                }
                break;
            default:
                Log.v(TAG, "Unknown message type, sending to parent handleMessage().");
                super.handleMessage(msg);
        }
    }

    public static NetworkConfiguration serializeConfiguration(Object obj) throws IOException{
        NetworkConfiguration config = NetworkConfiguration.parseFrom(serialize(obj));
        return config;
    }

    public static byte[] serialize(Object obj) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
}
