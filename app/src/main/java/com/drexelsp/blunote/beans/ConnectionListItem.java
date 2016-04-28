package com.drexelsp.blunote.beans;

import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkMap;
import com.drexelsp.blunote.blunote.BlunoteMessages.WelcomePacket;

/**
 * List Item data container to view available connections on login screen.
 */
public class ConnectionListItem {
    private String connectionName;
    private int totalConnections;
    private int totalSongs;

    private NetworkMap networkMap;

    public ConnectionListItem() { }

    public ConnectionListItem(NetworkMap networkMap, WelcomePacket welcomePacket) {
        this.connectionName = welcomePacket.getNetworkName();
        this.totalConnections = Integer.parseInt(welcomePacket.getNumUsers());
        this.totalSongs = Integer.parseInt(welcomePacket.getNumSongs());
        this.networkMap = networkMap;
    }

    public NetworkMap getNetworkMap() {
        return this.networkMap;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public void setTotalConnections(int totalConnections) {
        this.totalConnections = totalConnections;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }
}
