package com.drexelsp.blunote.network;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;

import java.io.IOException;

/**
 * Created by stephencantwell on 4/25/16.
 */
public interface BlunoteInputStream  {

    NetworkPacket read() throws IOException;

    byte[] rawRead() throws IOException;
}
