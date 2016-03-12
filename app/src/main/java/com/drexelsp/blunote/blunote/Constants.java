package com.drexelsp.blunote.blunote;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * A class to hold all constants necessary for the java code.
 */
public final class Constants {

    private Constants() {
    }

    //ViewFlipper IDs
    public static final int ACTIVITY_LOGIN = 0;
    public static final int ACTIVITY_NETWORK_SETTINGS = 1;
    public static final int ACTIVITY_PREFERENCES = 2;
    public static final int ACTIVITY_MEDIA_LIST = 3;
    public static final int ACTIVITY_ALBUM_VIEW = 4;
    public static final int ACTIVITY_ARTIST_VIEW = 5;
    public static final int ACTIVITY_SONG_VIEW = 6;
    public static final int ACTIVITY_PLAYLIST_VIEW = 7;
    public static final int ACTIVITY_MEDIA_PLAYER = 8;

    //App Bar Menu Item Locations
    public static final int MENU_ITEM_SONG_LIST = 0;
    public static final int MENU_ITEM_MEDIA_PLAYER = 1;
    public static final int MENU_ITEM_SEARCH = 2;
    public static final int MENU_ITEM_SETTINGS = 3;
    public static final int MENU_ITEM_NETWORK_SETTINGS = 4;
    public static final int MENU_ITEM_PREFERENCES = 5;

    //MediaStore String Constants
    //Albums
    public static final Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    public static final String ALBUM = MediaStore.Audio.Albums.ALBUM;
    public static final String ALBUM_ART = MediaStore.Audio.Albums.ALBUM_ART;
    public static final String ALBUM_ID = MediaStore.Audio.Albums._ID;
    public static final String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;
    public static final String FIRST_YEAR = MediaStore.Audio.Albums.FIRST_YEAR;
    public static final String LAST_YEAR = MediaStore.Audio.Albums.LAST_YEAR;
    public static final String NUM_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
    public static final String SORT_ALBUMS = ALBUM + " ASC";

    //Artists
    public static final Uri ARTIST_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    public static final String ARTIST = MediaStore.Audio.Artists.ARTIST;
    public static final String ARTIST_ID = MediaStore.Audio.Artists._ID;
    public static final String NUM_OF_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
    public static final String NUM_OF_TRACKS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;
    public static final String SORT_ARTISTS = ARTIST + " ASC";

    //Tracks
    public static final Uri TRACK_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static final String WHERE = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    public static final String TRACK_ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;
    public static final String TRACK_ARTIST_ID = MediaStore.Audio.Media.ARTIST_ID;
    public static final String DURATION = MediaStore.Audio.Media.DURATION;
    public static final String SONG_ID = MediaStore.Audio.Media._ID;
    public static final String TITLE = MediaStore.Audio.Media.TITLE;
    public static final String TRACK = MediaStore.Audio.Media.TRACK;
    public static final String YEAR = MediaStore.Audio.Media.YEAR;
    public static final String SORT_TRACK = TITLE + " ASC";

}
