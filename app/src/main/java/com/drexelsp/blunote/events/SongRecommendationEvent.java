package com.drexelsp.blunote.events;

/**
 * Created by omnia on 3/14/16.
 */
public class SongRecommendationEvent {
    public final String songId;
    public final String song;
    public final String artist;
    public final String album;
    public final String owner;



    public SongRecommendationEvent(String songId, String song, String artist, String album, String owner) {
        this.songId = songId;
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.owner = owner;
    }
}
