package com.drexelsp.blunote.network;

import com.drexelsp.blunote.blunote.BlunoteMessages;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Created by stephencantwell on 4/25/16.
 */
public interface BlunoteOutputStream {

    int write(BlunoteMessages.NetworkPacket networkPacket) throws IOException;

    int write(ByteString data) throws IOException;

    int write(byte[] data) throws IOException;
}
