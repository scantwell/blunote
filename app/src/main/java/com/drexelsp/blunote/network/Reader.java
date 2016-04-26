package com.drexelsp.blunote.network;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Reader implements Runnable {

    private BlunoteInputStream inputStream;
    private ConcurrentLinkedQueue<byte[]> queue;

    public Reader(ConcurrentLinkedQueue<byte[]> queue, BlunoteInputStream inputStream)
    {
        this.queue = queue;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {

    }
}
