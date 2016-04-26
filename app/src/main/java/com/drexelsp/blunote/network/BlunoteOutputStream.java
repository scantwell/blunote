package com.drexelsp.blunote.network;

import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Created by stephencantwell on 4/25/16.
 */
public interface BlunoteOutputStream {

    int write(ByteString data) throws IOException;
}
