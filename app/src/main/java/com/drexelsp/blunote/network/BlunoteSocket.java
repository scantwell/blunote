package com.drexelsp.blunote.network;

import android.os.Message;

import com.drexelsp.blunote.blunote.BlunoteMessages;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * Interface for BluNote Sockets
 * Currently supports BlunoteBluetoothSocket
 */
public interface BlunoteSocket {
    //int numMessages();

    //Message readMessage();

    boolean write(BlunoteMessages.NetworkPacket packet);
}
