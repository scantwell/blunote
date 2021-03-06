package com.drexelsp.blunote.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkConfiguration;
import com.drexelsp.blunote.events.BluetoothEvent;

abstract public class ClientService extends Service {
    private String TAG = "ClientService";
    protected final NetworkServiceConnection mConnection = new NetworkServiceConnection();
    protected Receiver receiver = new Receiver(this);
    protected IBinder mBinder = null;

    abstract public void onConnectionDownstream(String address);

    abstract public void onConnectionUpstream(String address);

    abstract public void onReceiveDownstream(byte[] data);

    abstract public void onReceiveUpstream(byte[] data);

    abstract public void onDisconnectionDownstream(String address);

    abstract public void onDisconnectionUpstream(String address);

    // Sends to another application via bluetooth/etc
    protected void sendUpstream(byte[] data) { this.send(ClientHandler.SEND_UPSTREAM, data); }

    // Sends to another application via bluetooth/etc
    protected void sendDownstream(byte[] data) {
        this.send(ClientHandler.SEND_DOWNSTREAM, data);
    }

    // Sends to another application via bluetooth/etc
    private void send(int direction, byte[] data) {
        Log.v(TAG, "Sending message.");
        Message msg;
        if ( direction == ClientHandler.SEND_DOWNSTREAM )
        {
            msg = Message.obtain(null, ClientHandler.SEND_DOWNSTREAM, 0, 0);
        }
        else
        {
            msg = Message.obtain(null, ClientHandler.SEND_UPSTREAM, 0, 0);
        }

        Bundle bundle = new Bundle(1);
        bundle.putByteArray("data", data);
        msg.setData(bundle);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void startNetwork(NetworkConfiguration config) {
        Log.v(TAG, "Starting Network.");
        Message msg = Message.obtain(null, ClientHandler.START_NEW_NETWORK, 0, 0);
        Bundle b = new Bundle();
        b.putByteArray("configuration", config.toByteArray());
        msg.setData(b);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void connectToNetwork(NetworkConfiguration config) {
        //Log.v(TAG, String.format("Connecting To Network %s", macAddress));
        Message msg = Message.obtain(null, ClientHandler.CONNECT_TO_NETWORK, 0, 0);
        Bundle bundle = new Bundle(1);
        bundle.putByteArray("configuration", config.toByteArray());
        msg.setData(bundle);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        Message msg = Message.obtain(null, ClientHandler.DISCONNECT, 0, 0);
        Bundle bundle = new Bundle(1);
        msg.setData(bundle);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateHandshake(byte[] handshake) {
        Message msg = Message.obtain(null, ClientHandler.UPDATE_HANDSHAKE, 0, 0);
        Bundle bundle = new Bundle(1);
        bundle.putByteArray("handshake", handshake);
        msg.setData(bundle);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void setBinder(IBinder mBinder) {
        this.mBinder = mBinder;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Created service.");

        IntentFilter intentFilter = new IntentFilter("networkservice.onrecieved");
        registerReceiver(receiver, intentFilter);

        bindService(new Intent(this, NetworkService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        unbindService(mConnection);
        Log.v(TAG, "Destroyed service.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If the service is stopped it will be started again with original intent
        // Needed for determining the type of mesh network
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "Unbinding user.");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "Rebind user.");
    }
}
