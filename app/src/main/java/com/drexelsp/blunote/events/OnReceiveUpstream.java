package com.drexelsp.blunote.events;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class OnReceiveUpstream {

    private NetworkPacket networkPacket;

    public OnReceiveUpstream(byte[] data) {
        try {
            this.networkPacket = NetworkPacket.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            this.networkPacket = null;
        }
    }

    public NetworkPacket getNetworkPacket() {
        return networkPacket;
    }
}