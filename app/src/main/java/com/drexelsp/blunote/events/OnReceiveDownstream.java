package com.drexelsp.blunote.events;

import com.drexelsp.blunote.blunote.BlunoteMessages;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class OnReceiveDownstream {
    private BlunoteMessages.NetworkPacket networkPacket;

    public OnReceiveDownstream(byte[] data) {
        try {
            this.networkPacket = BlunoteMessages.NetworkPacket.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            this.networkPacket = null;
        }
    }

    public BlunoteMessages.NetworkPacket getNetworkPacket() {
        return networkPacket;
    }
}
