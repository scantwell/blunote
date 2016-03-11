package com.drexelsp.blunote.provider;

import android.provider.BaseColumns;

/**
 * Created by scantwell on 3/10/2016.
 */
interface DbSchema {
    String DB_NAME = "metaStore.db";

    String TBL_ALBUM = "album";
    String TBL_ARTIST = "artist";
    String TBL_TRACK = "track";

    String COL_ID = BaseColumns._ID;
    String COL_ALBUM = "album";
    String COL_ALBUM_ID = "album_id";
    String COL_ARTIST = "artist";
    String COL_ARTIST_ID = "artist_id";
    String COL_DURATION = "duration";
    String COL_FIRST_YEAR = "first_year";
    String COL_LAST_YEAR = "last_year";
    String COL_NUMBER_OF_ALBUMS = "number_of_albums";
    String COL_NUMBER_OF_SONGS = "number_of_songs";
    String COL_NUMBER_OF_SONGS_FOR_ARTIST = "number_of_songs_for_artist";
    String COL_NUMBER_OF_TRACKS = "number_of_tracks";
    String COL_TITLE = "title";
    String COL_TRACK = "track";
    String COL_YEAR = "year";

    String DDL_CREATE_TBL_TRACK =
            "CREATE TABLE track (" +
            "_id            INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "album_id       INTEGER,\n" +
            "artist_id      INTEGER, \n" +
            "duration       TEXT, \n" +
            "song_id        INTEGER, \n" +
            "title          TEXT, \n" +
            "track          INTEGER, \n" +
            "year           TEXT \n" +
            ")";

    String DDL_CREATE_TBL_ARTIST =
            "CREATE TABLE artist (" +
            "_id                    INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "artist                 TEXT, \n" +
            "number_of_albums       INTEGER, \n" +
            "number_of_tracks       INTEGER \n" +
            ")";

    String DDL_CREATE_TBL_ALBUM =
            "CREATE TABLE album (" +
            "_id                            INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "album                          TEXT, \n" +
            "album_art                      TEXT, \n" +
            "album_id                       INTEGER, \n" +
            "artist                         TEXT, \n" +
            "first_year                     TEXT, \n" +
            "last_year                      TEXT, \n" +
            "number_of_songs                TEXT, \n" +
            "number_of_songs_for_artist     TEXT \n" +
            ")";

    String DDL_DROP_TBL_TRACK =
            "DROP TABLE IF EXISTS track";

    String DDL_DROP_TBL_ARTIST =
            "DROP TABLE IF EXISTS artist";

    String DDL_DROP_TBL_ALBUMS =
            "DROP TABLE IF EXISTS album";
}
