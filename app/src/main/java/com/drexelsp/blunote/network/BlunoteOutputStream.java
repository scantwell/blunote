package com.drexelsp.blunote.network;

import com.drexelsp.blunote.network.NetworkMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Created by stephencantwell on 4/25/16.
 */
public interface BlunoteOutputStream {

    int write(NetworkPacket networkPacket) throws IOException;

    int write(ByteString data) throws IOException;

    int write(byte[] data) throws IOException;
}
