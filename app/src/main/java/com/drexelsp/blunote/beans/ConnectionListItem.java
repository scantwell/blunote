package com.drexelsp.blunote.beans;

/**
 * List Item data container to view available connections on login screen.
 */
public class ConnectionListItem {
    private String connectionName;
    private int totalConnections;
    private int totalSongs;

    public String getConnectionName()
    {
        return connectionName;
    }

    public void setConnectionName(String connectionName)
    {
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
