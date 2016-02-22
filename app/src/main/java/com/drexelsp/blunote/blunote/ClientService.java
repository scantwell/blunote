package com.drexelsp.blunote.blunote;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


public class ClientService extends Service {

    public class LocalBinder extends Binder {
        ClientService getService() {
            return ClientService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();
    private final NetworkServiceConnection mConnection = new NetworkServiceConnection();
    private final Receiver receiver = new Receiver(this);
    private String TAG = "ClientService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onReceived(String data) {
        Log.v(TAG, "Received a message.");
    }

    // Sends to another application via bluetooth/etc
    public void send(String data) {
        Log.v(TAG, "Sending message.");
        Message msg;
        if (data == "StartNewNetwork") {
            msg = Message.obtain(null, ClientHandler.START_NEW_NETWORK, 0, 0);
        } else if (data == "DiscoverNetworks") {
            msg = Message.obtain(null, ClientHandler.GET_AVAILABLE_NETWORKS, 0, 0);
        } else if (data == "ConnectToNetwork") {
            msg = Message.obtain(null, ClientHandler.CONNECT_TO_NETWORK, 0, 0);
        } else {
            msg = Message.obtain(null, ClientHandler.SEND, 0, 0);
        }
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
