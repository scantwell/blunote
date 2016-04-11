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

import com.drexelsp.blunote.events.BluetoothEvent;

abstract public class ClientService extends Service {
    private String TAG = "ClientService";
    protected final NetworkServiceConnection mConnection = new NetworkServiceConnection();
    protected Receiver receiver = new Receiver(this);
    protected IBinder mBinder = null;

    abstract public void onReceived(byte[] data);

    abstract public void onNetworkEvent(BluetoothEvent bluetoothEvent);

    /**
     * send message across network
     * @param data to be sent
     */
    protected void send(byte[] data) {
        Log.v(TAG, "Sending message.");
        Message msg = Message.obtain(null, ClientHandler.SEND, 0, 0);
        Bundle bundle = new Bundle(1);
        bundle.putByteArray("data", data);
        msg.setData(bundle);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * start a new network and prepare for connections
     */
    protected void startNetwork() {
        Log.v(TAG, "Starting Network.");
        Message msg = Message.obtain(null, ClientHandler.START_NEW_NETWORK, 0, 0);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * connect to network and send message
     * @param macAddress to connect to
     */
    protected void connectToNetwork(String macAddress) {
        Log.v(TAG, String.format("Connecting To Network %s", macAddress));
        Message msg = Message.obtain(null, ClientHandler.CONNECT_TO_NETWORK, 0, 0);
        Bundle bundle = new Bundle(1);
        bundle.putString("MacAddress", macAddress);
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

    /**
     *
     * @param intent
     * @return mBinder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * log on created
     */
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

    /**
     * log on unbind
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "Unbinding user.");
        return true;
    }

    /**
     * log on rebind
     * @param intent
     */
    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "Rebind user.");
    }
}
