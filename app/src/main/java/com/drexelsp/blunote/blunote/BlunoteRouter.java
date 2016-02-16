package com.drexelsp.blunote.blunote;

import android.os.Message;

import java.util.ArrayList;

/**
 * Created by omnia on 2/15/16.
 */
public class BlunoteRouter extends Thread {
    private BlunoteSocket upStream;
    private ArrayList<BlunoteSocket> downStream;
    private boolean awake;

    public BlunoteRouter() {

    }

    public void setUpStream(BlunoteSocket socket) {
        upStream = socket;
    }

    public void addDownStream(BlunoteSocket socket) {
        downStream.add(socket);
    }

    public void run() {
        awake = true;
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

            napTime();
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
