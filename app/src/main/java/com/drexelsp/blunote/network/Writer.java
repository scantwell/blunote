package com.drexelsp.blunote.network;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Writer implements Runnable {

    private CopyOnWriteArrayList<BluetoothOutputStream> outs;
    private ConcurrentLinkedQueue<byte[]> queue;

    public Writer(ConcurrentLinkedQueue queue, CopyOnWriteArrayList<BluetoothOutputStream> outs)
    {
        this.queue = queue;
        this.outs = outs;
    }

    @Override
    public void run() {

    }
}
