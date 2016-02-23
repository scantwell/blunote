package com.drexelsp.blunote.blunote;


import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongRequest;
import com.drexelsp.blunote.blunote.BlunoteMessages.WrapperMessage;

/**
 * Created by scantwell on 2/16/2016.
 * Implements all media functionality of the Blunote application and handles all Network messages
 * from which SongRequests and SongFragments are acknowledged and generated respectively.
 */
public class MediaPlayer implements MessageHandler {
    private final String TAG = "MediaPlayer";

    @Override
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message) {
        if (WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType())) {
        } else if (WrapperMessage.Type.SONG_REQUEST.equals(message.getType())) {
        } else {
            Log.v(TAG, "Undefined message.");
        }
        return false;
    }


    private void processMessage(DeliveryInfo dinfo, SongRequest request) {
    }

    private void processMessage(DeliveryInfo dinfo, SongFragment frag) {
    }
}
