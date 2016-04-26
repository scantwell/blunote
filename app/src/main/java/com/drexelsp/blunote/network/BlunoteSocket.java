package com.drexelsp.blunote.network;

import java.io.IOException;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * Interface for BluNote Sockets
 * Currently supports BlunoteBluetoothSocket
 */
public interface BlunoteSocket {
    BlunoteInputStream getInputStream() throws IOException;
    BlunoteOutputStream getOutputStream() throws IOException;
    String getAddress();
    void close() throws IOException;
}
