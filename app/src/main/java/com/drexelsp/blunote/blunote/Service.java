package com.drexelsp.blunote.blunote;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.drexelsp.blunote.network.ClientService;

/**
 * Created by scantwell on 2/15/2016.
 */
public class Service extends ClientService {
    public class LocalBinder extends Binder {
        public Service getService() {
            return Service.this;
        }
    }

    public final int ALBUM = 1;
    public final int ARTISTS = 2;
    public final int GENRE = 3;
    public final int SONG = 4;

    private String TAG = "Service";
    private final IBinder mBinder = new LocalBinder();

    public Service()
    {
        super.setBinder(mBinder);
    }

    @Override
    public void onReceived(String data)
    {
        Log.v(TAG, "Received a message.");
    }

    public void sendRequest(String data, int type)
    {
        switch(type)
        {
            case ALBUM:
                break;
            case ARTISTS:
                break;
            case GENRE:
                break;
            case SONG:
                break;
            default:
                Log.v(TAG, "Unrecognized request type.");
                break;
        }
    }
}