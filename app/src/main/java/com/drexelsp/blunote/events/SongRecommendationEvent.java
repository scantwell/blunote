package com.drexelsp.blunote.events;

/**
 * Created by omnia on 3/14/16.
 */
public class SongRecommendationEvent {
    public final String songId;
    public final String owner;


    public SongRecommendationEvent(String songId, String owner) {
        this.songId = songId;
        this.owner = owner;
    }
}
