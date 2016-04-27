package com.drexelsp.blunote.events;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class OnDisconnectionEvent {

    public final String macAddress;
    public int direction;
    public static int UPSTREAM = 1;
    public static int DOWNSTREAM = 2;

    public OnDisconnectionEvent(int direction, String macAddress) {
        this.macAddress = macAddress;
        this.direction = direction;
    }
}

