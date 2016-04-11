package com.drexelsp.blunote.network;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class NetworkServiceConnection implements ServiceConnection {

    Messenger mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;

    /**
     * reset local values to be in connected state
     * @param className
     * @param service
     */
    public void onServiceConnected(ComponentName className, IBinder service) {
        // This is called when the connection with the service has been
        // established, giving us the object we can use to
        // interact with the service.  We are communicating with the
        // service using a Messenger, so here we get a client-side
        // representation of that from the raw IBinder object.
        mService = new Messenger(service);
        mBound = true;
    }

    /**
     * reset local values to be in disconnected state
     * @param className
     */
    public void onServiceDisconnected(ComponentName className) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        mService = null;
        mBound = false;
    }

    /**
     * send message to remote device
     * @param msg to be sent
     * @throws RemoteException exception thrown from remote device
      */
    public void send(Message msg) throws RemoteException {
        if (mBound) {
            mService.send(msg);
        }
    }
}