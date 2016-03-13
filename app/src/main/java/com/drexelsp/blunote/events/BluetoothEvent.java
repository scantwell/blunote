package com.drexelsp.blunote.events;

/**
 * Created by omnia on 3/12/16.
 */
public class BluetoothEvent {
    static public final int ERROR = 0;
    static public final int CONNECTOR = 1;
    static public final int SERVER_LISTENER = 2;

    public final int event;
    public final boolean success;
    public final String macAddress;

    public BluetoothEvent(int event, boolean success, String macAddress) {
        this.event = event;
        this.success = success;
        this.macAddress = macAddress;
    }
}
