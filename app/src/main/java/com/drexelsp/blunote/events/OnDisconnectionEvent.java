package com.drexelsp.blunote.events;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class OnDisconnectionEvent {

    public final String macAddress;

    public OnDisconnectionEvent(String macAddress) {
        this.macAddress = macAddress;
    }
}

