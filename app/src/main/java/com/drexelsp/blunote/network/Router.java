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
    private CopyOnWriteArrayList<BlunoteOutputStream> upstreamOuts;
    private CopyOnWriteArrayList<BlunoteOutputStream> downstreamOuts;


    public Router(BlunoteMessages.NetworkConfiguration config)
    {
        this.upstreamOuts = new CopyOnWriteArrayList<>();
        this.downstreamOuts = new CopyOnWriteArrayList<>();
        this.upBucket = new ConcurrentLinkedQueue<>();
        this.downBucket = new ConcurrentLinkedQueue<>();
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();
        writers.add(new Writer(downBucket, downstreamOuts));
        writers.add(new Writer(upBucket, upstreamOuts));
    }

    public void addUpstream(BlunoteSocket socket)
    {
        this.addConnection(socket, downBucket, upstreamOuts);
    }

    public void addDownstream(BlunoteSocket socket)
    {
        this.addConnection(socket, upBucket, downstreamOuts);
    }

    private void addConnection(BlunoteSocket socket, ConcurrentLinkedQueue<byte[]> bucket, CopyOnWriteArrayList<BlunoteOutputStream> streams)
    {
        BlunoteOutputStream out = socket.getOutputStream();
        BlunoteInputStream in = socket.getInputStream();
        streams.add(out);
        readers.add(new Reader(bucket, in));
    }

    public void addUpstreamMessage(byte[])
    {

    }

    public void addDownstreamMessage(byte[])
    {

    }
}
