package com.drexelsp.blunote.blunote;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ClientServiceConnection implements ServiceConnection {

    ClientService mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;


    public void onServiceConnected(ComponentName className, IBinder service) {
        // This is called when the connection with the service has been
        // established, giving us the object we can use to
        // interact with the service.  We are communicating with the
        // service using a Messenger, so here we get a client-side
        // representation of that from the raw IBinder object.
        mService = ((ClientService.LocalBinder) service).getService();
        mBound = true;
    }

    public void onServiceDisconnected(ComponentName className) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        mService = null;
        mBound = false;
    }

    public void send(String data) {
        if (mBound) {
            mService.send(data);
        }
    }
}
