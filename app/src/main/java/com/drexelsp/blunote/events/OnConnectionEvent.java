package com.drexelsp.blunote.events;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class OnConnectionEvent {

    public final String macAddress;

    public OnConnectionEvent(String macAddress) {
        this.macAddress = macAddress;
    }
}
