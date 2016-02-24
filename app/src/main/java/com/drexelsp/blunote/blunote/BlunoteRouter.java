package com.drexelsp.blunote.blunote;

import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by omnia on 2/15/16.
 *
 * Router for Reading and Writing BlunoteSockets
 */
public class BlunoteRouter extends Thread {
    private static final String TAG = "Blunote Router";
    private BlunoteSocket upStream;
    private ArrayList<BlunoteSocket> downStream;
    private boolean awake;

    public BlunoteRouter() {
        Log.v(TAG, "Created");
        downStream = new ArrayList<>();
    }

    public void setUpStream(BlunoteSocket socket) {
        upStream = socket;
    }

    public void addDownStream(BlunoteSocket socket) {
        downStream.add(socket);
    }

    public void run() {
        Log.v(TAG, "Thread Started");
        awake = true;
        //noinspection InfiniteLoopStatement
        while(true) {
            // Read Message from UpStream
            if (upStream.numMessages() > 0) {
                Message msg = upStream.readMessage();

                // Deliver message to application

                // Echo in downstream
                for (BlunoteSocket sock : downStream) {
                    sock.writeMessage(msg);
                }
            }

            // Echo Messages from DownStream
            for (BlunoteSocket sock : downStream) {
                if (sock.numMessages() > 0) {
                    Message msg = sock.readMessage();
                    upStream.writeMessage(msg);
                }
            }

            Log.v(TAG, "Going to sleep now");
            napTime();
            Log.v(TAG, "Waking up to do work");
        }
    }

    public synchronized void wakeUp() {
        if (!awake) {
            notify();
        }
    }

    private synchronized void napTime() {
        try{
            awake = false;
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
