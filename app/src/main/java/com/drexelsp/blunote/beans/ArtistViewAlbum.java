package com.drexelsp.blunote.beans;

import android.graphics.Bitmap;

/**
 * Created by Brisbin on 3/14/2016.
 */
public class ArtistViewAlbum {
    private Bitmap albumArtwork;
    private String albumName;
    private String numberOfTracks;
    private String albumYear;

    public Bitmap getAlbumArtwork() {
        return albumArtwork;
    }

    public void setAlbumArtwork(Bitmap albumArtwork) {
        this.albumArtwork = albumArtwork;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getNumberOfTracks() {
        return numberOfTracks;
    }

    public void setNumberOfTracks(String numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
    }

    public String getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(String albumYear) {
        this.albumYear = albumYear;
    }
}
