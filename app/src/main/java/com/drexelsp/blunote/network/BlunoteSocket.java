package com.drexelsp.blunote.network;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import java.io.IOException;

/**
 * Created by omnia on 2/12/16.
 * <p/>
 * Interface for BluNote Sockets
 * Currently supports BlunoteBluetoothSocket
 */
public interface BlunoteSocket {
    NetworkPacket read() throws IOException;
    boolean write(NetworkPacket packet) throws IOException;
}
