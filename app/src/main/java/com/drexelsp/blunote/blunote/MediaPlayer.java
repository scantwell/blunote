package com.drexelsp.blunote.blunote;


import android.util.Log;

import com.drexelsp.blunote.BlunoteMessages.*;

/**
 * Created by scantwell on 2/16/2016.
 */
public class MediaPlayer implements MessageHandler {
    private final String TAG = "MediaPlayer";
    @Override
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message)
    {
        if (WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType()))
        {
        }
        else if (WrapperMessage.Type.SONG_REQUEST.equals(message.getType()))
        {
        }
        else
        {
            Log.v(TAG, "Undefined message.");
        }
        return false;
    }


    private void processMessage(DeliveryInfo dinfo, SongRequest request)
    {
    }

    private void processMessage(DeliveryInfo dinfo, SongFragment frag)
    {
    }
}
