package com.drexelsp.blunote.events;

/**
 * Created by omnia on 3/14/16.
 */
public class SongRecommendationEvent {
    public final String songId;
    public final String owner;

    /**
     * package song info to be requested
     * @param songId id of song
     * @param owner owner of the song
     */
    public SongRecommendationEvent(String songId, String owner) {
        this.songId = songId;
        this.owner = owner;
    }
}
