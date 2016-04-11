package com.drexelsp.blunote.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by scantwell on 3/10/2016.
 */
public final class MetaStoreContract {
    public static final String AUTHORITY = "com.drexelsp.blunote.blunote.metastore";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Track implements CommonColumns {
        public static final String DURATION = "duration";
        public static final String TITLE = "title";
        public static final String TRACK_NO = "track";
        public static final String YEAR = "year";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(MetaStoreContract.CONTENT_URI, "track");
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_track";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_track";
        public static final String[] PROJECTION_ALL =
                {_ID, ALBUM, ARTIST, DURATION, SONG_ID, TITLE, TRACK_NO, YEAR};
        public static final String SORT_ORDER_DEFAULT = TITLE + " ASC";
    }

    public static final class Artist implements CommonColumns {
        public static final String NUMBER_OF_ALBUMS = "number_of_albums";
        public static final String NUMBER_OF_TRACKS = "number_of_tracks";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(MetaStoreContract.CONTENT_URI, "artist");
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_artist";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_artist";
        public static final String[] PROJECTION_ALL =
                {_ID, ARTIST, NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS};
        public static final String SORT_ORDER_DEFAULT = ARTIST + " ASC";
    }

    public static final class Album implements CommonColumns {
        public static final String ALBUM_ART = "album_art";
        public static final String FIRST_YEAR = "first_year";
        public static final String LAST_YEAR = "last_year";
        public static final String NUMBER_OF_SONGS = "number_of_songs";
        public static final String NUMBER_OF_SONGS_FOR_ARTIST = "number_of_songs_for_artist";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(MetaStoreContract.CONTENT_URI, "album");
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_album";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_album";
        public static final String[] PROJECTION_ALL =
                {_ID, ALBUM, ALBUM_ART, ARTIST, FIRST_YEAR, LAST_YEAR, NUMBER_OF_SONGS, NUMBER_OF_SONGS_FOR_ARTIST};
        public static final String SORT_ORDER_DEFAULT = ALBUM + " ASC";
    }

    public static final class User implements BaseColumns
    {
        public static final String USERNAME = "username";
        public static final String USER_ID = "user_id";
        public static final String LATENCY = "latency";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(MetaStoreContract.CONTENT_URI, "user");
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_user";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_user";
        public static final String[] PROJECTION_ALL =
                {_ID, USERNAME, USER_ID, LATENCY};
        public static final String SORT_ORDER_DEFAULT = USERNAME + " ASC";
    }

    public static final class UserTracks implements BaseColumns
    {
        public static final String USER_ID = "user_id";
        public static final String TITLE = "title";
        public static final String ALBUM = "album";
        public static final String ARTIST = "artist";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(MetaStoreContract.CONTENT_URI, "user_tracks");
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_user_tracks";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.com.drexelsp.blunote.blunote.metastore_user_tracks";
        public static final String[] PROJECTION_ALL =
                {_ID, USER_ID, TITLE, ALBUM, ARTIST};
        public static final String SORT_ORDER_DEFAULT = USER_ID + " ASC";
    }

    public interface CommonColumns extends BaseColumns {
        String ALBUM = "album";
        String ARTIST = "artist";
        String SONG_ID = "song_id";
    }
}
