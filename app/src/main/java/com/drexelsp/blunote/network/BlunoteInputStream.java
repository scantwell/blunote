package com.drexelsp.blunote.network;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Created by stephencantwell on 4/25/16.
 */
public interface BlunoteInputStream  {

    NetworkPacket read() throws IOException;

    ByteString readByteString() throws IOException;

    byte[] rawRead() throws IOException;
}
