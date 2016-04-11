package com.drexelsp.blunote.network;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by omnia on 2/15/16.
 * Router for Reading and Writing BlunoteSockets
 */
public class BlunoteRouter extends Thread {
    private static final String TAG = "Blunote Router";
    private static BlunoteRouter instance = new BlunoteRouter();
    private BlunoteSocket upStream;
    private ArrayList<BlunoteSocket> downStream = new ArrayList<>();
    private Context applicationContext;
    private boolean isHost;
    private boolean awake;

    private BlunoteRouter() {
    }

    public static BlunoteRouter getInstance() {
        return instance;
    }

    /**
     * send message across socket
     * @param msg
     */
    public void send(Message msg) {
        if (isHost) {
            for (BlunoteSocket socket : downStream) {
                socket.writeMessage(msg);
            }
        } else {
            upStream.writeMessage(msg);
        }
    }

    /**
     * start the router as host
     * @param context for application
     */
    public void setHostMode(Context context) {
        applicationContext = context;
        isHost = true;
        this.start();
    }

    /**
     * start the router as client
     * @param context for application
     */
    public void setClientMode(Context context) {
        applicationContext = context;
        isHost = false;
        this.start();
    }

    /**
     * add new connection pointing to host/server
     * @param socket that is upstream
     */
    public void setUpStream(BlunoteSocket socket) {
        Log.v(TAG, "New Up Stream Set");
        upStream = socket;
    }

    /**
     * add new connection pointing away from host/server
     * @param socket that is upstream
     */
    public void addDownStream(BlunoteSocket socket) {
        Log.v(TAG, "New Down Stream Added");
        downStream.add(socket);
    }

    /**
     * start thread
     */
    public void run() {
        awake = true;
        doWork();
    }

    /**
     * start the router and continually listen
     */
    public synchronized  void doWork() {
        //noinspection InfiniteLoopStatement
        while (true) {

            if (isHost) {
                // Host Mode
                for (BlunoteSocket socket : downStream) {
                    while (socket.numMessages() > 0) {
                        Log.v(TAG, "Reading Message from DownStream");
                        Message msg = socket.readMessage();
                        sendMessageToApplication(msg);
                    }
                }

            } else {
                // Client Mode
                if (upStream != null && upStream.numMessages() > 0) {
                    Log.v(TAG, "Reading Message from UpStream");
                    Message msg = upStream.readMessage();
                    sendMessageToApplication(msg);

                    for (BlunoteSocket socket : downStream) {
                        socket.writeMessage(msg);
                    }
                }

                for (BlunoteSocket socket : downStream) {
                    if (socket.numMessages() > 0) {
                        Log.v(TAG, "Reading Message from DownStream");
                        Message msg = socket.readMessage();
                        if (upStream != null) {
                            upStream.writeMessage(msg);
                        } else {
                            Log.e(TAG, "Unable to echo message from DownStream. UpStream not set");
                        }
                    }
                }
            }

            Log.v(TAG, "Going to sleep now");
            try {
                awake = false;
                wait();
                awake = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "Waking up to do work");
        }
    }

    /**
     * wake up the router
     */
    public synchronized void wakeUp() {
        if (!awake) {
            notify();
        }
    }

    /**
     * put message for main process to recive
     * @param msg
     */
    private void sendMessageToApplication(Message msg) {
        Intent intent = new Intent();
        intent.setAction("networkservice.onrecieved");
        intent.putExtra("Type", "MessageReceived");
        intent.putExtra("Data", msg.getData().getByteArray("data"));
        applicationContext.sendBroadcast(intent);
    }
}
