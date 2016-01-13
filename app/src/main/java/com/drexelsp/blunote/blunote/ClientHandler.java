package com.drexelsp.blunote.blunote;

import android.os.Message;
import android.util.Log;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by scantwell on 1/12/2016.
 *
 * Handles messages from activities or binding service to the background service
 */
public class ClientHandler extends Handler {

    static public final int SEND = 1;
    private String TAG = "NetworkServiceClientHandler";

    @Override
    public void handleMessage(Message msg)
    {

        switch (msg.what) {
            case SEND:
                Log.v(TAG, "Sending message.");
                break;
            default:
                Log.v(TAG, "Unknown message type, sending to parent handleMessage().");
                super.handleMessage(msg);
        }
    }
}
