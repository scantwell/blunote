package com.drexelsp.blunote.network;

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

abstract public class ClientService extends Service {
    private String TAG = "ClientService";
    protected final NetworkServiceConnection mConnection = new NetworkServiceConnection();
    protected Receiver receiver = new Receiver(this);;
    protected IBinder mBinder = null;

    abstract public void onReceived(byte[] data);

    // Sends to another application via bluetooth/etc
    protected void send(byte[] data) {
        Log.v(TAG, "Sending message.");
        Message msg = Message.obtain(null, ClientHandler.SEND, 0, 0);
        try {
            mConnection.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void setBinder(IBinder mBinder)
    {
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

        Intent intent = new Intent(this, NetworkService.class);
        startService(intent);

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
