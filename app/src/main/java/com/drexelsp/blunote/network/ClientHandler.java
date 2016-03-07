package com.drexelsp.blunote.network;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

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
    private final WeakReference<NetworkService> mService;
    private String TAG = "NetworkServiceClientHandler";

    public ClientHandler(NetworkService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
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
                Bundle data = msg.getData();
                String macAddress = data.getString("MacAddress");
                mService.get().connectToNetwork(macAddress);
                break;
            case START_NEW_NETWORK:
                Log.v(TAG, "Starting New Network");
                mService.get().startNetwork();
                break;
            default:
                Log.v(TAG, "Unknown message type, sending to parent handleMessage().");
                super.handleMessage(msg);
        }
    }
}
