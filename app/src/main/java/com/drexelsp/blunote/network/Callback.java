package com.drexelsp.blunote.network;

/**
 * Created by stephencantwell on 4/26/16.
 */
public interface Callback {

    void onReceivePacket(byte[] data);
    void onReadFailure(BlunoteSocket socket);
}
