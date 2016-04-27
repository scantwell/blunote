package com.drexelsp.blunote.network;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.util.ArrayList;

/**
 * Created by omnia on 2/15/16.
 * <p>
 * Router for Reading and Writing BlunoteSockets
 */
/*
public class BlunoteRouter extends Thread {
    private static final String TAG = "Blunote Router";
    private static BlunoteRouter instance = new BlunoteRouter();

    private BlunoteSocket upStream;
    private ArrayList<BlunoteSocket> downStream = new ArrayList<>();
    private ArrayList<BlunoteSocket> handShaking = new ArrayList<>();

    private ArrayList<Pair<BlunoteSocket, NetworkPacket>> mailbox = new ArrayList<>();
    private NetworkPacket handshakePacket;
    private final Object lock = new Object();

    private Context applicationContext;
    private boolean isHost;
    private boolean awake;

    private BlunoteRouter() {
    }

    public static BlunoteRouter getInstance() {
        return instance;
    }

    public void send(Message msg) {
        NetworkPacket packet;
        NetworkPacket.Builder builder = NetworkPacket.newBuilder();
        builder.setPdu(ByteString.copyFrom(msg.getData().getByteArray("data")));
        if (isHost) {
            builder.setType(NetworkPacket.Type.DOWNSTREAM);
            packet = builder.build();
            for (BlunoteSocket socket : downStream) {
                socket.write(packet);
            }
        } else {
            builder.setType(NetworkPacket.Type.UPSTREAM);
            packet = builder.build();
            upStream.write(packet);
        }
    }

    public void addPacketToQueue(BlunoteSocket sender, NetworkPacket networkPacket) {
        synchronized (lock) {
            mailbox.add(new Pair<>(sender, networkPacket));
        }
    }

    public void setHostMode(Context context) {
        applicationContext = context;
        isHost = true;
        this.start();
    }

    public void setClientMode(Context context) {
        applicationContext = context;
        isHost = false;
        this.start();
    }

    public void setUpStream(BlunoteSocket socket) {
        Log.v(TAG, "New Up Stream Set");
        upStream = socket;
    }

    public void addHandshaking(BlunoteSocket socket) {
        Log.v(TAG, "New handshaking added");
        handShaking.add(socket);
        socket.write(handshakePacket);
    }

    public boolean addDownStream(BlunoteSocket socket) {
        if (removeHandshaking(socket)) {
            Log.v(TAG, "Handshaking completed, new client connected");
            downStream.add(socket);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeHandshaking(BlunoteSocket socket) {
        return handShaking.remove(socket);
    }

    public void run() {
        awake = true;

        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (lock) {
                if (mailbox.size() > 0 && upStream != null) {
                    Pair<BlunoteSocket, NetworkPacket> pair = mailbox.get(0);
                    BlunoteSocket sender = pair.first;
                    NetworkPacket networkPacket = pair.second;
                    processNetworkPacket(sender, networkPacket);
                }
            }
        }
    }

    private void processNetworkPacket(BlunoteSocket sender, NetworkPacket packet) {
        NetworkPacket.Type type = packet.getType();
        switch (type) {
            case DOWNSTREAM:
                if (isHost) {
                    Log.v(TAG, "Host received a downstream message. This should never happen");
                } else {
                    sendMessageToApplication(packet.getPdu().toByteArray());
                    for (BlunoteSocket socket : downStream) {
                        socket.write(packet);
                    }
                }
                break;
            case UPSTREAM:
                if (isHost) {
                    sendMessageToApplication(packet.getPdu().toByteArray());
                } else {
                    upStream.write(packet);
                }
                break;
            case NETWORK_DATA_UPDATE:
                handshakePacket = packet;
                for (BlunoteSocket socket : downStream) {
                    socket.write(packet);
                }
                break;
            case HANDSHAKE:
                break;
            case DROP:
                removeHandshaking(sender);
                break;
            case NEW:
                addDownStream(sender);
                break;
            default:
                Log.e(TAG, "Network packet type not recognized");
                break;
        }
    }

    public synchronized void wakeUp() {
        if (!awake) {
            notify();
        }
    }

    private void sendMessageToApplication(byte[] bytes) {
        Intent intent = new Intent();
        intent.setAction("networkservice.onrecieved");
        intent.putExtra("Type", "MessageReceived");
        intent.putExtra("Data", bytes);
        applicationContext.sendBroadcast(intent);
    }
}
*/