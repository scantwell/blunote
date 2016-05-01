package com.drexelsp.blunote.events;

/**
 * Created by scantwell on 5/1/2016.
 */
public class PlaySongEvent {

    public String title = "";
    public String artist = "";
    public String album = "";
    public String duration = "0";

    public PlaySongEvent(String title, String artist, String album, String duration)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }
}
