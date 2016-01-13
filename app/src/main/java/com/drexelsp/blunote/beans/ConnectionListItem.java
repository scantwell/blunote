package com.drexelsp.blunote.beans;

/**
 * List Item data container to view available connections on login screen.
 */
public class ConnectionListItem {
    private String connectionName;
    private String totalConnections;
    private String totalSongs;

    public String getConnectionName()
    {
        return connectionName;
    }

    public void setConnectionName(String connectionName)
    {
        this.connectionName = connectionName;
    }

    public String getTotalConnections()
    {
        return totalConnections;
    }

    public void setTotalConnections(String totalConnections)
    {
        this.totalConnections = totalConnections;
    }

    public String getTotalSongs()
    {
        return totalSongs;
    }

    public void setTotalSongs(String totalSongs)
    {
        this.totalSongs = totalSongs;
    }
}
