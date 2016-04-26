package com.drexelsp.blunote.network;

import com.drexelsp.blunote.blunote.BlunoteMessages;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Router {

    private ArrayList<Reader> readers;
    private ArrayList<Writer> writers;
    private ConcurrentLinkedQueue<byte[]> upBucket;
    private ConcurrentLinkedQueue<byte[]> downBucket;
    private CopyOnWriteArrayList<BluetoothOutputStream> upstreamOuts;
    private CopyOnWriteArrayList<BluetoothOutputStream> downstreamOuts;


    public Router(BlunoteMessages.NetworkConfiguration config)
    {

    }

    public void addUpstream(BluetoothSocket)


}
