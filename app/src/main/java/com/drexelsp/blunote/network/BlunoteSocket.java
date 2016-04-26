package com.drexelsp.blunote.network;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * Interface for BluNote Sockets
 * Currently supports BlunoteBluetoothSocket
 */
public interface BlunoteSocket {
    BlunoteInputStream getInputStream();
    BlunoteOutputStream getOutputStream();
    String getAddress();
    void close();
}
