package com.drexelsp.blunote.network;

import android.telecom.Call;

import com.drexelsp.blunote.blunote.BlunoteMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Router extends Thread {

    private ArrayList<Reader> readers;
    private ArrayList<Writer> writers;
    private ConcurrentLinkedQueue<byte[]> upBucket;
    private ConcurrentLinkedQueue<byte[]> downBucket;
    private CopyOnWriteArrayList<BlunoteSocket> upSockets;
    private CopyOnWriteArrayList<BlunoteSocket> downSockets;
    private CopyOnWriteArrayList<BlunoteOutputStream> upOuts;
    private CopyOnWriteArrayList<BlunoteOutputStream> downOuts;
    private boolean isRunning;
    private Callback downstreamCallback;
    private Callback upstreamCallback;


    public Router()
    {
        this.downstreamCallback = null;
        this.upstreamCallback = null;
        this.isRunning = false;
        this.upSockets = new CopyOnWriteArrayList<>();
        this.downSockets = new CopyOnWriteArrayList<>();
        this.upBucket = new ConcurrentLinkedQueue<>();
        this.downBucket = new ConcurrentLinkedQueue<>();
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();
        writers.add(new Writer(this));
        writers.add(new Writer(this));
    }

    public void start()
    {
        super.start();
        this.isRunning = true;
    }

    public void shutdown()
    {
        this.isRunning = false;
    }

    public void registerUpstream(Callback cb) {
        this.upstreamCallback = cb;
    }

    public void registerDownstream(Callback cb) {
        this.downstreamCallback = cb;
    }

    public void addUpstream(BlunoteSocket socket)
    {
        new Thread(new Reader(new UpstreamCallback(this), socket)).start();
        upSockets.add(socket);
        upOuts.add(socket.getOutputStream());
    }

    public void addDownstream(BlunoteSocket socket)
    {
        new Thread(new Reader(new DownstreamCallback(this), socket)).start();
        downSockets.add(socket);
        downOuts.add(socket.getOutputStream());
    }


    public synchronized void addUpstreamMessage(byte[] data)
    {
        this.downBucket.add(data);
        notifyAll();
    }

    public synchronized void addDownstreamMessage(byte[] data)
    {
        this.upBucket.add(data);
        notifyAll();
    }

    @Override
    public void run() {
        while(this.isRunning) {
            waitForMessages();
            if (downBucket.size() > 0) {
                byte[] data = downBucket.remove();
                this.sendDownstream(data);
            }
            if (upBucket.size() > 0) {
                byte[] data = upBucket.remove();
                this.sendUpstream(data);
            }
        }
        //clean up
    }
    protected synchronized void waitForMessages() {
        while (downBucket.size() < 1 && upBucket.size() < 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDrop(BlunoteSocket socket)
    {

    }

    private void sendDownstream(byte[] data)
    {
        if (this.downstreamCallback != null) {
            this.downstreamCallback.onReceivePacket(data);
        }
        send(data, downOuts);
    }

    private void sendUpstream(byte[] data)
    {
        if (this.upstreamCallback != null) {
            this.upstreamCallback.onReceivePacket(data);
        }
        send(data, upOuts);
    }

    private void send(byte[] data, CopyOnWriteArrayList<BlunoteOutputStream> outs)
    {
        for (int i = 0; i < outs.size(); i++)
        {
            try {
                outs.get(i).write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}