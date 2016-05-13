package com.drexelsp.blunote.events;

import com.drexelsp.blunote.blunote.Song;

import java.util.ArrayList;

/**
 * Created by scantwell on 5/12/2016.
 */
public class PlaylistUpdateEvent {

    public ArrayList<String> playlist;

    public PlaylistUpdateEvent(ArrayList<Song> songs)
    {
        initPlaylist(songs);
    }

    private void initPlaylist(ArrayList<Song> songs)
    {
        for(Song s : songs)
        {
            this.playlist.add(String.format("Song: %s Artist: %s Album: %s", s.getTitle(), s.getArtist(), s.getAlbum()));
        }
    }
}
