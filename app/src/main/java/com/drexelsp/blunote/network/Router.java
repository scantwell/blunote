package com.drexelsp.blunote.network;

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
    private boolean notifyOnDisconnectUpstream;
    private boolean notifyOnDisconnectDownstream;
    private boolean notifyOnConnectUpstream;

    private boolean notifyOnConnectDownstream;


    public Router(String address) {
        this.downstreamCallback = null;
        this.upstreamCallback = null;
        this.isRunning = false;
        this.upSockets = new CopyOnWriteArrayList<>();
        this.downSockets = new CopyOnWriteArrayList<>();
        this.upBucket = new ConcurrentLinkedQueue<>();
        this.downBucket = new ConcurrentLinkedQueue<>();
        this.networkList = new ArrayList<>();
        this.networkList.add(address);
    }

    public void start() {
        Log.d(TAG, "Starting router.");
        super.start();
        this.isRunning = true;
    }

    public void shutdown() {

        Log.d(TAG, "Shutting down router.");
        this.isRunning = false;
    }

    public void setNotifyOnReceiveUpstream(boolean notifyOnReceiveUpstream) {
        this.notifyOnReceiveUpstream = notifyOnReceiveUpstream;
    }

    public void setNotifyOnReceiveDownstream(boolean notifyOnReceiveDownstream) {
        this.notifyOnReceiveDownstream = notifyOnReceiveDownstream;
    }

    public void setNotifyOnConnectDownstream(boolean notifyOnConnectDownstream) {
        this.notifyOnConnectDownstream = notifyOnConnectDownstream;
    }

    public void setNotifyOnConnectUpstream(boolean notifyOnConnectUpstream) {
        this.notifyOnConnectUpstream = notifyOnConnectUpstream;
    }

    public void setNotifyOnDisconnectDownstream(boolean notifyOnDisconnectDownstream) {
        this.notifyOnDisconnectDownstream = notifyOnDisconnectDownstream;
    }

    public void setNotifyOnDisconnectUpstream(boolean notifyOnDisconnectUpstream) {
        this.notifyOnDisconnectUpstream = notifyOnDisconnectUpstream;
    }

    protected void addUpstream(BlunoteSocket socket) throws IOException {
        Log.d(TAG, String.format("Adding upstream connection with address: %s", socket.getAddress()));
        postOnUpstreamConnection(socket);
        networkList.add(socket.getAddress());
        new Thread(new Reader(new UpstreamCallback(this), socket)).start();
        upSockets.add(socket);
        upOuts.add(socket.getOutputStream());
    }

    protected void addDownstream(BlunoteSocket socket) throws IOException {
        Log.d(TAG, String.format("Adding downstream connection with address: %s", socket.getAddress()));
        postOnDownstreamConnection(socket);
        networkList.add(socket.getAddress());
        new Thread(new Reader(new DownstreamCallback(this), socket)).start();
        downSockets.add(socket);
        downOuts.add(socket.getOutputStream());
    }

    private void postOnUpstreamConnection(BlunoteSocket socket) {
        if (notifyOnConnectUpstream) {
            Log.d(TAG, String.format("Posting OnConnectionEvent for upstream connection with address: %s", socket.getAddress()));
            OnConnectionEvent event = new OnConnectionEvent(OnConnectionEvent.UPSTREAM, socket.getAddress());
            EventBus.getDefault().post(event);
        }
    }

    private void postOnDownstreamConnection(BlunoteSocket socket) {
        if (notifyOnConnectDownstream) {
            Log.d(TAG, String.format("Posting OnConnectionEvent for downstream connection with address: %s", socket.getAddress()));
            OnConnectionEvent event = new OnConnectionEvent(OnConnectionEvent.DOWNSTREAM, socket.getAddress());
            EventBus.getDefault().post(event);
        }
    }


    public synchronized void addUpstreamMessage(byte[] data) {
        Log.d(TAG, String.format("Storing upstream message."));
        this.downBucket.add(data);
        notifyAll();
    }

    public synchronized void addDownstreamMessage(byte[] data) {
        Log.d(TAG, String.format("Storing downstream message."));
        this.upBucket.add(data);
        notifyAll();
    }

    @Override
    public void run() {
        while (this.isRunning) {
            waitForMessages();
            if (downBucket.size() > 0) {
                Log.d(TAG, "Processing downstream message queue.");
                byte[] data = downBucket.remove();
                this.sendDownstream(data);
            }
            if (upBucket.size() > 0) {
                Log.d(TAG, "Processing upstream message queue.");
                byte[] data = upBucket.remove();
                this.sendUpstream(data);
            }
        }
        cleanUp();
    }

    public BlunoteMessages.NetworkMap getNetworkMap()
    {
        BlunoteMessages.NetworkMap.Builder builder = BlunoteMessages.NetworkMap.newBuilder();
        builder.addAllMacAddresses(networkList);
        return builder.build();
    }

    private void removeSocketFromNetworkList(BlunoteSocket socket) {

        for (int i = 0; i < networkList.size(); i++) {
            if (networkList.get(i).equals(socket.getAddress())) {
                Log.d(TAG, String.format("Removing socket from network list with address %s.", socket.getAddress()));
                networkList.remove(i);
            }
        }
    }

    private void postOnDownstreamDisconnection(BlunoteSocket socket) {
        if (notifyOnDisconnectDownstream) {
            Log.d(TAG, String.format("Posting OnDisconnectionEvent for downstream connection with address: %s", socket.getAddress()));
            OnDisconnectionEvent event = new OnDisconnectionEvent(OnDisconnectionEvent.DOWNSTREAM, socket.getAddress());
            EventBus.getDefault().post(event);
        }
        removeSocketFromNetworkList(socket);
    }

    private void postOnUpstreamDisconnection(BlunoteSocket socket) {
        if (notifyOnDisconnectUpstream) {
            Log.d(TAG, String.format("Posting OnDisconnectionEvent for upstream connection with address: %s", socket.getAddress()));
            OnDisconnectionEvent event = new OnDisconnectionEvent(OnDisconnectionEvent.UPSTREAM, socket.getAddress());
            EventBus.getDefault().post(event);
        }
        removeSocketFromNetworkList(socket);
    }

    private void cleanUp() {
        Log.d(TAG, "Closing all open connections.");
        closeConnections(upSockets);
        closeConnections(downSockets);
    }

    private void closeConnections(CopyOnWriteArrayList<BlunoteSocket> sockets) {
        for (BlunoteSocket s : sockets) {
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
                Log.d(TAG, String.format("Router thread going to sleep, awaiting incoming messages."));
                wait();
                Log.d(TAG, String.format("Router thread awake."));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDownstream(byte[] data) {
        Log.d(TAG, String.format("Sending downstream message."));
        send(data, downOuts);
        if (notifyOnReceiveDownstream) {
            Log.d(TAG, String.format("Posting OnReceiveDownstream for message."));
            OnReceiveDownstream event = new OnReceiveDownstream(data);
            EventBus.getDefault().post(event);
            this.downstreamCallback.onReceivePacket(data);
        }
    }

    private void sendUpstream(byte[] data) {
        Log.d(TAG, String.format("Sending upstream message."));
        send(data, upOuts);
        if (notifyOnReceiveUpstream) {
            Log.d(TAG, String.format("Posting OnReceiveUpstream for messsage"));
            OnReceiveUpstream event = new OnReceiveUpstream(data);
            EventBus.getDefault().post(event);
            this.upstreamCallback.onReceivePacket(data);
        }
    }

    private void send(byte[] data, CopyOnWriteArrayList<BlunoteOutputStream> outs) {
        for (int i = 0; i < outs.size(); i++) {
            try {
                outs.get(i).write(data);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}