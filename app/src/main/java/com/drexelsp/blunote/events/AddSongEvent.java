package com.drexelsp.blunote.events;

import com.drexelsp.blunote.blunote.Song;

/**
 * Created by stephencantwell on 5/10/16.
 */
public class AddSongEvent {

    public String title = "";
    public String artist = "";
    public String album = "";

    public AddSongEvent(Song song)
    {
        title = song.getTitle();
        artist = song.getArtist();
        album = song.getAlbum();
    }
}
