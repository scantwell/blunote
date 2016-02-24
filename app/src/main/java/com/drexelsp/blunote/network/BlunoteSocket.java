package com.drexelsp.blunote.network;

import android.os.Message;

/**
 * Created by omnia on 2/12/16.
 *
 * Interface for BluNote Sockets
 * Currently supports BlunoteBluetoothSocket
 */
public interface BlunoteSocket {
    int numMessages();
    Message readMessage();
    boolean writeMessage(Message msg);
}
