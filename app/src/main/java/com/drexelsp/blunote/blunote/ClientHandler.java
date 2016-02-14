package com.drexelsp.blunote.blunote;

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
            default:
                Log.v(TAG, "Unknown message type, sending to parent handleMessage().");
                super.handleMessage(msg);
        }
    }
}
