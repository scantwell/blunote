package com.drexelsp.blunote.events;

import com.drexelsp.blunote.blunote.Player;

/**
 * Created by scantwell on 5/1/2016.
 */
public class PlaySongEvent {

    public String title = "";
    public String artist = "";
    public String album = "";
    public String owner = "";
    public String duration = "0:00";
    public Player player;

    public PlaySongEvent(String title, String artist, String album, String owner, String duration, Player player) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.owner = owner;
        this.duration = duration;
        this.player = player;
    }
}
