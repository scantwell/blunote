package com.drexelsp.blunote.network;

import android.telecom.Call;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages;
import com.drexelsp.blunote.events.OnConnectionEvent;
import com.drexelsp.blunote.events.OnDisconnectionEvent;
import com.drexelsp.blunote.events.OnReceiveDownstream;
import com.drexelsp.blunote.events.OnReceiveUpstream;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Router extends Thread {

    private static String TAG = "Router";
    private ConcurrentLinkedQueue<byte[]> upBucket;
    private ConcurrentLinkedQueue<byte[]> downBucket;
    private CopyOnWriteArrayList<BlunoteSocket> upSockets;
    private CopyOnWriteArrayList<BlunoteSocket> downSockets;
    private CopyOnWriteArrayList<BlunoteOutputStream> upOuts;
    private CopyOnWriteArrayList<BlunoteOutputStream> downOuts;
    private boolean isRunning;
    private Callback downstreamCallback;
    private Callback upstreamCallback;
    private ArrayList<String> networkList;
    private boolean notifyOnReceiveUpstream;
    private boolean notifyOnReceiveDownstream;
    private boolean notifyOnDisconnect;
    private boolean notifyOnConnect;


    public Router()
    {
        this.downstreamCallback = null;
        this.upstreamCallback = null;
        this.isRunning = false;
        this.upSockets = new CopyOnWriteArrayList<>();
        this.downSockets = new CopyOnWriteArrayList<>();
        this.upBucket = new ConcurrentLinkedQueue<>();
        this.downBucket = new ConcurrentLinkedQueue<>();
        this.networkList = new ArrayList<>();
    }

    public void start()
    {
        Log.d(TAG, "Starting router.");
        super.start();
        this.isRunning = true;
    }

    public void shutdown()
    {

        Log.d(TAG, "Shutting down router.");
        this.isRunning = false;
    }

    public void setNotifyOnReceiveUpstream(boolean notifyOnReceiveUpstream) {
        this.notifyOnReceiveUpstream = notifyOnReceiveUpstream;
    }

    public void setNotifyOnReceiveDownstream(boolean notifyOnReceiveDownstream) {
        this.notifyOnReceiveDownstream = notifyOnReceiveDownstream;
    }

    public void setNotifyOnDisconnect(boolean notifyOnDisconnect) {
        this.notifyOnDisconnect = notifyOnDisconnect;
    }

    public void setNotifyOnConnect(boolean notifyOnConnect) {
        this.notifyOnConnect = notifyOnConnect;
    }

    protected void addUpstream(BlunoteSocket socket)
    {
        networkList.add(socket.getAddress());
        new Thread(new Reader(new UpstreamCallback(this), socket)).start();
        upSockets.add(socket);
        upOuts.add(socket.getOutputStream());
    }

    protected void addDownstream(BlunoteSocket socket)
    {
        this.onConnection(socket);
        new Thread(new Reader(new DownstreamCallback(this), socket)).start();
        downSockets.add(socket);
        downOuts.add(socket.getOutputStream());
    }

    private void onConnection(BlunoteSocket socket)
    {
        networkList.add(socket.getAddress());
        if (notifyOnConnect)
        {
            OnConnectionEvent event = new OnConnectionEvent(socket.getAddress());
            EventBus.getDefault().post(event);
        }
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
        cleanUp();
    }

    private void onDisconnect(BlunoteSocket socket)
    {
        if (notifyOnDisconnect)
        {
            OnDisconnectionEvent event = new OnDisconnectionEvent(socket.getAddress());
            EventBus.getDefault().post(event);
        }
        for (int i = 0; i < networkList.size(); i++)
        {
            if (networkList.get(i).equals(socket.getAddress()))
            {
                networkList.remove(i);
            }
        }
    }

    private void cleanUp() {
        Log.d(TAG, "Closing all open connections.");
        closeConnections(upSockets);
        closeConnections(downSockets);
    }

    private void closeConnections(CopyOnWriteArrayList<BlunoteSocket> sockets)
    {
        for (BlunoteSocket s : sockets)
        {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void sendDownstream(byte[] data)
    {
        if (notifyOnReceiveDownstream)
        {
            OnReceiveDownstream event = new OnReceiveDownstream(data);
            EventBus.getDefault().post(event);
            this.downstreamCallback.onReceivePacket(data);
        }
        send(data, downOuts);
    }

    private void sendUpstream(byte[] data)
    {
        if (notifyOnReceiveUpstream) {
            OnReceiveUpstream event = new OnReceiveUpstream(data);
            EventBus.getDefault().post(event);
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