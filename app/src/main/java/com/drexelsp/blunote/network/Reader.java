package com.drexelsp.blunote.network;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Reader implements Runnable {

    private BluetoothInputStream inputStream;
    private ConcurrentLinkedQueue<byte[]> queue;

    public Reader(ConcurrentLinkedQueue<byte[]> queue, BluetoothInputStream inputStream)
    {
        this.queue = queue;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {

    }
}
